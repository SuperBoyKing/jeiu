package com.example.funnychat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        SQLiteDatabase db = new FunnyDBHelper(this).getReadableDatabase();
        String selectQuery = "SELECT * FROM chatUser WHERE email =? AND password =?";
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (!validate(email, password))  return false;
        BtnLogin.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                              R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, password} );

        if (cursor != null) {
            if (cursor.getCount() > 0)
            {
                String userName = null;
                if  (cursor.moveToFirst())
                {
                    do {
                      userName = cursor.getString(cursor.getColumnIndex("nickName"));
                    }while (cursor.moveToNext());
                }

                String userInfo[] = {email, userName};
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginSuccess(userInfo);
                                progressDialog.dismiss();
                            }
                        }, 500);
                return true;
            }
        } else {
            onLoginFailed(progressDialog);
            return false;
        }

        onLoginFailed(progressDialog);
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SINGUP) {
            if (resultCode == RESULT_OK) {}
        }
    }


    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("이메일 형식이 올바르지 않습니다.");
            valid = false;
        } else {
            loginEmail.setError(null);
        }

        if (password.isEmpty()) {
            loginPassword.setError("비밀번호를 입력해 주세요.");
            valid = false;
        } else if(password.length() < 5) {
            loginPassword.setError("비밀번호는 최소 5자 이상입니다.");
            valid = false;
        } else {
            loginPassword.setError(null);
        }

        return valid;
    }


    public void onLoginSuccess(String[] userInfo) {
        BtnLogin.setEnabled(true);
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.putExtra(USER_INFO, userInfo);
        startActivity(mainIntent);
        finish();
    }


    public void onLoginFailed(ProgressDialog progressDialog) {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "가입되지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
        BtnLogin.setEnabled(true);
    }
}
