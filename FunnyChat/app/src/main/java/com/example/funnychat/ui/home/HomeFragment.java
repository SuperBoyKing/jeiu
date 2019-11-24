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
import com.example.funnychat.background.SessionManager;
import com.example.funnychat.chat.ClientActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private final static String USER_INFO = "user_Info";
    private final static String ROOM_INFO = "room_Info";
    HomeViewModel homeViewModel;
    ListView listview;
    EditText roomName;
    Button makeRoom;
    Button cancelRoom;
    View popupInputDialogView;
    String room;
    String room_info;
    String mName;
    String mEmail;
    SessionManager sessionManager;

    public HomeFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        // 세션에서 회원정보 로드
        sessionManager = new SessionManager(this.getActivity());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        this.mName = user.get(sessionManager.NAME);
        this.mEmail = user.get(sessionManager.EMAIL);

        final String userInfo[] = {mEmail, mName};
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = new HomeViewModel(getActivity(), R.layout.fragment_home);

        selectRoomList(userInfo); // DB에서 저장된 방 목록 불러오기

        /* 리스트뷰에 setAdapter를 생성하여 방 목록 구현*/
        listview = root.findViewById(R.id.roomView);
        listview.setAdapter(homeViewModel);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 방 목록 클릭 시 ChatServer 접속
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 RoomInfo roomInfo = homeViewModel.getItem(position);
                 Intent intent = new Intent(getActivity(), ClientActivity.class);
                 intent.putExtra(USER_INFO, userInfo);  // 회원 정보 전달
                 intent.putExtra(ROOM_INFO, roomInfo.roomName); // 방 이름 전달
                 startActivity(intent); // 엑티비티 실행
             }
         });
        // fab 버튼 클릭 시 방 생성 UI 호출
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
                        String room = roomName.getText().toString();
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
        // 방 목록 업데이트
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

     // 방 생성 UI 초기화
    private void initMakeRoomDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popupInputDialogView = layoutInflater.inflate(R.layout.make_room, null);
        roomName = popupInputDialogView.findViewById(R.id.room_name);
        makeRoom = popupInputDialogView.findViewById(R.id.btn_make_room);
        cancelRoom = popupInputDialogView.findViewById(R.id.btn_cancel_room);
    }

    // 방 목록 데이터 파싱
    private int parse(String parse) {
        try {
            JSONArray jsonArray = new JSONArray(parse);
            JSONObject jsonObject = null;

            homeViewModel.clear();  // 기존 방 목록 제거

            for (int i=0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                room_info = jsonObject.getString("room");
                homeViewModel.add(new RoomInfo(room_info)); // 방
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void selectRoomList(String[] userInfo) {
        try {
            RoomCreator roomCreator = new RoomCreator();
            String select = roomCreator.execute(userInfo[0], room, "select").get(); // 방 정보 셀릭트
            parse(select); // 반환데이터파싱
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}