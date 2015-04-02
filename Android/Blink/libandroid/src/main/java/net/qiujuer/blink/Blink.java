package net.qiujuer.blink;

import android.os.Environment;

import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.core.ReceiveParser;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.File;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executor;

public class Blink {
    /**
     * Default sd-disk resource directory.
     */
    private static final String DEFAULT_RESOURCE_DIR = "blink";
    /**
     * Default socket buffer size
     */
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 4 * 1024 * 1024;

    /**
     * Create a Bink connection by socket with custom executor callback
     *
     * @param socket           Socket
     * @param socketBufferSize Socket BufferSize
     * @param resourcePath     File Resource Path
     * @param fileMark         File name mark to clear
     * @param executor         Executor to notify callback
     * @param listener         ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, int socketBufferSize, String resourcePath, String fileMark, Executor executor, ReceiveListener listener) throws Exception {
        File rootDir = new File(resourcePath, DEFAULT_RESOURCE_DIR);
        DiskResource resource = new DiskResource(rootDir, fileMark);
        ReceiveParser parser = new ReceiveParser(resource);
        SocketAdapter socketAdapter = new SocketAdapter(socket, socketBufferSize, parser);
        ExecutorDelivery delivery = new ExecutorDelivery(executor, listener);
        return new BlinkConn(socketAdapter, delivery, socketAdapter, delivery, resource);
    }

    /**
     * Create a Bink connection by socket with main thread callback
     *
     * @param socket       Socket
     * @param resourcePath File Resource Path
     * @param fileMark     File name mark to clear
     * @param listener     ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, int socketBufferSize, String resourcePath, String fileMark, ReceiveListener listener) throws Exception {
        File rootDir = new File(resourcePath, DEFAULT_RESOURCE_DIR);
        DiskResource resource = new DiskResource(rootDir, fileMark);
        ReceiveParser parser = new ReceiveParser(resource);
        SocketAdapter socketAdapter = new SocketAdapter(socket, socketBufferSize, parser);
        return new BlinkConn(socketAdapter, resource, listener);
    }

    /**
     * Create a Bink connection by socket with main thread callback
     *
     * @param socket       Socket
     * @param resourcePath File Resource Path
     * @param listener     ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, String resourcePath, ReceiveListener listener) throws Exception {
        return newConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, UUID.randomUUID().toString(), listener);
    }

    /**
     * Create a Bink connection by socket with main thread callback
     *
     * @param socket   Socket
     * @param listener ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, ReceiveListener listener) throws Exception {
        return newConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, getDefaultResourcePath(), UUID.randomUUID().toString(), listener);
    }


    /**
     * Get the app sd card path
     *
     * @return SD path
     */
    private static String getDefaultResourcePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            return path.getPath();
        }
        throw new NullPointerException("ExternalStorage path is null.");
    }
}
