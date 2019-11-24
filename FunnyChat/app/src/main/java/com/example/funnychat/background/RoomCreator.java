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
    HostConnector hostConnector;

    public RoomCreator() {
        hostConnector = new HostConnector();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String str;
            URL url = new URL("http://" + hostConnector.getHostName() + ":8080/FunnyChatServer/Android/roomType.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");   // 페이지 속성 정의
            conn.setRequestMethod("POST");  // 메소드 타입 정의
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());    // 문자열 데이터 전송을 위한 스트림라이터와 아웃풋스트림 생성
            sendMsg = "email=" + params[0] + "&room=" + params[1] + "&type=" + params[2];   // queryString을 이용한 테이터 전달

            osw.write(sendMsg); // 스트림에 데이터문자열 write
            osw.flush(); // 추후에 있을 데이터 전달을 위해 스트림 초기화

            if (conn.getResponseCode() == conn.HTTP_OK) {   // 서버와 연결 성공 여부 확인
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");  // 서버에서 데이터 수신을 위한 스트림리더와 인풋스트림 생성
                BufferedReader reader = new BufferedReader(tmp);    // 버퍼리더를 생성하여 문자열 읽기;
                StringBuffer buffer = new StringBuffer();           // 스레드 세이프 한 스트링버퍼를 생성하여 수신받은 데이터문자열 저장
                while ((str = reader.readLine()) != null) {         // str 변수에 라인별로 문자열 저장
                    buffer.append(str);     // 변수에 저장된 문자열을 스트링버퍼에 추가
                }
                receiveMsg = buffer.toString(); // 버퍼에 저장된 바이트 코드를 문자열로 변환
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return receiveMsg;
    }

}
