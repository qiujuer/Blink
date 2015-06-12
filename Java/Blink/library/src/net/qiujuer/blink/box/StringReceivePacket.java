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
package net.qiujuer.blink.box;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * String receive class
 */
public class StringReceivePacket extends BaseReceivePacket<String> {
    public StringReceivePacket(long id, byte type, long len) {
        super(id, type, len);
    }

    @Override
    public boolean startPacket() {
        try {
            mStream = new ByteArrayOutputStream((int) getLength());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void endPacket() {
        if (mStream != null) {
            byte[] bytes = ((ByteArrayOutputStream) mStream).toByteArray();
            try {
                mEntity = new String(bytes, 0, bytes.length, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            closeStream();
        }
    }
}
