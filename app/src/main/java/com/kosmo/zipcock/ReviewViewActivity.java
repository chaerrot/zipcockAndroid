package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReviewViewActivity extends AppCompatActivity {

    private static final String TAG = "lecture";
    private RatingBar ratingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);

        String ip = getResources().getString(R.string.ip);

        Log.i(TAG,ip);

        final Intent intent = getIntent();
        final String num = intent.getStringExtra("review_num");
        final String point = intent.getStringExtra("review_point");
        final String id = intent.getStringExtra("review_id");
        final String content = intent.getStringExtra("review_content");

        ((TextView)findViewById(R.id.RevIdx)).setText(String.format(num));

        ((TextView)findViewById(R.id.RevId)).setText(String.format(id));
        ((TextView)findViewById(R.id.RevCon)).setText(String.format(content));

        String p = point;
        switch (p){
            case "1" :
                ((RatingBar)findViewById(R.id.RevPo)).setRating(1);
                break;
            case "2" :
                ((RatingBar)findViewById(R.id.RevPo)).setRating(2);
                break;
            case "3" :
                ((RatingBar)findViewById(R.id.RevPo)).setRating(3);
                break;
            case "4" :
                ((RatingBar)findViewById(R.id.RevPo)).setRating(4);
                break;
            case "5" :
                ((RatingBar)findViewById(R.id.RevPo)).setRating(5);
                break;
            default:
                Log.i(TAG, "잘못누름");
                break;
        }


        Button Delete = (Button)findViewById(R.id.Delete);
        Button Modify = (Button) findViewById(R.id.Modify);
        Modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReviewModifyActivity.class);
                intent.putExtra("review_num", num);
                intent.putExtra("review_point", point);
                intent.putExtra("review_id", id);
                intent.putExtra("review_content", content);

                startActivity(intent);
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/Reviewdelete.do";
                requestUrl += "?review_num="+ num;
                requestUrl += "&review_id=" + id;

                Log.d(TAG, requestUrl);

                new ReviewViewActivity.AsyncHttpRequest().execute(requestUrl);

                Toast.makeText(getApplicationContext(), "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                startActivity(intent);
            }
        });
    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                OutputStream out = connection.getOutputStream();
                out.flush();
                out.close();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK 성공");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null){
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                }
                else {
                    Log.i(TAG, "HTTP OK 안됨");
                }
                Log.i(TAG, sBuffer.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return sBuffer.toString();
        }
    }
}