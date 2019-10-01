package com.example.funnychat;

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

import com.example.funnychat.background.ConnectDB;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String USER_INFO= "user_Info";

    private static final int REQUEST_SINGUP = 0;

    @BindView(R.id.login_email) EditText loginEmail;
    @BindView(R.id.login_password) EditText loginPassword;
    @BindView(R.id.btn_login) Button BtnLogin;
    @BindView(R.id.txt_Register) TextView SignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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


    public boolean login() throws SQLException {
        Log.d(TAG, "Login");

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        String name = null;

        if (!validate(email, password))  return false;
        BtnLogin.setEnabled(false);
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
                }, 1000);

        try {

            ConnectDB connectDB = new ConnectDB();
            String result = connectDB.execute(email, password, name, "login").get();
            if (result.equals("false")) {
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


    public void onLoginSuccess(String[] userInfo) {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.putExtra(USER_INFO, userInfo);
        startActivity(mainIntent);
        finish();
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "가입되지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
        BtnLogin.setEnabled(true);
    }
}
