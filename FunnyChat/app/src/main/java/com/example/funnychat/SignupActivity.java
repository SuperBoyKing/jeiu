package com.example.funnychat;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @BindView(R.id.sign_email) EditText signupEmail;
    @BindView(R.id.sign_password) EditText signupPassword;
    @BindView(R.id.sign_passwordConfirm) EditText confirmPassword;
    @BindView(R.id.sign_nickname) EditText signupNickname;
    @BindView(R.id.btn_signup) Button btnSignup;
    @BindView(R.id.txt_toLogin) TextView LoginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        ButterKnife.bind(this);

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
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String nickName = signupNickname.getText().toString();

        Log.d(TAG, "Signup");

        SQLiteDatabase db = new FunnyDBHelper(this).getWritableDatabase();

        if (!validate(email, password, nickName, db)) return;

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                                              R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        btnSignup.setEnabled(false);
        ContentValues values = new ContentValues();
        values.put(FunnyDBContract.ChatUser.COLUMN_EMAIL, email);
        values.put(FunnyDBContract.ChatUser.COLUMN_PASSWORD, password);
        values.put(FunnyDBContract.ChatUser.COLUMN_NICKNAME, nickName);

        try {
            Calendar calendar = Calendar.getInstance();
            Date dt = calendar.getTime();
            long date = calendar.getTimeInMillis();
            values.put(FunnyDBContract.ChatUser.COLUMN_FOUNDED_DATE, date);
        }
        catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }
        long newColumn = db.insert(FunnyDBContract.ChatUser.TABLE_NAME, null, values);
        Toast.makeText(this, "success signup! " + newColumn , Toast.LENGTH_LONG).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 1000);

    }


    public boolean validate(String email, String password, String nickName, SQLiteDatabase db) {
        boolean valid = true;

        String conPassword = confirmPassword.getText().toString();
        Cursor cs = db.rawQuery("SELECT * FROM chatUser WHERE email =?", new String[] {email});

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.setError("유효하지 않은 이메일 형식입니다.");
            valid = false;
        } else if (cs != null) {
            if (cs.getCount() > 0)
            {
                signupEmail.setError("중복된 이메일 입니다.");
                valid = false;
            }
        } else {
            signupEmail.setError(null);
        }

        if (password.isEmpty()) {
            signupPassword.setError("비밀번호를 입력하세요.");
            valid = false;
        } else if (password.length() < 5) {
            signupPassword.setError("비밀번호는 최소 5자 이상입니다.");
            valid = false;
        } else if (password.compareTo(conPassword) != 0) {
            confirmPassword.setError("비밀번호 확인이 일치하지 않습니다.");
            valid = false;
        } else {
            signupPassword.setError(null);
        }

        cs = db.rawQuery("SELECT * FROM chatUser WHERE nickName=?", new String[] {nickName});
        if (nickName.isEmpty() || nickName.length() < 4) {
            signupNickname.setError("별칭은 최소 4자 이상이어야 합니다.");
            valid = false;
        } else if (cs != null) {
            if (cs.getCount() > 0)
            {
                signupNickname.setError("중복되는 별칭입니다.");
                valid = false;
            }
        } else {
            signupNickname.setError(null);
        }

        return valid;
    }


    public void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }


    public void onSignupFailed() {
        Toast.makeText(this, "회원가입 실패",Toast.LENGTH_SHORT).show();
        btnSignup.setEnabled(true);
    }
}
