/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 04/16/2015
 * Changed 04/25/2015
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
package net.qiujuer.blink.async;

import net.qiujuer.blink.kit.Disposable;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Selector dispatcher manager
 * This can dispatcher sender and receiver by async
 */
public class SelectorFactory implements Disposable {
    // Instance
    private static SelectorFactory instance;

    // Class
    private boolean isRunning = false;
    private final AtomicBoolean mRegRead = new AtomicBoolean(false);
    private final AtomicBoolean mRegWrite = new AtomicBoolean(false);
    private final Selector mReadSelector;
    private final Selector mWriteSelector;
    private final HashMap<SelectionKey, HandleCallback> mReadRegisterMap = new HashMap<SelectionKey, HandleCallback>();
    private final HashMap<SelectionKey, HandleCallback> mWriteRegisterMap = new HashMap<SelectionKey, HandleCallback>();
    private final ExecutorService mHandlePool;

    public static SelectorFactory getInstance() {
        if (instance == null) {
            synchronized (SelectorFactory.class) {
                if (instance == null) {
                    try {
                        instance = new SelectorFactory();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    public static void tryDispose() {
        if (instance != null) {
            synchronized (SelectorFactory.class) {
                if (instance != null) {
                    if (instance.canDispose()) {
                        instance.dispose();
                        instance = null;
                    }
                }
            }
        }
    }

    SelectorFactory() throws IOException {
        isRunning = true;

        mReadSelector = Selector.open();
        mWriteSelector = Selector.open();

        int size = Runtime.getRuntime().availableProcessors();
        size = size < 2 ? 2 : (size > 4 ? size - 2 : size);
        mHandlePool = Executors.newFixedThreadPool(size);

        runRead();
        runWrite();
    }

    public SelectionKey registerSend(SocketChannel channel, HandleCallback callback) throws ClosedChannelException {
        SelectionKey key = null;

        synchronized (mRegWrite) {
            mRegWrite.set(true);

            mWriteSelector.wakeup();


            if (channel.isRegistered()) {
                key = channel.keyFor(mWriteSelector);
                if (key != null)
                    key.interestOps(key.readyOps() | SelectionKey.OP_WRITE);
            }

            if (key == null) {
                key = channel.register(mWriteSelector, SelectionKey.OP_WRITE);
                mWriteRegisterMap.put(key, callback);
            }

            mRegWrite.set(false);
            try {
                mRegWrite.notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    public SelectionKey registerReceive(SocketChannel channel, HandleCallback callback) throws ClosedChannelException {
        SelectionKey key = null;

        synchronized (mRegRead) {
            mRegRead.set(true);

            mReadSelector.wakeup();


            if (channel.isRegistered()) {
                key = channel.keyFor(mReadSelector);
                if (key != null)
                    key.interestOps(key.readyOps() | SelectionKey.OP_READ);
            }

            if (key == null) {
                key = channel.register(mReadSelector, SelectionKey.OP_READ);
                mReadRegisterMap.put(key, callback);
            }

            mRegRead.set(false);

            try {
                mRegRead.notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return key;
    }

    /**
     * Cancel a SocketChannel register
     *
     * @param channel SocketChannel
     */
    public void unRegister(SocketChannel channel) {
        if (channel.isRegistered()) {
            SelectionKey key = channel.keyFor(mReadSelector);
            if (key != null) {
                key.cancel();
                mReadRegisterMap.remove(key);
                mReadSelector.wakeup();
            }

            key = channel.keyFor(mWriteSelector);
            if (key != null) {
                key.cancel();
                mWriteRegisterMap.remove(key);
                mWriteSelector.wakeup();
            }

        }
    }

    private void runRead() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {

                        if (mReadSelector.select() == 0) {
                            waitReadRegister();
                            continue;
                        }

                        Set<SelectionKey> keys = mReadSelector.selectedKeys();
                        for (SelectionKey key : keys) {
                            if (key.isReadable()) {
                                handleRead(key);
                            }
                        }
                        keys.clear();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setName("Blink AsyncRead SelectorManager");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void runWrite() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {

                        if (mWriteSelector.select() == 0) {
                            waitWriteRegister();
                            continue;
                        }

                        Set<SelectionKey> keys = mWriteSelector.selectedKeys();
                        for (SelectionKey key : keys) {
                            if (key.isWritable()) {
                                handleWrite(key);
                            }
                        }
                        keys.clear();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setName("Blink AsyncWrite SelectorManager");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void waitWriteRegister() {
        synchronized (mRegWrite) {
            if (mRegWrite.get()) {
                try {
                    mRegWrite.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void waitReadRegister() {
        synchronized (mRegRead) {
            if (mRegRead.get()) {
                try {
                    mRegRead.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleWrite(final SelectionKey key) {
        // Cancel
        key.interestOps(key.readyOps() & ~SelectionKey.OP_WRITE);

        // Call
        HandleCallback callback = null;
        try {
            callback = mWriteRegisterMap.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (callback != null && !mHandlePool.isShutdown()) {
            final HandleCallback finalCallback = callback;
            mHandlePool.execute(new Runnable() {
                @Override
                public void run() {
                    finalCallback.handleSend(key);
                }
            });
        }
    }

    private void handleRead(final SelectionKey key) {
        // Cancel
        key.interestOps(key.readyOps() & ~SelectionKey.OP_READ);

        // Call
        HandleCallback callback = null;
        try {
            callback = mReadRegisterMap.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (callback != null && !mHandlePool.isShutdown()) {
            final HandleCallback finalCallback = callback;
            mHandlePool.execute(new Runnable() {
                @Override
                public void run() {
                    finalCallback.handleReceive(key);
                }
            });
        }
    }

    /**
     * This Manager isn't have call , manager is can dispose
     *
     * @return CanDispose
     */
    public boolean canDispose() {
        return mReadRegisterMap.isEmpty() && mWriteRegisterMap.isEmpty();
    }

    @Override
    public void dispose() {
        isRunning = false;

        mHandlePool.shutdownNow();

        mWriteSelector.wakeup();
        mReadSelector.wakeup();

        try {
            mWriteSelector.close();
            mReadSelector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mReadRegisterMap.clear();
        mWriteRegisterMap.clear();
    }

    /**
     * Handle The Socket Send  And Receive
     */
    public interface HandleCallback {
        void handleSend(SelectionKey key);

        void handleReceive(SelectionKey key);
    }
}
