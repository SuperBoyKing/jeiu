package com.example.funnychat;

import android.content.Intent;
import android.os.Bundle;

import com.example.funnychat.chat.ClientActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SocketChannel socketChannel;
    private static final String USER_INFO = "user_Info";
    EditText roomName;
    Button makeRoom;
    Button cancelRoom;
    View popupInputDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("채팅방 개설");
                alertDialogBuilder.setCancelable(false);
                initMakeRoomDialog();
                alertDialogBuilder.setView(popupInputDialogView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                makeRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String room = roomName.getText().toString();
                    }
                });
                cancelRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
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

    private void initMakeRoomDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        popupInputDialogView = layoutInflater.inflate(R.layout.make_room, null);
        roomName = popupInputDialogView.findViewById(R.id.room_name);
        makeRoom = popupInputDialogView.findViewById(R.id.btn_make_room);
        cancelRoom = popupInputDialogView.findViewById(R.id.btn_cancel_room);
    }

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
