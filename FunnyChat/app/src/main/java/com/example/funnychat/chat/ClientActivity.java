package com.example.funnychat.chat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.funnychat.background.FileTransfer;
import com.google.gson.*;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.funnychat.R;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;


public class ClientActivity extends Activity implements PermissionCallbacks {

    private static final String TAG = ClientActivity.class.getSimpleName();
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;

    SocketChannel socketChannel;
    boolean left = true;
    ChatArrayAdapter chatArrayAdapter;
    EditText chatText;
    ListView chatView;
    TextView their_name;
    ImageButton sent;
    ImageButton fileBrowser;
    ImageButton fileUpload;
    File file;
    Uri fileUri;

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
        fileBrowser = findViewById((R.id.fileBrowser));
        fileUpload = findViewById(R.id.fileUpload);

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
        fileBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (EasyPermissions.hasPermissions(ClientActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showFileBrowserIntent();

                } else {
                    EasyPermissions.requestPermissions(ClientActivity.this, getString(R.string.file_access),
                            READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                chatText.setFocusable(false);
                hideFileBrowser();
            }
        });
        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (file != null) {
                    FileTransfer fileTransfer = new FileTransfer(ClientActivity.this, file);
                    fileTransfer.execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select a file first", Toast.LENGTH_LONG).show();
                }
                chatText.setText("");
                showFileBrowser();
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

    private void showFileBrowserIntent() {
        Intent fileManager = new Intent(Intent.ACTION_GET_CONTENT);
        fileManager.setType("*/*");
        startActivityForResult(fileManager, REQUEST_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK)  {
            fileUri = data.getData();
            previewFile(fileUri);
        }
    }

    private void previewFile(Uri uri) {
        String filePath = getRealPathFromURIPath(uri, ClientActivity.this);
        file = new File(filePath);
        Log.d(TAG, "FileName " + file.getName());
        chatText.setText(file.getName());

        /*ContentResolver cR = this.getContentResolver();
        String mime = cR.getType(uri);

        if (mime != null && mime.contains("image")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        }*/
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            realPath = cursor.getString(idx);
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResult, ClientActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileBrowserIntent();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    private void hideFileBrowser() {
        fileBrowser.setVisibility((View.GONE));
        fileUpload.setVisibility(View.VISIBLE);
    }

    private void showFileBrowser() {
        fileUpload.setVisibility(View.GONE);
        fileBrowser.setVisibility(View.VISIBLE);
    }

    void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("172.30.1.21", 1000));
                    JSONObject message = new JSONObject();
                    message.put("type", "로그인");
                    message.put("email", userInfo[0]);
                    message.put("name", userInfo[1]);

                    ByteBuffer byteBuffer = charset.encode(String.valueOf(message));
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
                ByteBuffer byteBuffer = charset.encode(String.valueOf(message));
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
