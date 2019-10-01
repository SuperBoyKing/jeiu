package com.example.funnychat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.funnychat.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private TextView chatText;
    private TextView their_name;
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textView) {
        super(context, textView);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!chatMessageObj.left) {
            row = inflater.inflate(R.layout.my_message, parent, false);
        } else {
            row = inflater.inflate(R.layout.their_message, parent, false);
            their_name = (TextView) row.findViewById(R.id.name);
            their_name.setText(chatMessageObj.name);
        }

        chatText = (TextView) row.findViewById(R.id.message_body);
        chatText.setText(chatMessageObj.message);
        return row;
    }
}
