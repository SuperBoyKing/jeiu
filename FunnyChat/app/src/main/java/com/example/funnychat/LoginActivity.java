package com.example.funnychat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SINGUP = 0;

    @BindView(R.id.txt_email) EditText TxtEmail;
    @BindView(R.id.txt_password) EditText TxtPassword;
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
}

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        BtnLogin.setEnabled(false);

        String email = TxtEmail.getText().toString();
        String password = TxtPassword.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (request == REQUEST_SINGUP) {
            if (result == RESULT_OK) {

                this.finish();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = TxtEmail.getText().toString();
        String password = TxtPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            TxtEmail.setError("이메일 형식이 올바르지 않습니다.");
            valid = false;
        } else {
            TxtEmail.setError(null);
        }

        if (password.isEmpty()) {
            TxtPassword.setError("비밀번호를 입력해 주세요.");
            valid = false;
        } else {
            TxtPassword.setError(null);
        }

        return valid;
    }

    public void onLoginSuccess() {
        BtnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
        BtnLogin.setEnabled(true);
    }
}
