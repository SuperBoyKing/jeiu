package com.example.client;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class ClientActivity extends Activity {

    SocketChannel socketChannel;
    boolean side = false;
    ChatArrayAdapter chatArrayAdapter;
    EditText chatText;
    ListView chatView;
    ImageButton sent;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        chatView = (ListView) findViewById(R.id.chatView);
        chatText = (EditText) findViewById(R.id.chatText);
        sent = (ImageButton) findViewById(R.id.sent);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.my_message);
        chatView.setAdapter(chatArrayAdapter);
        chatView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatView.setAdapter(chatArrayAdapter);
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Thread sentThread = new Thread(new SendMessage());
                sentThread.start();
            }
        });
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatView.setSelection(chatArrayAdapter.getCount() -1);
            }
        });
        startClient();
    }

    void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("121.172.113.28", 1000));

                } catch (Exception e) {
                    try {
                        stopClient();
                        return;
                    } catch(Exception e2) {}
                }
                receive();
            }
        };
        thread.start();
    }

    void stopClient() {
        try {
            chatText.setFocusable(false);
            sent.setFocusable(false);
            chatView.setFocusable(false);

            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {}
    }

    void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                int readByteCount = socketChannel.read(byteBuffer);

                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString();
            } catch (Exception e) {
                stopClient();
                break;
            }
        }
    }

    class SendMessage implements Runnable
    {
        @Override
        public void run() {
            try {
                Charset charset = Charset.forName("UTF-8");
                final String smessage = chatText.getText().toString();
                ByteBuffer byteBuffer = charset.encode(smessage);
                socketChannel.write(byteBuffer);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatArrayAdapter.add(new ChatMessage(side, smessage));
                        chatText.setText("");
                    }
                });
            }
            catch(Exception e) {
                e.printStackTrace();
                stopClient();
                return;
            }
        }
    }


   /* public class ClientThread implements Runnable
    {
        public void run()  {
            try {
                while (true)
                {
                    InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                    Socket socket = new Socket(serverAddr, 10000);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String line = null;
                    while ((line = br.readLine()) != null)
                    {
                        msg = msg + "Server : " + line + "\n";
                        handler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                chat.setText(msg);
                            }
                        });
                    }
                    in.close();
                    socket.close();
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }*/
}
