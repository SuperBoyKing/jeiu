package com.example.funnychat.ui.home;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.funnychat.MainActivity;
import com.example.funnychat.R;
import com.example.funnychat.chat.ClientActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.zip.Inflater;

import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment {

    private final static String USER_INFO = "user_Info";
    HomeViewModel homeViewModel;
    ListView listview;
    EditText roomName;
    Button makeRoom;
    Button cancelRoom;
    View popupInputDialogView;
    String room;

    public HomeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
         homeViewModel = new HomeViewModel(getActivity(), R.layout.fragment_home);
         View root = inflater.inflate(R.layout.fragment_home, container, false);
         listview = root.findViewById(R.id.roomView);
         listview.setAdapter(homeViewModel);
         listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 Intent intent = new Intent(getActivity(), ClientActivity.class);
                 final String userInfo[] = getActivity().getIntent().getExtras().getStringArray(USER_INFO);
                 intent.putExtra(USER_INFO, userInfo);
                 startActivity(intent);
             }
         });
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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
                        // 예외처리 요망
                        homeViewModel.add(new RoomInfo(room));
                        Toast.makeText(getContext(), "방 생성 완료!", Toast.LENGTH_LONG).show();
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
        homeViewModel.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listview.setSelection(homeViewModel.getCount() - 1);
            }
        });
         return root;
    }

     @Override
     public void onViewCreated(View view, Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
     }

    private void initMakeRoomDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popupInputDialogView = layoutInflater.inflate(R.layout.make_room, null);
        roomName = popupInputDialogView.findViewById(R.id.room_name);
        makeRoom = popupInputDialogView.findViewById(R.id.btn_make_room);
        cancelRoom = popupInputDialogView.findViewById(R.id.btn_cancel_room);
    }
}