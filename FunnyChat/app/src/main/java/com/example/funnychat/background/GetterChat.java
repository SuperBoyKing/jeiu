package com.example.funnychat.background;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetterChat extends AsyncTask<String, Void, String> {

    String sendMsg, receiveMsg;
    HostConnector hostConnector;

    public GetterChat() {
        hostConnector = new HostConnector();
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            String str;
            URL url = new URL("http://" + hostConnector.getHostName() +":8080/FunnyChatServer/Android/getStatistics.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            sendMsg = "send=" + params[0];

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
