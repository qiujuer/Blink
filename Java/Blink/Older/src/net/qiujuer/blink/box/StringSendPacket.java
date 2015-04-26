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
package net.qiujuer.blink.box;

import net.qiujuer.blink.core.listener.SendListener;

import java.io.UnsupportedEncodingException;

/**
 * String send class
 */
public class StringSendPacket extends ByteSendPacket {

    public StringSendPacket(String entity) throws UnsupportedEncodingException {
        this(entity, null);
    }

    public StringSendPacket(String entity, SendListener listener) throws UnsupportedEncodingException {
        super(entity.getBytes("UTF-8"), listener);
        mType = Type.STRING;
    }
}
