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

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import net.qiujuer.blink.async.AsyncEventArgs;
import net.qiujuer.blink.async.AsyncSocketAdapter;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.kit.BlinkParserImpl;
import net.qiujuer.blink.kit.DiskResource;
import net.qiujuer.blink.kit.ExecutorDelivery;
import net.qiujuer.blink.listener.BlinkListener;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.File;
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
     * If executor is null blink will create a Executors.newSingleThreadExecutor() {@link Executors#newSingleThreadExecutor()} to notify
     *
     * @param channel           A socket link SocketChannel {@link SocketChannel}
     * @param bufferSize        SocketChannel data transmission bufferSize ,The use init a SocketAsyncEventArgs {@link AsyncEventArgs}
     * @param resourcePath      Receive file path
     * @param fileMark          Receive file name mark
     * @param executor          A Executor {@link Executor} to notify receive or send post
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
     *
     * @param channel           A socket link SocketChannel {@link SocketChannel}
     * @param bufferSize        SocketChannel data transmission bufferSize ,The use init a SocketAsyncEventArgs {@link AsyncEventArgs}
     * @param resourcePath      Receive file path
     * @param fileMark          Receive file name mark
     * @param handler           Android Handler {@link Handler} to notify receive or send post
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
            final Handler handler,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {

        // Create a  handler executor poster
        Executor poster = new Executor() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };

        return newConnection(channel, bufferSize, resourcePath, fileMark, poster, progressPrecision, receiveListener, blinkListener);
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     *
     * @param channel           A socket link SocketChannel {@link SocketChannel}
     * @param resourcePath      Receive file path
     * @param fileMark          Receive file name mark
     * @param handler           Android Handler {@link Handler} to notify receive or send post
     * @param progressPrecision Notify send and receive progress min change value, The scope of 0~1 float value
     * @param receiveListener   Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener     Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            String resourcePath,
            String fileMark,
            Handler handler,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        return newConnection(channel, DEFAULT_BUFFER_SIZE, resourcePath, fileMark, handler, progressPrecision, receiveListener, blinkListener);
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     * Use UUID{@link UUID} to file mark
     * Use default progress precision {0.001f}
     * Use Main Handler to notify {@link Looper#getMainLooper()}
     *
     * @param channel         A socket link SocketChannel {@link SocketChannel}
     * @param receiveListener Receiver notify on start/run/end receive {@link ReceiveListener}
     * @param blinkListener   Blink notify on channel disconnect and close {@link BlinkListener}
     * @return Bink connection {@link BlinkConn}, if error return null
     */
    public static BlinkConn newConnection(
            SocketChannel channel,
            String fileMark,
            ReceiveListener receiveListener,
            BlinkListener blinkListener) {
        return newConnection(channel, fileMark, UUID.randomUUID().toString(), new Handler(Looper.getMainLooper()), DEFAULT_PROGRESS_PRECISION, receiveListener, blinkListener);
    }

    /**
     * Create a Bink connection by socket {@link BlinkConn}
     * Use default buffer size {4*1024*1024}
     * Use the current program is running directory to file path
     * Use UUID{@link UUID} to file mark
     * Use default progress precision {0.001f}
     * Use Main Handler to notify {@link Looper#getMainLooper()}
     * Use the android ExternalStorage path to blink path
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
        return newConnection(channel, getDefaultResourcePath(), receiveListener, blinkListener);
    }

    /**
     * Get Default path with ExternalStorage
     *
     * @return Path
     */
    private static String getDefaultResourcePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            return path.getPath();
        }
        throw new NullPointerException("ExternalStorage path is null.");
    }
}
