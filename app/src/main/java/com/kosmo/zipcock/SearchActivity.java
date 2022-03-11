package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = "zipcock";
    EditText SearchName, SearchEmail, SearchId, SearchNamePw, SearchemailPw;
    String tsearchName, tsearchEmail, tsearchId, tsearchNamePw, tsearchemailPw;
    ProgressDialog dialog, dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("아디 찾는 중");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");
        //비번
        dialog2 = new ProgressDialog(this);
        dialog2.setCancelable(true);
        dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog2.setIcon(android.R.drawable.ic_dialog_email);
        dialog2.setTitle("비번 찾는 중");
        dialog2.setMessage("서버로부터 응답을 기다리고 있습니다.");

        setContentView(R.layout.activity_search);
        SearchName = findViewById(R.id.SearchName); // 아이디 찾기의 이름 입력 란
        SearchEmail = findViewById(R.id.SearcheEail); // 아아디 찾기의 이메일 입력 란
        SearchId = (EditText) findViewById(R.id.SearchId); // 비밀번호 찾기의 아이디 입력 란
        SearchNamePw = (EditText) findViewById(R.id.SearchNamePw); // 비밀번호 찾기의 이름 입력 란
        SearchemailPw = (EditText) findViewById(R.id.SearchEmailPw); // 비밀번호 찾기의 생년월일 입력 란
        Button searchIdbtn = (Button) findViewById(R.id.SearchIdbtn);
        Button searchPwbtn = (Button) findViewById(R.id.SearchPwbtn);

        searchIdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = getResources().getString(R.string.ip);
                String searchName = SearchName.getText().toString().trim();
                String searchEmail = SearchEmail.getText().toString().trim();
                if (searchName.length() == 0 || searchEmail.length() == 0) {
                    Toast.makeText(getApplicationContext(), "빈칸 없이 입력 해주세요", Toast.LENGTH_SHORT).show();

                }
                String requestUrl =
                            "http://"+ip+":8081/zipcock/android/findId.do";
                requestUrl += "?member_name=" + SearchName.getText().toString();
                requestUrl += "&member_email=" + SearchEmail.getText().toString();
                new AsyncHttpRequest().execute(requestUrl);


            }
        });
        searchPwbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = getResources().getString(R.string.ip);
                String searchId = SearchId.getText().toString().trim();
                String tsearchNamePw = SearchNamePw.getText().toString().trim();
                String tsearchemailPw = SearchemailPw.getText().toString().trim();
                if ( searchId.length()==0 || tsearchNamePw.length() == 0 || tsearchemailPw.length() == 0) {
                    Toast.makeText(getApplicationContext(), "빈칸 없이 입력 해주세요", Toast.LENGTH_SHORT).show();

                }
                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/findPass.do";
                requestUrl += "?member_id=" + SearchId.getText().toString();
                requestUrl += "&member_name=" + SearchNamePw.getText().toString();
                requestUrl += "&member_email=" + SearchemailPw.getText().toString();
                new AsyncHttpRequest1().execute(requestUrl);
            }
        });

    }
    //비밀번호 찾기
    class AsyncHttpRequest1 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog2.isShowing())
                dialog2.show();
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
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(TAG, "HTTP OK 성공");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                } else {
                    Log.i(TAG, "HTTP OK 안됨");
                }
                Log.i(TAG, sBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog2.dismiss();
            Log.i(TAG, s);
            String result = jsonParser1(s);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

        }
    }
    public String jsonParser1(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("findPass"));
            if (success==1){
                JSONObject mInfo = jsonObject.getJSONObject("memberfindPass");
                String findpass = mInfo.toString();
                String pass = mInfo.getString("member_pass").toString();
                Toast.makeText(getApplicationContext(),"비밀번호:"+pass+"입니다.",Toast.LENGTH_SHORT).show();

            }
            else {
                sb.append("찾을 수 없는 정보입니다.");

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    //아이디찾기
    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
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
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(TAG, "HTTP OK 성공");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                } else {
                    Log.i(TAG, "HTTP OK 안됨");
                }
                Log.i(TAG, sBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(TAG, s);
            dialog.dismiss();
            String result = jsonParser(s);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            String result1 = jsonParser1(s);
            Toast.makeText(getApplicationContext(),result1,Toast.LENGTH_SHORT).show();
        }
    }

    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("findid"));
            if (success==1){
                JSONObject mInfo = jsonObject.getJSONObject("memberfindId");
                String find = mInfo.toString();
                String id = mInfo.getString("member_id").toString();
                Toast.makeText(getApplicationContext(),"아이디:"+id+"입니다.",Toast.LENGTH_SHORT).show();

            }
            else {
                sb.append("찾을 수 없는 정보입니다.");

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();

    }



}
