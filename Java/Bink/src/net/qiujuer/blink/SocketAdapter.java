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

import net.qiujuer.blink.core.ReceiveDelivery;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.core.ReceiveParser;
import net.qiujuer.blink.core.Receiver;
import net.qiujuer.blink.core.SendDelivery;
import net.qiujuer.blink.core.SendPacket;
import net.qiujuer.blink.core.Sender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;

/**
 * Socket adapter
 */
public class SocketAdapter implements Sender, Receiver {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private final int mBufferSize;

    private InputStream mIn;
    private OutputStream mOut;

    private byte[] mInBuffer;
    private byte[] mOutBuffer;

    /**
     * Receive Parser to create entity
     */
    private final ReceiveParser mParser;

    public SocketAdapter(Socket socket, int bufferSize, ReceiveParser parser)
            throws IOException {
        mIn = socket.getInputStream();
        mOut = socket.getOutputStream();

        mBufferSize = bufferSize;

        mInBuffer = new byte[bufferSize];
        mOutBuffer = new byte[bufferSize];

        mParser = parser;
    }

    /**
     * Receive socket head
     *
     * @return ReceiveEntity
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ReceivePacket<?> receiveHead() {
        try {
            int type = mIn.read();
            if (type != -1) {
                byte[] lenByte = new byte[4];
                mIn.read(lenByte);
                int len = convertToInt(lenByte);
                ReceivePacket<?> entity = mParser.parseReceive(type, len);
                if (entity == null)
                    receiveRedundancy();
                return entity;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Receive socket entity
     *
     * @param entity   ReceiveEntity
     * @param delivery ReceiveDelivery
     * @return Status
     */
    @Override
    public boolean receiveEntity(ReceivePacket<?> entity,
                                 ReceiveDelivery delivery) {
        OutputStream out = entity.getOutputStream();
        MessageDigest md5Verification = null;
        int surplusLen = entity.getLength();
        int cursor = 0;
        try {
            md5Verification = MessageDigest.getInstance("MD5");
            int readLen;
            while (surplusLen > 0) {
                // Read
                if (surplusLen > mBufferSize)
                    readLen = mIn.read(mInBuffer);
                else
                    readLen = mIn.read(mInBuffer, 0, surplusLen);

                // Write
                out.write(mInBuffer, 0, readLen);
                out.flush();
                md5Verification.update(mInBuffer, 0, readLen);

                // Compute surplusLen
                surplusLen -= readLen;
                cursor += readLen;

                // Post progress
                delivery.postReceiveProgress(entity, entity.getLength(), cursor);
            }
            return true;
        } catch (Exception e) {
            receiveRedundancy();
        } finally {
            closeOutStream(out);
            if (md5Verification != null) {
                entity.setHashCode(getMD5String(md5Verification.digest()));
            }
        }
        return false;
    }

    /**
     * Receive socket redundancy data
     */
    public void receiveRedundancy() {
        try {
            while (true) {
                if (mIn.read(mInBuffer) <= 0)
                    return;
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Send socket head
     *
     * @param entity SendEntity
     * @return Status
     */
    @Override
    public boolean sendHead(SendPacket entity) {
        int length = entity.getLength();
        if (length <= 0)
            return false;
        try {
            byte[] lenBytes = convertToBytes(length);
            // Send Type
            mOut.write(entity.getType());
            // Send Length
            mOut.write(lenBytes, 0, 4);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send socket entity
     *
     * @param entity   SendEntity
     * @param delivery SendDelivery
     * @return Status
     */
    @Override
    public boolean sendEntity(SendPacket entity, SendDelivery delivery) {
        int cursor = 0;
        int total = entity.getLength();
        InputStream in = entity.getInputStream();
        int count;
        try {
            while ((count = in.read(mOutBuffer)) != -1) {
                // Write
                mOut.write(mOutBuffer, 0, count);
                cursor += count;

                // Post progress
                delivery.postSendProgress(entity, total, cursor);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInputStream(in);
        }
        return false;
    }

    /**
     * Destroy IO
     */
    @Override
    public void destroyReceiveIO() {
        try {
            mIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mIn = null;
        }
        mInBuffer = null;
    }

    /**
     * Destroy IO
     */
    @Override
    public void destroySendIO() {
        try {
            mOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mOut = null;
        }
        mOutBuffer = null;
    }

    /**
     * Close the input stream
     *
     * @param in InputStream
     */
    public static void closeInputStream(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the out stream
     *
     * @param out OutputStream
     */
    public static void closeOutStream(OutputStream out) {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert int to byte[]
     */
    static byte[] convertToBytes(int n) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xff & n);
        bytes[1] = (byte) ((0xff00 & n) >> 8);
        bytes[2] = (byte) ((0xff0000 & n) >> 16);
        bytes[3] = (byte) ((0xff000000 & n) >> 24);
        return bytes;
    }

    static byte[] convertToBytes_n(int n) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (n & 0xff);
        bytes[1] = (byte) ((n >> 8) & 0xff);
        bytes[2] = (byte) ((n >> 16) & 0xff);
        bytes[3] = (byte) ((n >> 24) & 0xff);
        return bytes;
    }

    /**
     * Convert byte[] to int
     */
    static int convertToInt(byte[] bytes) {
        int n = bytes[0] & 0xFF;
        n |= ((bytes[1] << 8) & 0xFF00);
        n |= ((bytes[2] << 16) & 0xFF0000);
        n |= ((bytes[3] << 24) & 0xFF000000);
        return n;
    }

    static int convertToInt_n(byte[] bytes) {
        int n = 0;
        n |= (bytes[0]);
        n |= (bytes[1] << 8);
        n |= (bytes[2] << 16);
        n |= (bytes[3] << 24);
        return n;
    }

    static byte[] writeLong(long n) {
        byte[] bytes = new byte[8];
        bytes[0] = ((byte) (n));
        bytes[1] = ((byte) (n >>> 8));
        bytes[2] = ((byte) (n >>> 16));
        bytes[3] = ((byte) (n >>> 24));
        bytes[4] = ((byte) (n >>> 32));
        bytes[5] = ((byte) (n >>> 40));
        bytes[6] = ((byte) (n >>> 48));
        bytes[7] = ((byte) (n >>> 56));
        return bytes;
    }

    static long readLong(byte[] bytes) {
        long n = 0;
        n |= ((bytes[0] & 0xFFL));
        n |= ((bytes[1] & 0xFFL) << 8);
        n |= ((bytes[2] & 0xFFL) << 16);
        n |= ((bytes[3] & 0xFFL) << 24);
        n |= ((bytes[4] & 0xFFL) << 32);
        n |= ((bytes[5] & 0xFFL) << 40);
        n |= ((bytes[6] & 0xFFL) << 48);
        n |= ((bytes[7] & 0xFFL) << 56);
        return n;
    }

    public static String getMD5String(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte a : b) {
            sb.append(HEX_DIGITS[(a & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[a & 0x0f]);
        }
        return sb.toString();
    }
}
