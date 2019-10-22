package com.example.funnychat;

import android.content.Intent;
import android.os.Bundle;

import com.example.funnychat.chat.ClientActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    SocketChannel socketChannel;
    private AppBarConfiguration mAppBarConfiguration;
    private static final String USER_INFO = "user_Info";
    Charset charset = Charset.forName("UTF-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { chat(); }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        SycUserProfile(navigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    /*public void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    final String[] userInfo = getIntent().getStringArrayExtra(USER_INFO);
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("121.172.113.28", 1000));

                    JSONObject message = new JSONObject();
                    message.put("email", userInfo[0]);
                    message.put("type", "Login");
                    message.put("name", userInfo[1]);

                    ByteBuffer byteBuffer = charset.encode(String.valueOf(message));
                    socketChannel.write(byteBuffer);
                    socketChannel.close();
                } catch (Exception e) {
                    try {
                        stopClient();
                        return;
                    } catch(Exception e2) {}
                }
            }

        };
        thread.start();
    }*/

    public void stopClient() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void SycUserProfile(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        final String userInfo[] = getIntent().getStringArrayExtra(USER_INFO);
        TextView headerTitle = headerView.findViewById(R.id.textView_head);
        TextView headerSubTitle = headerView.findViewById(R.id.textView_sub);
        headerTitle.setText(userInfo[0]);
        headerSubTitle.setText(userInfo[1]);
    }

    public void chat() {
        final String userInfo[] = getIntent().getStringArrayExtra(USER_INFO);
        Intent ChatIntent = new Intent(MainActivity.this, ClientActivity.class);
        ChatIntent.putExtra(USER_INFO, userInfo);
        startActivity(ChatIntent);
    }
}
