package com.example.funnychat.background;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FunnyChatDAO {

   /* private static final String url = "pjs.chgycbit5egq.ap-northeast-2.rds.amazonaws.com";
    private static final String user = "admin";
    private static final String pass = "mysql7612";
    FunnyChatDTO funnyChatDTO = new FunnyChatDTO();

    public class ConnectDB extends AsyncTask<String, Void, String> {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String email = funnyChatDTO.getEmail();
        String password = funnyChatDTO.getPassword();
        String name = funnyChatDTO.getName();

        void connect() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, pass);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        void disconnect() {
            if(pstmt != null) {
                try {
                    pstmt.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
            btnSignup.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            connect();
            String sql = "insert into User(email, password, name)" +
                    "values (?, ?, ?)";
            try {
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.setString(3, name);

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }
    }*/
}
