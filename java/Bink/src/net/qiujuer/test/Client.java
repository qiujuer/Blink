package net.qiujuer.test;

import java.io.File;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executor;

import net.qiujuer.blink.Blink;
import net.qiujuer.blink.BlinkConn;
import net.qiujuer.blink.Entity.Type;
import net.qiujuer.blink.ReceiveEntity;
import net.qiujuer.blink.box.StringReceiveEntity;
import net.qiujuer.blink.listener.ReceiveListener;

public class Client {
	private static BlinkConn conn = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			System.out.println("Start socket");
			Socket socket = new Socket("127.0.0.1", 4700);
			System.out.println("Start bind Blink");
			conn = bindBlink(socket);
			System.out.println("Blink socket.");
		} catch (Exception e) {
			System.out.println("Error" + e);
		}

		if (conn != null) {

			Thread thread = new Thread() {
				public void run() {

					System.out.println("Test Send String...");
					for (int i = 0; i <= 10; i++) {
						conn.send("Send String:" + i);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					System.out.println("Test Send Bytes...");
					conn.send(new byte[] { 1, 1, 0, 0 });

					System.out.println("Test Send File...");
					conn.send(new File("F:/TDDOWNLOAD/250315GT_AS.zip"));

					System.out.println("Send End.");
				}
			};
			thread.start();
		}

	}

	private static BlinkConn bindBlink(Socket socket) throws Exception {
		Executor executor = new Executor() {

			@Override
			public void execute(Runnable arg0) {
				// TODO Auto-generated method stub
				Thread thread = new Thread(arg0);
				thread.start();
			}

		};

		ReceiveListener listener = new ReceiveListener() {
			@Override
			public void onReceiveStart(int type, long id) {
				// TODO Auto-generated method stub
				System.out.println("ReceiveListener->start:" + type + " " + id);
			}

			@Override
			public void onReceiveProgress(int type, long id, int total, int cur) {
				// TODO Auto-generated method stub
				System.out.println("ReceiveListener->run:" + type + " " + id
						+ " " + total + " " + cur);
			}

			@Override
			public void onReceiveEnd(ReceiveEntity entity) {
				// TODO Auto-generated method stub
				if (entity.getType() == Type.STRING)
					System.out.println("ReceiveListener->end:" + entity.getId()
							+ " " + entity.getLength() + " :"
							+ ((StringReceiveEntity) entity).getResult());
				else
					System.out.println("ReceiveListener->end:" + entity.getId()
							+ " " + entity.getLength());
			}
		};

		BlinkConn conn = Blink.newConnection(socket, 4 * 1024 * 1024,
				"D:/Blink/", UUID.randomUUID().toString(), executor, listener);

		return conn;
	}

}
