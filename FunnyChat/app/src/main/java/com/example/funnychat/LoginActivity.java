package com.example.funnychat;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

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

        String email = TxtEmail.getText().toString();
        String password = TxtPassword.getText().toString();

        if (!validate(email, password)) {
            onLoginFailed();
            return false;
        }

        BtnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        SQLiteDatabase db = new FunnyDBHelper(this).getReadableDatabase();
        final String tableName = FunnyDBContract.ChatUser.TABLE_NAME;
        final String columnEmail = FunnyDBContract.ChatUser.COLUMN_EMAIL;
        final String columnPassword = FunnyDBContract.ChatUser.COLUMN_PASSWORD;


        Cursor cursor =  db.rawQuery("SELECT * FROM " + tableName +
                        " WHERE columnEmail =? AND columnPassword =?",
                new String[]{email,password} );

        if (cursor != null) {
            if (cursor.getCount() > 0)
            {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginSuccess();
                                progressDialog.dismiss();
                            }
                        },3000);

                return true;
            }
        }
        //progressDialog.dismiss();
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SINGUP) {
            if (resultCode == RESULT_OK) {

            }
        }
    }


    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            TxtEmail.setError("이메일 형식이 올바르지 않습니다.");
            valid = false;
        } else {
            TxtEmail.setError(null);
        }

        if (password.isEmpty() ) {
            TxtPassword.setError("비밀번호를 입력해 주세요.");
            valid = false;
        } else if(password.length() < 5) {
            TxtPassword.setError("비밀번호는 최소 5자 이상입니다.");
            valid = false;
        } else {
            TxtPassword.setError(null);
        }

        return valid;
    }


    public void onLoginSuccess() {
        BtnLogin.setEnabled(true);
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
        BtnLogin.setEnabled(true);
    }
}
