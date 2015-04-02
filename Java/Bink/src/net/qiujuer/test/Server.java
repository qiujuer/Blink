package net.qiujuer.test;

import net.qiujuer.blink.core.BlinkConn;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;
        try {
            System.out.println("start server...");
            server = new ServerSocket(4700);
            System.out.println("accept socket...");
            socket = server.accept();
            System.out.println("new link socket; start bind...");
            BlinkConn conn = Utils.bindBlink(socket);
            System.out.println("bind ok.");
            conn.getResource().clearAll();
        } catch (Exception e) {
            System.out.println("error." + e);
        }

        // Exit
        Utils.readKey();
        Utils.close(server, socket);
        System.exit(0);
    }


}
