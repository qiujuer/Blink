package net.qiujuer.test;


import net.qiujuer.blink.Blink;
import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.core.BlinkPacket;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utils {

    static BlinkConn bindBlink(Socket socket) throws Exception {
        // Create a async thread to callback listener
        Executor executor = Executors.newSingleThreadExecutor();
        // Receive listener
        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void onReceiveStart(int type, long id) {
                System.out.println("Receive->start:" + type + " " + id);
            }

            @Override
            public void onReceiveProgress(int type, long id, int total, int cur) {
                System.out.println("Receive->progress:" + type + " " + id
                        + " " + total + " " + cur);
            }

            @Override
            public void onReceiveEnd(ReceivePacket entity) {
                if (entity.getType() == BlinkPacket.Type.STRING)
                    System.out.println("Receive->end: String:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + ((StringReceivePacket) entity).getEntity());
                else if (entity.getType() == BlinkPacket.Type.BYTES)
                    System.out.println("Receive->end: Bytes:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + Arrays.toString(((ByteReceivePacket) entity).getEntity()));
                else
                    System.out.println("Receive->end: File:"
                            + entity.getId()
                            + " "
                            + entity.getLength()
                            + " :"
                            + ((FileReceivePacket) entity).getEntity()
                            .getPath() + " " + entity.getHashCode());
            }
        };

        return Blink.newConnection(socket, 4 * 1024 * 1024,
                "D:/Blink/", UUID.randomUUID().toString(), executor, listener);
    }

    static void close(ServerSocket server, Socket client) {
        if (client != null)
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (server != null)
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    static void readKey() {
        System.out.println("=========PRESS ANY KEY TO EXIT==========");
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
