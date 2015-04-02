package net.qiujuer.sample.blink;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.qiujuer.blink.Blink;
import net.qiujuer.blink.box.ByteReceivePacket;
import net.qiujuer.blink.box.FileReceivePacket;
import net.qiujuer.blink.box.StringReceivePacket;
import net.qiujuer.blink.core.BlinkConn;
import net.qiujuer.blink.core.BlinkPacket;
import net.qiujuer.blink.core.ReceivePacket;
import net.qiujuer.blink.listener.ReceiveListener;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private final String TAG = "BlinkSample";

    private EditText mIp;
    private EditText mPort;
    private EditText mMsg;

    private Button mLink;
    private Button mSend;

    private Socket mSocket = null;
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
                    Log.v(TAG, "start socket");
                    mSocket = new Socket(ip, Integer.parseInt(port));
                    Log.v(TAG, "start bind Blink");
                    mConn = bindBlink(mSocket);
                    Log.v(TAG, "start blink socket.");
                } catch (Exception e) {
                    Log.e(TAG, "error" + e);
                }
                refreshStatus();
            }
        };
        thread.start();
    }

    private BlinkConn bindBlink(Socket socket) throws Exception {
        // Receive listener
        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void onReceiveStart(int type, long id) {
                Log.v(TAG, "Receive->start:" + type + " " + id);
            }

            @Override
            public void onReceiveProgress(int type, long id, int total, int cur) {
                Log.v(TAG, "Receive->progress:" + type + " " + id
                        + " " + total + " " + cur);
            }

            @Override
            public void onReceiveEnd(ReceivePacket entity) {
                if (entity.getType() == BlinkPacket.Type.STRING)
                    Log.v(TAG, "Receive->end: String:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + ((StringReceivePacket) entity).getEntity());
                else if (entity.getType() == BlinkPacket.Type.BYTES)
                    Log.v(TAG, "Receive->end: Bytes:"
                            + entity.getId() + " " + entity.getLength() + " :"
                            + Arrays.toString(((ByteReceivePacket) entity).getEntity()));
                else
                    Log.v(TAG, "Receive->end: File:"
                            + entity.getId()
                            + " "
                            + entity.getLength()
                            + " :"
                            + ((FileReceivePacket) entity).getEntity()
                            .getPath() + " " + entity.getHashCode());
            }
        };

        return Blink.newConnection(socket, listener);
    }

    private void destroy() {
        if (mConn != null) {
            mConn.destroy();
            mConn = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }
}
