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
package net.qiujuer.test;

import net.qiujuer.blink.core.BlinkConn;

import java.io.File;
import java.net.Socket;

public class Client {
    private static BlinkConn conn = null;

    public static void main(String[] args) {
        Socket socket = null;
        try {
            System.out.println("start socket");
            socket = new Socket("127.0.0.1", 2626);
            System.out.println("start bind Blink");
            conn = Utils.bindBlink(socket);
            System.out.println("start blink socket.");
            send();
        } catch (Exception e) {
            System.out.println("error" + e);
        }

        // Exit
        Utils.readKey();
        if (conn != null)
            conn.destroy();
        Utils.close(null, socket);
        System.exit(0);
    }

    static void send() {
        if (conn == null)
            return;

        System.out.println("Test Send String...");
        for (int i = 0; i <= 1; i++) {
            conn.send("Test String:" + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Test Bytes...");
        conn.send(new byte[]{1, 1, 0, 0});

        System.out.println("Test File...");
        conn.send(new File("F:/TDDOWNLOAD/250315GT_AS.zip"));

        System.out.println("Send End.");
    }


}
