package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class InfoUpdate extends AppCompatActivity {

    public static final String TAG = "zipcock";

    Spinner join_age;
    EditText join_password, join_email, join_phone, join_pwck;
    TextView join_id, join_name;
    String[] jage = {"나이 고르기", "10대", "20대", "30대", "40대", "50대", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_update);

        String ip = getResources().getString(R.string.ip);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("member_id");
        final String name = intent.getStringExtra("member_name");
        final String pass = intent.getStringExtra("member_pass");
        final String email = intent.getStringExtra("member_email");
        final String age = intent.getStringExtra("member_age");
        final String phone = intent.getStringExtra("member_phone");


        ((TextView)findViewById(R.id.join_name)).setText(String.format(name));
        ((TextView)findViewById(R.id.join_id)).setText(String.format(id));
        ((EditText)findViewById(R.id.join_password)).setText(String.format(pass));
        ((EditText)findViewById(R.id.join_email)).setText(String.format(email));
        ((EditText)findViewById(R.id.join_phone)).setText(String.format(phone));

        join_name = findViewById(R.id.join_name);
        join_id = findViewById(R.id.join_id);
        join_password = findViewById(R.id.join_password);
        join_pwck = findViewById(R.id.join_pwck);
        join_email = findViewById(R.id.join_email);
        join_age = findViewById(R.id.join_age);
        join_phone = findViewById(R.id.join_phone);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, jage
        );

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        join_age.setAdapter(adapter2);
        //인덱스 알아보기
        int aIndex = Arrays.asList(jage).indexOf(age);
        Log.d(TAG, ""+aIndex);
        join_age.setSelection(aIndex);
        join_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(join_password.getText().toString() == null){
                    Toast.makeText(InfoUpdate.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    join_password.requestFocus();
                    return;
                }

                if(join_pwck.getText().toString() == null){
                    Toast.makeText(InfoUpdate.this, "비밀번호확인을 입력하세요", Toast.LENGTH_SHORT).show();
                    join_pwck.requestFocus();
                    return;
                }

                if(join_email.getText().toString() == null){
                    Toast.makeText(InfoUpdate.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    join_email.requestFocus();
                    return;
                }

                if(join_age.getSelectedItem().toString() == null){
                    Toast.makeText(InfoUpdate.this, "나이대를 선택하세요", Toast.LENGTH_SHORT).show();
                    join_age.requestFocus();
                    return;
                }

                if(join_phone.getText().toString() == null){
                    Toast.makeText(InfoUpdate.this, "번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    join_phone.requestFocus();
                    return;
                }
                
                if( !join_password.getText().toString().equals(join_pwck.getText().toString())){
                    Toast.makeText(InfoUpdate.this, "비밀번호 불일치", Toast.LENGTH_SHORT).show();
                    join_password.requestFocus();
                    return;
                }

                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/memberInfoEdit.do";
                requestUrl += "?member_pass=" + join_password.getText() + "&member_email=" + join_email.getText();
                requestUrl += "&member_age=" + join_age.getSelectedItem() + "&member_phone=" + join_phone.getText() + "&member_name=" + join_name.getText();
                requestUrl += "&member_id=" + join_id.getText();

                Log.d(TAG, requestUrl);

                new AsyncHttpRequest().execute(requestUrl);

                Toast.makeText(getApplicationContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ZipMain.class);
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