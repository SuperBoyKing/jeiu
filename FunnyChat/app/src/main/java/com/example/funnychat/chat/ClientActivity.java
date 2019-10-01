package com.example.funnychat.chat;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import com.google.gson.*;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.funnychat.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class ClientActivity extends Activity {

    SocketChannel socketChannel;
    boolean left = true;
    ChatArrayAdapter chatArrayAdapter;
    EditText chatText;
    ListView chatView;
    TextView their_name;
    ImageButton sent;
    Handler handler = new Handler();
    private static final String USER_INFO = "user_Info";
    Charset charset = Charset.forName("UTF-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        chatView = (ListView) findViewById(R.id.chatView);
        chatText = (EditText) findViewById(R.id.chatText);
        sent = (ImageButton) findViewById(R.id.sent);
        their_name = (TextView) findViewById(R.id.name);
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
                    final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("121.172.113.28", 1000));
                    JSONObject message = new JSONObject();
                    message.put("type", "로그인");
                    message.put("email", userInfo[0]);
                    message.put("name", userInfo[1]);

                    //String data = gson.toJson(message);
                    ByteBuffer byteBuffer = charset.encode(String.valueOf(message)/*.
                            replace(',', '\n').replace('\"', ' ')*/);
                    socketChannel.write(byteBuffer);
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
                final String data = charset.decode(byteBuffer).toString();
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject)parser.parse(data);
                String name = obj.get("name").getAsString();
                String text = obj.get("text").getAsString();
                left = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatArrayAdapter.add(new ChatMessage(left, name, text));
                    }
                });
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
                final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                final String text = chatText.getText().toString();

                JSONObject message = new JSONObject();
                message.put("name", userInfo[1]);
                message.put("text", text);
                ByteBuffer byteBuffer = charset.encode(String.valueOf(message)/*.
                        replace(',', '\n').replace('\"', ' ')*/);
                socketChannel.write(byteBuffer);
                left = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatArrayAdapter.add(new ChatMessage(left, null, text));
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

}
