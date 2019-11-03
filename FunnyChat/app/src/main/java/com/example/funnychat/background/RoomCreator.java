package com.example.funnychat.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RoomCreator extends AsyncTask<String, Void, String> {

    String sendMsg, receiveMsg;
    Context context;
    ListView listView;
    ArrayList<String> roomList = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected String doInBackground(String... params) {
        try {
            String str;
            URL url = new URL("http://172.30.1.21:8080/Connect/Android/roomType.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            sendMsg = "email=" + params[0] + "&room=" + params[1] + "&type=" + params[2];

            osw.write(sendMsg);
            osw.flush();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }

}
