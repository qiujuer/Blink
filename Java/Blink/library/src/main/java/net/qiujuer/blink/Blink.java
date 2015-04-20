/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/19/2015
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

import net.qiujuer.blink.async.AsyncEventArgs;
import net.qiujuer.blink.async.AsyncSocketAdapter;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.kit.BlinkParserImpl;
import net.qiujuer.blink.kit.DiskResource;
import net.qiujuer.blink.kit.ExecutorDelivery;
import net.qiujuer.blink.listener.BlinkListener;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Blink {
    // Default on-disk resource directory.
    private static final String DEFAULT_RESOURCE_DIR = "Blink";
    // Default buffer size
    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;
    // Default progress precision
    public static final float DEFAULT_PROGRESS_PRECISION = 0.001F;


    /**
     * Create a Bink connection by socket {@link BlinkConn}
     *
     * @param channel           A socket link SocketChannel {@link SocketChannel}
     * @param bufferSize        SocketChannel data transmission bufferSize ,The use init a SocketAsyncEventArgs {@link AsyncEventArgs}
     * @param resourcePath      Receive file path
     * @param fileMark          Receive file name mark
     * @param executor          A Executor {@link Executor} Use notify receive or send post
     * @param progressPrecision Notify send and receive progress min change value, The scope of 0~1 float value
     * @param receiveListener   Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener     Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            int bufferSize,
            String resourcePath,
            String fileMark,
            Executor executor,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        try {

            if (channel == null
                    || bufferSize <= 0
                    || resourcePath == null
                    || resourcePath.length() <= 0
                    || fileMark == null
                    || fileMark.length() <= 0
                    || progressPrecision <= 0
                    || progressPrecision >= 1)
                throw new Exception("The input parameter is illegal.");


            File path = new File(resourcePath, DEFAULT_RESOURCE_DIR);
            DiskResource resource = new DiskResource(path, fileMark);
            BlinkParserImpl parser = new BlinkParserImpl(resource);
            ExecutorDelivery delivery = new ExecutorDelivery(executor, blinkListener, receiveListener);
            AsyncSocketAdapter socketAdapter = new AsyncSocketAdapter(channel, bufferSize, delivery);
            return new BlinkConn(socketAdapter, socketAdapter, delivery, delivery, delivery, resource, parser, progressPrecision);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     *
     * @param channel           A socket link SocketChannel {@link SocketChannel}
     * @param resourcePath      Receive file path
     * @param fileMark          Receive file name mark
     * @param executor          A Executor {@link Executor} Use notify receive or send post
     * @param progressPrecision Notify send and receive progress min change value, The scope of 0~1 float value
     * @param receiveListener   Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener     Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            String resourcePath,
            String fileMark,
            Executor executor,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        return newConnection(channel, DEFAULT_BUFFER_SIZE, resourcePath, fileMark, executor, progressPrecision, receiveListener, blinkListener);
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     * Use the current program is running directory to file path
     * Use UUID{@link UUID} to file mark
     * Use default progress precision {0.001f}
     *
     * @param channel         A socket link SocketChannel {@link SocketChannel}
     * @param executor        A Executor {@link Executor} Use notify receive or send post
     * @param receiveListener Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener   Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            Executor executor,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        return newConnection(channel, getDefaultResourcePath(), UUID.randomUUID().toString(), executor, DEFAULT_PROGRESS_PRECISION, receiveListener, blinkListener);
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     * Use the current program is running directory to file path
     * Use UUID{@link UUID} to file mark
     * Use default progress precision {0.001f}
     * Use a SingleThreadExecutor to notify {@link Executors}
     *
     * @param channel         A socket link SocketChannel {@link SocketChannel}
     * @param receiveListener Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener   Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        return newConnection(channel, null, receiveListener, blinkListener);
    }

    /**
     * Get Default path with CanonicalPath
     *
     * @return Path
     */
    private static String getDefaultResourcePath() {
        File path = new File("");
        try {
            return path.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
