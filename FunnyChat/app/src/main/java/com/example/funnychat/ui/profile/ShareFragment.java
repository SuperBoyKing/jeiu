package com.example.funnychat.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.funnychat.R;
import com.example.funnychat.background.DBConnector;
import com.example.funnychat.background.GetterChat;
import com.example.funnychat.background.SessionManager;
import com.example.funnychat.foreground.LoginActivity;
import com.example.funnychat.foreground.MainActivity;
import com.example.funnychat.foreground.SignupActivity;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
    TextView email;
    EditText name;
    EditText password;
    EditText confirm_password;
    Button ok;
    Button cancel;
    SessionManager sessionManager;
    String mName;
    String mEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        email = root.findViewById(R.id.profile_email);
        name = root.findViewById(R.id.profile_name);
        password = root.findViewById(R.id.password);
        confirm_password = root.findViewById(R.id.confirm_password);
        ok = root.findViewById(R.id.ok);
        cancel = root.findViewById(R.id.cancel);

        sessionManager = new SessionManager(this.getContext());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        this.mName = user.get(sessionManager.NAME);
        this.mEmail = user.get(sessionManager.EMAIL);

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { updateProfile(); }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { getFragmentManager().popBackStack(); }
        });

        try {
            DBConnector dbConnector = new DBConnector();
            String data = dbConnector.execute(mEmail, mName, null, "select").get();
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject)parser.parse(data);
            email.setText(obj.get("email").getAsString());
            name.setText(obj.get("name").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    void updateProfile() {
        String profileEmail = email.getText().toString();
        String profileName = name.getText().toString();
        String proFilePassword = password.getText().toString();

        if (!validate(proFilePassword, profileName)) return;
        ok.setEnabled(false);
        cancel.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext(),
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
            result = DBConnector.execute(profileEmail, proFilePassword, profileName, "update").get();
            if (result.equals(" success")) {
                sessionManager.createSession(profileEmail, profileName);
                onUpdateSuccess();
            } else {
                onUpdateFailed();
            }
            Log.i("리턴 값 = ", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean validate(String proFilePassword, String profileName) {
        String conPassword = confirm_password.getText().toString();

        if (proFilePassword.isEmpty()) {
            password.setError("비밀번호를 입력하세요.");
            return false;
        } else if (proFilePassword.length() < 5) {
            password.setError("비밀번호는 최소 5자 이상입니다.");
            return false;
        } else if (proFilePassword.length() >= 65){
            password.setError("제발 짧게 좀 하세요.");
            return false;
        } else if (proFilePassword.compareTo(conPassword) != 0) {
            confirm_password.setError("비밀번호 확인이 일치하지 않습니다.");
            return false;
        } else {
            password.setError(null);
        }

        if (profileName.isEmpty() || profileName.length() < 4) {
            name.setError("별칭은 최소 4자 이상이어야 합니다.");
            return false;
        }

        return true;
    }

    void onUpdateSuccess() {
        Toast.makeText(this.getContext(), "프로필 수정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        //sessionManager.logout();
        /*ok.setEnabled(true);
        cancel.setEnabled(true);*/
        Intent mainIntent = new Intent(this.getContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    void onUpdateFailed() {
        Toast.makeText(this.getContext(), "수정 오류! 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
        ok.setEnabled(true);
        cancel.setEnabled(true);
    }
}