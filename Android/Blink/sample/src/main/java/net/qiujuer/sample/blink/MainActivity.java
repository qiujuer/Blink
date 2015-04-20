package net.qiujuer.sample.blink;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.qiujuer.blink.Blink;
import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.core.BlinkPacket;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.listener.BlinkListener;
import net.qiujuer.blink.listener.ReceiveListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private final String TAG = "BlinkSample";

    private EditText mIp;
    private EditText mPort;
    private EditText mMsg;

    private Button mLink;
    private Button mSend;

    private BlinkConn mConn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIp = (EditText) findViewById(R.id.edit_ip);
        mPort = (EditText) findViewById(R.id.edit_port);
        mMsg = (EditText) findViewById(R.id.edit_msg);

        mLink = (Button) findViewById(R.id.btn_link);
        mSend = (Button) findViewById(R.id.btn_send);

        mLink.setOnClickListener(this);
        mSend.setOnClickListener(this);

        refreshStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_link) {
            String ip = mIp.getText().toString();
            String port = mPort.getText().toString();

            if (ip.length() == 0 || port.length() == 0)
                return;

            if (mConn == null) {
                linkSocket(ip, port);
            } else {
                destroy();
                refreshStatus();
            }
        } else if (id == R.id.btn_send) {
            if (mConn == null)
                return;
            String msg = mMsg.getText().toString();
            if (msg.length() == 0)
                return;
            mConn.send(msg);
        }
    }

    private void refreshStatus() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConn == null) {
                    mSend.setEnabled(false);
                    mIp.setEnabled(true);
                    mPort.setEnabled(true);
                    mMsg.setEnabled(false);
                    mLink.setText("Link");
                } else {
                    mSend.setEnabled(true);
                    mIp.setEnabled(false);
                    mPort.setEnabled(false);
                    mMsg.setEnabled(true);
                    mLink.setText("UnLink");
                }
            }
        });
    }

    private void linkSocket(final String ip, final String port) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.v(TAG, "Start socket link...");
                    SocketChannel channel = connectChannel(new InetSocketAddress(ip, Integer.parseInt(port)));
                    mConn = bindBlink(channel);
                    Log.v(TAG, "Bind BlinkConn SocketChannel.");
                } catch (Exception e) {
                    Log.e(TAG, "Error" + e);
                }
                refreshStatus();
            }
        };
        thread.start();
    }

    private SocketChannel connectChannel(SocketAddress address) throws Exception {
        Selector selector = Selector.open();

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);

        channel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {

            if (selector.select() == 0)
                continue;

            Iterator ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                ite.remove();

                if (key.isConnectable()) {
                    channel = (SocketChannel) key.channel();

                    // Link
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    Log.v(TAG, "Client Connected.");

                    // End
                    key.interestOps(key.readyOps() & ~SelectionKey.OP_CONNECT);
                    key.cancel();

                    selector.close();

                    return channel;
                }
            }
        }
    }

    private BlinkConn bindBlink(SocketChannel channel) throws Exception {
        // Receive listener
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void onReceiveStart(byte type, long id) {
                Log.v(TAG, "Receive->start:" + type + " " + id);
            }

            @Override
            public void onReceiveProgress(ReceivePacket packet, float progress) {
                Log.v(TAG, "Receive->progress: " + progress);
            }

            @Override
            public void onReceiveEnd(ReceivePacket entity) {
                if (entity.getPacketType() == BlinkPacket.PacketType.STRING)
                    Log.v(TAG, "Receive->end: String:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + ((StringReceivePacket) entity).getEntity());
                else if (entity.getPacketType() == BlinkPacket.PacketType.BYTES)
                    Log.v(TAG, "Receive->end: Bytes:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + Arrays.toString(((ByteReceivePacket) entity).getEntity()));
                else if (entity.getPacketType() == BlinkPacket.PacketType.FILE)
                    Log.v(TAG, "Receive->end: File:"
                            + entity.getId()
                            + " "
                            + entity.getLength()
                            + " :"
                            + ((FileReceivePacket) entity).getEntity()
                            .getPath() + " " + entity.getHash());
            }
        };

        BlinkListener blinkListener = new BlinkListener() {
            @Override
            public void onBlinkDisconnect() {
                Log.v(TAG, "BlinkConnection has been disconnected.");
                Toast.makeText(MainActivity.this, "BlinkConnection has been disconnected.", Toast.LENGTH_SHORT).show();
                // You should dispose blink
            }
        };

        return Blink.newConnection(channel, receiveListener, blinkListener);
    }

    private void destroy() {
        if (mConn != null) {
            mConn.dispose();
            mConn = null;
        }
    }
}
