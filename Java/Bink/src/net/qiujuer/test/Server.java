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
import net.qiujuer.blink.core.Resource;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Socket> sockets = new ArrayList<>();
    private static List<BlinkConn> blinkConnList = new ArrayList<>();
    private static ServerSocket server = null;

    public static void main(String[] args) {
        try {
            System.out.println("start server...");
            server = new ServerSocket(2626);
            accept();
        } catch (Exception e) {
            System.out.println("error." + e);
        }

        // Exit
        Utils.readKey();
        Utils.close(server, null);
        for (Socket s : sockets) {
            Utils.close(null, s);
        }
        for (BlinkConn b : blinkConnList) {
            b.destroy();
        }
        System.exit(0);
    }

    static void accept() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (server != null && !server.isClosed()) {
                        System.out.println("accept socket...");
                        final Socket socket = server.accept();
                        System.out.println("new link socket; start bind...");
                        BlinkConn conn = Utils.bindBlink(socket);
                        Resource res = conn.getResource();
                        res.clearAll();
                        System.out.println("bind ok:->" + res.getMark().toString());

                        sockets.add(socket);
                        blinkConnList.add(conn);
                    }
                } catch (Exception e) {
                    System.out.println("error." + e);
                }
            }
        };
        thread.start();
    }
}
