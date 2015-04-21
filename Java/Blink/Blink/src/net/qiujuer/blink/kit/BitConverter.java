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
package net.qiujuer.blink.kit;

/**
 * Bit Converter
 */
public class BitConverter {

    /**
     * Convert char to byte[]
     *
     * @param x char
     * @return bytes
     */
    public static byte[] toBytes(char x) {
        return toBytes(x, new byte[2], 0);
    }

    /**
     * Convert char to byte[]
     *
     * @param x       char
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(char x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos] = (byte) (x >> 8);
        return bytes;
    }


    /**
     * Convert short to byte[]
     *
     * @param x Short
     * @return bytes
     */
    public static byte[] toBytes(short x) {
        return toBytes(x, new byte[2], 0);
    }

    /**
     * Convert short to byte[]
     *
     * @param x       Short
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(short x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos] = (byte) (x >> 8);
        return bytes;
    }

    /**
     * Convert int to byte[]
     *
     * @param x int
     * @return bytes
     */
    public static byte[] toBytes(int x) {
        return toBytes(x, new byte[4], 0);
    }

    /**
     * Convert int to byte[]
     *
     * @param x       int
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(int x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos++] = (byte) (x >> 8);
        bytes[bytePos++] = (byte) (x >> 16);
        bytes[bytePos] = (byte) (x >> 24);
        return bytes;
    }

    /**
     * Convert long to byte[]
     *
     * @param x long
     * @return bytes
     */
    public static byte[] toBytes(long x) {
        return toBytes(x, new byte[8], 0);
    }

    /**
     * Convert long to byte[]
     *
     * @param x       long
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(long x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos++] = (byte) (x >> 8);
        bytes[bytePos++] = (byte) (x >> 16);
        bytes[bytePos++] = (byte) (x >> 24);
        bytes[bytePos++] = (byte) (x >> 32);
        bytes[bytePos++] = (byte) (x >> 40);
        bytes[bytePos++] = (byte) (x >> 48);
        bytes[bytePos] = (byte) (x >> 56);
        return bytes;
    }

    /**
     * Convert byte[] to char
     *
     * @param bytes bytes
     * @return char
     */
    public static char toChar(byte[] bytes) {
        return toChar(bytes, 0);
    }

    /**
     * Convert byte[] to char
     *
     * @param bytes bytes
     * @param index byte start index
     * @return char
     */
    public static char toChar(byte[] bytes, int index) {
        return (char) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
    }

    /**
     * Convert byte[] to short
     *
     * @param bytes bytes
     * @return short
     */
    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }

    /**
     * Convert byte[] to short
     *
     * @param bytes bytes
     * @param index byte start index
     * @return short
     */
    public static short toShort(byte[] bytes, int index) {
        return (short) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
    }

    /**
     * Convert byte[] to int
     *
     * @param bytes bytes
     * @return int
     */
    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }

    /**
     * Convert byte[] to int
     *
     * @param bytes bytes
     * @param index bytes start index
     * @return int
     */
    public static int toInt(byte[] bytes, int index) {
        return (((bytes[index + 3]) << 24) |
                ((bytes[index + 2] & 0xff) << 16) |
                ((bytes[index + 1] & 0xff) << 8) |
                ((bytes[index] & 0xff)));
    }

    /**
     * Convert byte[] to long
     *
     * @param bytes bytes
     * @return long
     */
    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }

    /**
     * Convert byte[] to long
     *
     * @param bytes bytes
     * @param index bytes start index
     * @return long
     */
    public static long toLong(byte[] bytes, int index) {
        return ((((long) bytes[index + 7]) << 56) |
                (((long) bytes[index + 6] & 0xff) << 48) |
                (((long) bytes[index + 5] & 0xff) << 40) |
                (((long) bytes[index + 4] & 0xff) << 32) |
                (((long) bytes[index + 3] & 0xff) << 24) |
                (((long) bytes[index + 2] & 0xff) << 16) |
                (((long) bytes[index + 1] & 0xff) << 8) |
                (((long) bytes[index] & 0xff)));
    }
}
