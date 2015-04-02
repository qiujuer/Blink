package net.qiujuer.blink;

import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.core.ReceiveParser;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.File;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executor;

public class Blink {
    /**
     * Default on-disk resource directory.
     */
    private static final String DEFAULT_RESOURCE_DIR = "blink";
    /**
     * Default buffer size
     */
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 4 * 1024 * 1024;

    /**
     * Create a Bink connection by socket
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
        File rootDir = new File(resourcePath);
        DiskResource resource = new DiskResource(rootDir, fileMark);
        ReceiveParser parser = new ReceiveParser(resource);
        SocketAdapter socketAdapter = new SocketAdapter(socket, socketBufferSize, parser);
        ExecutorDelivery delivery = new ExecutorDelivery(executor, listener);
        return new BlinkConn(socketAdapter, delivery, socketAdapter, delivery, resource);
    }

    /**
     * Create a Bink connection by socket
     *
     * @param socket       Socket
     * @param resourcePath File Resource Path
     * @param fileMark     File name mark to clear
     * @param executor     Executor to notify callback
     * @param listener     ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, String resourcePath, String fileMark, Executor executor, ReceiveListener listener) throws Exception {
        return newConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, fileMark, executor, listener);
    }

    /**
     * Create a Bink connection by socket
     *
     * @param socket           Socket
     * @param socketBufferSize Socket BufferSize
     * @param resourcePath     File Resource Path
     * @param executor         Executor to notify callback
     * @param listener         ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, int socketBufferSize, String resourcePath, Executor executor, ReceiveListener listener) throws Exception {
        return newConnection(socket, socketBufferSize, resourcePath, UUID.randomUUID().toString(), executor, listener);
    }

    /**
     * Create a Bink connection by socket
     *
     * @param socket       Socket
     * @param resourcePath File Resource Path
     * @param executor     Executor to notify callback
     * @param listener     ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, String resourcePath, Executor executor, ReceiveListener listener) throws Exception {
        return newConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, UUID.randomUUID().toString(), executor, listener);
    }

    /**
     * Create a Bink connection by socket
     *
     * @param socket   Socket
     * @param executor Executor to notify callback
     * @param listener ReceiveListener
     * @return BlinkConn
     * @throws Exception
     */
    public static BlinkConn newConnection(Socket socket, Executor executor, ReceiveListener listener) throws Exception {
        return newConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, DEFAULT_RESOURCE_DIR, UUID.randomUUID().toString(), executor, listener);
    }
}
