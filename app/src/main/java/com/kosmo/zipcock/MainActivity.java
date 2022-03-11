package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    SharedPreferences.Editor editor;
    public static final String TAG = "zipcock";

    EditText user_id, user_pw;
    ProgressDialog dialog;
    TextView failtext;
    TextView memberChoice, member_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("로그인 처리중");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");

        user_id = findViewById(R.id.user_id);
        user_pw = findViewById(R.id.user_pw);
        failtext = findViewById(R.id.failtext);
        Button btnLogin = findViewById(R.id.loginbutton);

        memberChoice = findViewById(R.id.textView5);
        member_search = findViewById(R.id.textView2);

        /* 아이디 / 비밀번호 찾기  이동 */
        member_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        /****** 회원가입 페이지로 이동 *******/
        memberChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberChoice.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            String ip = getResources().getString(R.string.ip);
            @Override
            public void onClick(View v) {
                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/memberLogin.do";
                requestUrl += "?member_id=" + user_id.getText().toString();
                requestUrl += "&member_pass=" + user_pw.getText().toString();
                Log.d(TAG, requestUrl);

                //SharePreferences에 아이디
                String id = user_id.getText().toString();
                SharedPreferences pref = getSharedPreferences("user_id",MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("member_id", id);
                editor.commit();
                //저장끝

                getHashKey();
                new AsyncHttpRequest().execute(requestUrl);


            }

        });
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!dialog.isShowing())
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

        //로그인실패시 알림
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(TAG, s);
            dialog.dismiss();
            String result = jsonParser(s);
            failtext.setText(result);
        }
    }
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("isLogin"));
            if (success==1){
                JSONObject mInfo = jsonObject.getJSONObject("memberInfo");
                String memberInfo = mInfo.toString();
                String name = mInfo.getString("member_name").toString();

                Toast.makeText(getApplicationContext(), name+"님 환영합니다!", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getApplicationContext(), ZipMain.class);
                startActivity(intent);

            }
            else {
                sb.append("아이디나 비밀번호를 확인해주세요.");

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}






