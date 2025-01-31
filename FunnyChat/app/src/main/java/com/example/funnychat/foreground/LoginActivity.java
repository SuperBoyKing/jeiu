package com.example.funnychat.foreground;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funnychat.R;
import com.example.funnychat.background.DBConnector;
import com.example.funnychat.background.SessionManager;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String USER_INFO= "user_Info";
    private static final int REQUEST_SINGUP = 0;
    SessionManager sessionManager;

    EditText loginEmail;
    EditText loginPassword;
    Button BtnLogin;
    TextView SignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 회원 정보를 저장하기 위한 세션 생성
        sessionManager = new SessionManager(this);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        BtnLogin = findViewById(R.id.btn_login);
        SignupLink = findViewById(R.id.txt_Register);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        SignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SINGUP);
            }
        });
    }

    // 로그인 처리
    public boolean login() throws SQLException {
        Log.d(TAG, "Login");

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        String name = "empty";

        if (!validate(email, password))  return false;  // 입력한 회원 정보 유효성 검사
        BtnLogin.setEnabled(false);

        // DB 커넥팅 동안 다이알로그 display
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);

        try {
            DBConnector DBConnector = new DBConnector();
            String result = DBConnector.execute(email, password, name, "login").get();
            if (result.equals(" failed")) {     // 결과 값에 따라 로그인 성공 여부 결정
                onLoginFailed();
            } else {
                BtnLogin.setEnabled(true);
                name = result;
                String userInfo[] = {email, name};
                onLoginSuccess(userInfo);
            }
            Log.i("리턴 값 = ", result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SINGUP) {
            if (resultCode == RESULT_OK) {}
        }
    }

    public boolean validate(String email, String password) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("유효하지 않은 이메일 형식입니다.");
            return false;
        }

        if (password.isEmpty() || password.length() < 5) {
            loginPassword.setError("비밀번호는 최소 5자 이상입니다.");
            return false;
        }

        return true;
    }

    // 로그인 성공 시
    public void onLoginSuccess(String[] userInfo) {
        sessionManager.createSession(userInfo[0], userInfo[1]); // 세션에 회원 정보 저장
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class); // 실행할 엑티비티 생성
        mainIntent.putExtra(USER_INFO, userInfo); // 실행 될 엑티비티에 전달할 유저정보 저장
        startActivity(mainIntent); // 액티비티 실행
        finish(); // 엑티비티 종료
    }

    // 로그인 실패 시
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "계정이메일 혹은 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
        BtnLogin.setEnabled(true);
    }
}
