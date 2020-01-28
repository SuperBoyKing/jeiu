package com.example.funnychat.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.funnychat.chat.MyHttpEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtilsHC4;

import java.io.File;

public class FileUploader extends AsyncTask<Void, Integer, String> {

    HttpClient httpClient = HttpClientBuilder.create().build();
    private Context context;
    Exception exception;
    private ProgressDialog progressDialog;
    private File file;
    HostConnector hostConnector;

    public FileUploader(Context context, File file) {
        this.context = context;
        this.file = file;
        hostConnector = new HostConnector();
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpResponse httpResponse;
        HttpEntity httpEntity;
        String responseString = null;
        final String SERVER_PATH = "http://" + hostConnector.getHostName() +":8080/FunnyChatServer/Android/index.jsp";

        try {
            HttpPostHC4 httpPost = new HttpPostHC4(SERVER_PATH);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addPart("file", new FileBody(file));

            MyHttpEntity.ProgressListener progressListener = new MyHttpEntity.ProgressListener() {
                @Override
                public void transferred(float progress) {
                    publishProgress((int) progress);
                }
            };
            httpPost.setEntity(new MyHttpEntity(multipartEntityBuilder.build(), progressListener));
            httpResponse = httpClient.execute(httpPost);
            httpEntity = httpResponse.getEntity();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                responseString = EntityUtilsHC4.toString(httpEntity);
            } else {
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("UPLOAD", e.getMessage());
            this.exception = e;
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        this.progressDialog.dismiss();
        Toast.makeText(context,"파일전송완료!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        this.progressDialog.setProgress(progress[0]);
    }
}
