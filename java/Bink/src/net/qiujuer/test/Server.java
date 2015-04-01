package net.qiujuer.test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executor;

import net.qiujuer.blink.*;
import net.qiujuer.blink.Entity.Type;
import net.qiujuer.blink.box.ByteReceiveEntity;
import net.qiujuer.blink.box.FileReceiveEntity;
import net.qiujuer.blink.box.StringReceiveEntity;
import net.qiujuer.blink.listener.ReceiveListener;

public class Server {

	/**
	 * @param arg
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			ServerSocket server = null;
			try {
				server = new ServerSocket(4700);
				System.out.println("start server...");
			} catch (Exception e) {
				System.out.println("can not listen to:" + e);
			}

			Socket socket = null;
			try {
				System.out.println("accept socket...");
				socket = server.accept();
				System.out.println("new link socket; start bind...");
				bindBlink(socket);
				System.out.println("Bind ok.");
			} catch (Exception e) {
				System.out.println("Error." + e);
			}

			// socket.close();
			// server.close();
		} catch (Exception e) {
			System.out.println("Error:" + e);
		}
	}

	private static void bindBlink(Socket socket) throws Exception {
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
					System.out.println("ReceiveListener->end: String:"
							+ entity.getId() + " " + entity.getLength() + " :"
							+ ((StringReceiveEntity) entity).getResult());
				else if (entity.getType() == Type.BYTES)
					System.out.println("ReceiveListener->end: Bytes:"
							+ entity.getId() + " " + entity.getLength() + " :"
							+ ((ByteReceiveEntity) entity).getResult());
				else
					System.out.println("ReceiveListener->end: Bytes:"
							+ entity.getId()
							+ " "
							+ entity.getLength()
							+ " :"
							+ ((FileReceiveEntity) entity).getResult()
									.getPath());
			}
		};

		BlinkConn conn = Blink.newConnection(socket, 4 * 1024 * 1024,
				"D:/Blink/", UUID.randomUUID().toString(), executor, listener);

	}
}
