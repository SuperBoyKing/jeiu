package com.example.funnychat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;

import com.example.funnychat.chat.ClientActivity;
import com.example.funnychat.ui.home.HomeFragment;
import com.example.funnychat.ui.home.HomeViewModel;
import com.example.funnychat.ui.home.RoomInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SocketChannel socketChannel;
    private static final String USER_INFO = "user_Info";
    private final static String ROOM_NAME = "room_name";

    ListView roomList;
    String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        /*FloatingActionButton fab = findViewById(R.id.fab);
        roomName = (EditText)findViewById(R.id.room_name);
        roomList = (ListView)findViewById(R.id.roomView);
        //chat();
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
                        room = roomName.getText().toString();
                        Toast.makeText(MainActivity.this, "방 생성 완료!", Toast.LENGTH_LONG).show();
                        alertDialog.cancel();
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

      /*roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chat();
            }
        });*/
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
        final String userInfo[] = getIntent().getStringArrayExtra(USER_INFO);
        View headerView = navigationView.getHeaderView(0);
        TextView headerTitle = headerView.findViewById(R.id.textView_head);
        TextView headerSubTitle = headerView.findViewById(R.id.textView_sub);
        headerTitle.setText(userInfo[0]);
        headerSubTitle.setText(userInfo[1]);
    }

    /*public void chat() {
        final String userInfo[] = getIntent().getStringArrayExtra(USER_INFO);
        Intent ChatIntent = new Intent(MainActivity.this, HomeFragment.class);
        ChatIntent.putExtra(USER_INFO, userInfo);
        //startActivity(ChatIntent);
    }*/
}
