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
            socket = new Socket("127.0.0.1", 4700);
            System.out.println("start bind Blink");
            conn = Utils.bindBlink(socket);
            System.out.println("start blink socket.");
            send();
        } catch (Exception e) {
            System.out.println("error" + e);
        }

        // Exit
        Utils.readKey();
        Utils.close(null, socket);
        System.exit(0);
    }

    static void send() {
        if (conn == null)
            return;

        System.out.println("Test Send String...");
        for (int i = 0; i <= 10; i++) {
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
