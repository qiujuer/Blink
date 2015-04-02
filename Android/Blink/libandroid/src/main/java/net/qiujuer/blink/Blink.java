/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/31/2015
 * Changed 04/02/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.blink;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

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
        // Create a Handler with main thread callback
        ExecutorDelivery delivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()), listener);
        return new BlinkConn(socketAdapter, delivery, socketAdapter, delivery, resource);
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
