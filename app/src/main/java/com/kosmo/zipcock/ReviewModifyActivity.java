package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReviewModifyActivity<ratingbar> extends AppCompatActivity {

    public static final String TAG = "zipcock";

    TextView review_id, review_num;
    EditText review_content, review_point;
    private RatingBar ratingbar;
    
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_modift_activty);

        String ip = getResources().getString(R.string.ip);

        final Intent intent = getIntent();
        final String num = intent.getStringExtra("review_num");
        final String point = intent.getStringExtra("review_point");
        final String id = intent.getStringExtra("review_id");
        final String content = intent.getStringExtra("review_content");

        ((TextView)findViewById(R.id.RevMIdx)).setText(String.format(num));
        ((RatingBar)findViewById(R.id.RevMPoBar)).setRating(Integer.parseInt(point));
        ((TextView)findViewById(R.id.RevMPo)).setText(String.format(point));
        ((TextView)findViewById(R.id.RevMId)).setText(String.format(id));
        ((EditText)findViewById(R.id.RevMCon)).setText(String.format(content));
        
        review_id = findViewById(R.id.RevMId);
        review_num = findViewById(R.id.RevMIdx);
        review_content = findViewById(R.id.RevMCon);
        review_point = findViewById(R.id.RevMPo);

        RatingBar RevMPoBar = findViewById(R.id.RevMPoBar);
        RevMPoBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ((RatingBar)findViewById(R.id.RevMPoBar)).setRating(rating);
                review_point.setText("" + rating);
            }
        });


        Button Mcomplete = (Button) findViewById(R.id.Mcomplete);
        Mcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/Reviewmodify.do";
                requestUrl += "?review_num=" + review_num.getText();
                requestUrl += "&review_content=" + review_content.getText() + "&review_point=" + review_point.getText();

                Log.d(TAG, requestUrl);

                new AsyncHttpRequest().execute(requestUrl);

                Toast.makeText(getApplicationContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                intent.putExtra("member_id", id);
                startActivity(intent);
            }
        });

    }
    class AsyncHttpRequest extends AsyncTask<String, Void, String>{
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
                Log.i("TAG일까나", sBuffer.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return sBuffer.toString();
        }
    }




}