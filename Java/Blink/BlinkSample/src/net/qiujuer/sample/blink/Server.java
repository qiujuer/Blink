package net.qiujuer.sample.blink;

import net.qiujuer.blink.BlinkConnect;
import net.qiujuer.blink.BlinkServer;

import java.io.IOException;

/**
 * Test Server
 */
public class Server extends Thread {
    public static void main(String[] args) throws IOException {
        final CallBack callBack = new CallBack();

        BlinkServer accept = new BlinkServer(new BlinkServer.ServerListener() {
            @Override
            public void onConnectCreated(BlinkConnect connect) {
                connect.setReceiveListener(callBack);
                connect.setConnectListener(callBack);
                System.out.println("onConnectCreated:" + connect.getId());
            }

            @Override
            public void onConnectClosed(BlinkConnect connect) {
                System.out.println("onConnectClosed:" + connect.getId());
            }
        });
        accept.bind(2626);

        Utils.readKey();

        accept.dispose();
    }
}
