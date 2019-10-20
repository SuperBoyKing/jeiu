package com.example.funnychat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnychat.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    ImageView file;
    private TextView chatText;
    private TextView their_name;
    Context context;
    private ChatMessage chatMessageObj;
    private View row;

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
        chatMessageObj = getItem(position);
        row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.position.equals("right")) {
            if(chatMessageObj.type.equals("File")) {
                row = inflater.inflate(R.layout.my_file, parent, false);
                showUpFile();
            } else {
                row = inflater.inflate(R.layout.my_message, parent, false);
                showUpText();
            }

        } else if (chatMessageObj.position.equals("left")) {
            if (chatMessageObj.type.equals("File")) {
                row = inflater.inflate(R.layout.their_file, parent, false);
                showUpName();
                showUpFile();
            } else {
                row = inflater.inflate(R.layout.their_message, parent, false);
                showUpName();
                showUpText();
            }
        }
        return row;
    }

    private void showUpText() {
        chatText = (TextView) row.findViewById(R.id.message_body);
        chatText.setText(chatMessageObj.message);
    }

    private void showUpFile() {
        file = row.findViewById(R.id.message_body);
        chatText = row.findViewById(R.id.fileName);
        chatText.setText(chatMessageObj.message);
    }

    private void showUpName() {
        their_name = (TextView) row.findViewById(R.id.name);
        their_name.setText(chatMessageObj.name);
    }
}
