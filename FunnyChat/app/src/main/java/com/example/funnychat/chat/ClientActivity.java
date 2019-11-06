package com.example.funnychat.chat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.funnychat.background.ChatStore;
import com.example.funnychat.background.FileDownloader;
import com.example.funnychat.background.FileUploader;
import com.google.gson.*;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;


public class ClientActivity extends Activity implements PermissionCallbacks {

    private static final String HOST_NAME = "172.30.1.21";
    private static final String TAG = ClientActivity.class.getSimpleName();
    private static final String[] WRITE_PERMISSION = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 250;
    private static final int WRITE_REQUEST_CODE = 300;

    SocketChannel socketChannel;
    String position = "left";
    String download_fileName;
    ChatArrayAdapter chatArrayAdapter;
    EditText chatText;
    ListView chatView;
    TextView their_name;
    ImageButton sent;
    ImageButton fileBrowser;
    ImageButton fileUpload;
    File file;
    Uri fileUri;
    Bitmap bitmap;
    Thread sentThread;

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
        chatView.setTextFilterEnabled(true);

        chatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ChatMessage chatList = chatArrayAdapter.getItem(position);
                if (chatList.type.equals("File") && isSDCardPresent()) {
                    if (hasPermission(WRITE_PERMISSION)) {
                        download_fileName = chatList.message;
                        DownloadFile();
                    } else {
                        requestPermission(WRITE_PERMISSION);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), chatList.message, Toast.LENGTH_LONG).show();
                }
            }
        });
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sentThread = new Thread(new SendMessage("Message"));
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
                hideFileBrowser();
            }
        });
        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (file != null) {
                    FileUploader fileUploader = new FileUploader(ClientActivity.this, file);
                    fileUploader.execute();
                    sentThread = new Thread(new SendMessage("File"));
                    sentThread.start();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "업로드할 파일 선택을 해주세요.", Toast.LENGTH_LONG).show();
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
        connectServer();
    }

    @Override
    public void onBackPressed() {
       try {
           sentThread = new Thread(new SendMessage("Leave"));
           sentThread.start();
           sentThread.join();
           stopClient();
           super.onBackPressed();
       } catch (Exception e) {
           e.printStackTrace();
           stopClient();
           super.onBackPressed();
       }
    }

    void connectServer() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(HOST_NAME, 1000));

                    JSONObject message = new JSONObject();
                    message.put("name", userInfo[1]);
                    message.put("type", "Connect");
                    message.put("email", userInfo[0]);

                    ByteBuffer byteBuffer = charset.encode(String.valueOf(message));
                    socketChannel.write(byteBuffer);
                } catch (Exception e) {
                    try {
                        stopClient();
                        return;
                    } catch(Exception e2) {}
                }
                receive();
                sentThread = new Thread(new SendMessage("Enter"));
                sentThread.start();
            }

        };
        thread.start();
    }

    public void stopClient() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {}
    }

    void receive() {
        Thread thread = new Thread() {
            @Override
            public void run() {
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
                        JsonObject jsonObj = (JsonObject) parser.parse(data);
                        String name = jsonObj.get("name").getAsString();
                        String type = jsonObj.get("type").getAsString();
                        String contents = jsonObj.get("contents").getAsString();
                        if (type.equals("Enter") || type.equals("Leave")) {
                            position = "center";
                        } else {
                            position = "left";
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatArrayAdapter.add(new ChatMessage(position, name, type, contents));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        stopClient();
//                        disableToUI();
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    class SendMessage implements Runnable {
        private String type;

        public SendMessage(String type) { this.type = type; }
        @Override
        public void run() {
            try {
                final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                final String contents;
                if (type.equals("Message")) {
                    contents = chatText.getText().toString();
                    ChatStore chatStore = new ChatStore();
                    chatStore.execute(userInfo[0], contents).get();
                } else if (type.equals("File")) {
                    contents = file.getName();
                } else if (type.equals("Enter")){
                    contents = userInfo[1] + "입장";
                } else {
                    contents = userInfo[1] + "퇴장";
                }

                JSONObject message = new JSONObject();
                message.put("name", userInfo[1]);
                message.put("type", type);
                message.put("contents", contents);
                ByteBuffer byteBuffer = charset.encode(String.valueOf(message));
                socketChannel.write(byteBuffer);
                if (type.equals("Message") || type.equals("File")) {
                    position = "right";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatArrayAdapter.add(new ChatMessage(position, "", type, contents));
                            chatText.setText("");
                        }
                    });
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                stopClient();
//                disableToUI();
            }
        }
    }

    boolean hasPermission(String[] permission) {
        int res = 0;
        res = checkCallingOrSelfPermission(permission[0]);
        if(!(res == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;
    }

    private void requestPermission(String[] permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(WRITE_PERMISSION, WRITE_REQUEST_CODE);
        }
    }

    private void DownloadFile() {
        FileDownloader fileDownloader = new FileDownloader(ClientActivity.this);
        fileDownloader.execute("http://" + HOST_NAME + ":8080/Connect/upload/" + download_fileName);
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

        ContentResolver cR = this.getContentResolver();
        String mime = cR.getType(uri);

        if (mime != null && mime.contains("image")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            bitmap = BitmapFactory.decodeFile(filePath, options);
        }
        else {
            bitmap = null;
        }
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
        if (requestCode == READ_REQUEST_CODE) {
            showFileBrowserIntent();
        } else if (requestCode == WRITE_REQUEST_CODE){
            DownloadFile();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    private void hideFileBrowser() {
        fileBrowser.setVisibility((View.GONE));
        fileUpload.setVisibility(View.VISIBLE);
    }

    private void showFileBrowser() {
        fileUpload.setVisibility(View.GONE);
        fileBrowser.setVisibility(View.VISIBLE);
    }

    void disableToUI() {
        chatText.setFocusable(false);
        sent.setFocusable(false);
        chatView.setFocusable(false);
        fileBrowser.setFocusable(false);
        fileUpload.setFocusable(false);
    }
}
