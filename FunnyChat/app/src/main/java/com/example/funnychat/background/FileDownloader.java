package com.example.funnychat.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.funnychat.chat.ClientActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDownloader extends AsyncTask<String, String, String> {

    private static final String TAG = ClientActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Context context;
    private String fileName;
    private String folder;

    public FileDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    protected String doInBackground(String... file_url) {
        int count;
        try {
            URL url = new URL(file_url[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int lengthOfFile = conn.getContentLength();

            fileName = file_url[0].substring(file_url[0].lastIndexOf('/') + 1, file_url[0].length());
            folder = Environment.getExternalStorageDirectory() + File.separator +"funnyDownloader/";
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            File directory = new File(folder);
            File newFile = new File(directory.getAbsolutePath() + fileName);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (!newFile.exists()) {
                newFile.createNewFile();
            }

            OutputStream fos = new FileOutputStream(folder + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                Log.d(TAG, "진행률 : " + (int) ((total * 100) / lengthOfFile));
                bos.write(data, 0, count);
            }

            bos.flush();

            bos.close();
            fos.close();
            input.close();
            return "Downloaded at: " + folder + fileName;
        } catch (Exception e) {
            Log.e("Error ", e.getMessage());
        }
        return "잠시 후 다시 시도하세요.";
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        progressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String message) {
        this.progressDialog.dismiss();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}