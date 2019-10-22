package com.example.funnychat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funnychat.background.DBConnector;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText signupEmail;
    EditText signupPassword;
    EditText confirmPassword;
    EditText signupNickname;
    Button btnSignup;
    TextView LoginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        signupEmail = findViewById(R.id.sign_email);
        signupPassword = findViewById(R.id.sign_password);
        confirmPassword = findViewById(R.id.sign_passwordConfirm);
        signupNickname = findViewById(R.id.sign_nickname);
        btnSignup = findViewById(R.id.btn_signup);
        LoginLink = findViewById(R.id.txt_toLogin);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void signup() {

        Log.d(TAG, "Signup");
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String name = signupNickname.getText().toString();
        if (!validate(email, password, name)) return;
        btnSignup.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                                              R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);

        try {
            String result;
            DBConnector DBConnector = new DBConnector();
            result = DBConnector.execute(email, password, name, "signup").get();
            if (result.equals("true")) {
                onSignupSuccess();
            } else {
                onSignupFailed();
            }
            Log.i("리턴 값 = ", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected boolean validate(String email, String password, String name) {
        String conPassword = confirmPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.setError("유효하지 않은 이메일 형식입니다.");
            return false;
        }

        if (password.isEmpty()) {
            signupPassword.setError("비밀번호를 입력하세요.");
            return false;
        } else if (password.length() < 5) {
            signupPassword.setError("비밀번호는 최소 5자 이상입니다.");
            return false;
        } else if (password.length() >= 65){
            signupPassword.setError("제발 짧게 좀 하세요.");
            return false;
        } else if (password.compareTo(conPassword) != 0) {
            confirmPassword.setError("비밀번호 확인이 일치하지 않습니다.");
            return false;
        } else {
            signupPassword.setError(null);
        }

        if (name.isEmpty() || name.length() < 4) {
            signupNickname.setError("별칭은 최소 4자 이상이어야 합니다.");
            return false;
        }

        return true;
    }


    public void onSignupSuccess() {
        Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }


    public void onSignupFailed() {
        Toast.makeText(this, "이미 존재하는 이메일입니다.",Toast.LENGTH_SHORT).show();
        btnSignup.setEnabled(true);
    }
}