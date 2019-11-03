package com.example.funnychat.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import com.example.funnychat.R;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ArrayAdapter<RoomInfo> {

    private List<RoomInfo> roomList = new ArrayList<RoomInfo>();
    private final Context context;
    private MutableLiveData<String> mText;
    private RoomInfo roomInfo;
    private TextView textView;
    private View row;

    public HomeViewModel(Context context, int values) {
        super(context, values);
        this.context = context;
    }

    public void add(RoomInfo object) {
        roomList.add(object);
        super.add(object);
    }

    public int getCount() { return this.roomList.size(); }

    public RoomInfo getItem(int index) { return this.roomList.get(index); }

    public View getView(int position, View convertView, ViewGroup parent) {
        roomInfo = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.room_info, parent, false);
        textView = (TextView) row.findViewById(R.id.roomName);
        textView.setText(roomInfo.roomName);

        return row;
    }

}