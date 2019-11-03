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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.funnychat.R;
import com.example.funnychat.background.RoomCreator;
import com.example.funnychat.chat.ClientActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

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
        final String userInfo[] = getActivity().getIntent().getExtras().getStringArray(USER_INFO);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new HomeViewModel(getActivity(), R.layout.fragment_home);
        listview = root.findViewById(R.id.roomView);
        listview.setAdapter(homeViewModel);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 Intent intent = new Intent(getActivity(), ClientActivity.class);
                 intent.putExtra(USER_INFO, userInfo);
                 startActivity(intent);
             }
         });
        try {
            RoomCreator roomCreator = new RoomCreator();
            String select = roomCreator.execute(userInfo[0], room, "select").get();
            parse(select);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        if (room.equals("") || room == null) {
                            Toast.makeText(getContext(), "방 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            try {
                                RoomCreator roomCreator = new RoomCreator();
                                roomCreator.execute(userInfo[0], room, "create"); // userInfo[0] means for email
                                homeViewModel.add(new RoomInfo(room));
                                Toast.makeText(getContext(), "방 생성 완료!", Toast.LENGTH_SHORT).show();
                                alertDialog.cancel();
                            } catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getContext(), "방 생성 실패!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
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

    private int parse(String parse) {
        try {
            JSONArray jsonArray = new JSONArray(parse);
            JSONObject jsonObject = null;

            homeViewModel.clear();

            for (int i=0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String room = jsonObject.getString("room");
                homeViewModel.add(new RoomInfo(room));
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}