package net.qiujuer.sample.blink;

import net.qiujuer.blink.BlinkServer;
import net.qiujuer.blink.core.Connector;

import java.io.IOException;

/**
 * Test Server
 */
public class Server extends Thread {
    public static void main(String[] args) throws IOException {
        final CallBack callBack = new CallBack();

        BlinkServer accept = new BlinkServer(new BlinkServer.ServerListener() {
            @Override
            public void onConnectClosed(Connector connector) {
                System.out.println("onConnectClosed:" + connector.getId());
            }

            @Override
            public void onConnectCreated(Connector connector) {
                connector.setReceiveListener(callBack);
                System.out.println("onConnectCreated:" + connector.getId());
            }
        });
        accept.bind(2626);

        Utils.readKey();

        accept.dispose();
    }
}
