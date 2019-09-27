package com.example.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.client.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class ClientActivity extends Activity {

    SocketChannel socketChannel;

    EditText smessage;
    TextView chat;
    Button sent;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        chat = (TextView) findViewById(R.id.chat);
        smessage = (EditText) findViewById(R.id.smessage);
        sent = (Button) findViewById(R.id.sent_button);
        startClient();
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Thread sentThread = new Thread(new SendMessage());
                sentThread.start();
            }
        });
    }

    void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("172.30.1.21", 1000));

                } catch (Exception e) {
                    try {
                        chat.setText("연결 실패");
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
            smessage.setFocusable(false);
            sent.setFocusable(false);
            chat.setFocusable(false);

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
                String message = smessage.getText().toString();
                ByteBuffer byteBuffer = charset.encode(message);
                socketChannel.write(byteBuffer);
                chat.append("\n" + message);
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
