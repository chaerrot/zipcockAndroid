package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class HInfoUpdate extends AppCompatActivity {

    public static final String TAG = "zipcock";

    Spinner hjoin_age, hjoin_bank;
    EditText hjoin_password, hjoin_email, hjoin_phone,
            hjoin_pwck, hjoin_account, hjoin_introduce;
    TextView hjoin_id, hjoin_name;
    String[] jage = {"나이 고르기", "10대", "20대", "30대", "40대", "50대", "기타"};
    String[] jban = {"은행" ,"기업은행", "국민은행", "우리은행", "신한은행",
            "하나은행", "농협은행", "SC은행", "카카오뱅크"};
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hinfo_update);

        String ip = getResources().getString(R.string.ip);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("member_id");
        final String name = intent.getStringExtra("member_name");
        final String pass = intent.getStringExtra("member_pass");
        final String email = intent.getStringExtra("member_email");
        final String age = intent.getStringExtra("member_age");
        final String phone = intent.getStringExtra("member_phone");
        final String bank = intent.getStringExtra("member_bank");
        final String account = intent.getStringExtra("member_account");
        final String introduce = intent.getStringExtra("member_introduce");


        ((TextView)findViewById(R.id.hjoin_name)).setText(String.format(name));
        ((TextView)findViewById(R.id.hjoin_id)).setText(String.format(id));
        ((EditText)findViewById(R.id.hjoin_password)).setText(String.format(pass));
        ((EditText)findViewById(R.id.hjoin_email)).setText(String.format(email));
        ((EditText)findViewById(R.id.hjoin_phone)).setText(String.format(phone));
        ((EditText)findViewById(R.id.hjoin_account)).setText(String.format(account));
        ((EditText)findViewById(R.id.hjoin_introduce)).setText(String.format(introduce));

        hjoin_name = findViewById(R.id.hjoin_name);
        hjoin_id = findViewById(R.id.hjoin_id);
        hjoin_password = findViewById(R.id.hjoin_password);
        hjoin_pwck = findViewById(R.id.hjoin_pwck);
        hjoin_email = findViewById(R.id.hjoin_email);
        hjoin_age = findViewById(R.id.hjoin_age);
        hjoin_phone = findViewById(R.id.hjoin_phone);
        hjoin_bank = findViewById(R.id.hjoin_bank);
        hjoin_account = findViewById(R.id.hjoin_account);
        hjoin_introduce = findViewById(R.id.hjoin_introduce);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, jban
        );

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hjoin_bank.setAdapter(adapter1);
        //인덱스 알아보기
        int bIndex = Arrays.asList(jban).indexOf(bank);
        Log.d(TAG, ""+bIndex);
        hjoin_bank.setSelection(bIndex);
        hjoin_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, jage
        );

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hjoin_age.setAdapter(adapter2);
        //인덱스 알아보기
        int aIndex = Arrays.asList(jage).indexOf(age);
        Log.d(TAG, ""+aIndex);
        hjoin_age.setSelection(aIndex);
        hjoin_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                if(hjoin_password.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_password.requestFocus();
                    return;
                }

                if(hjoin_pwck.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "비밀번호확인을 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_pwck.requestFocus();
                    return;
                }

                if(hjoin_email.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_email.requestFocus();
                    return;
                }

                if(hjoin_age.getSelectedItem().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "나이대를 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_age.requestFocus();
                    return;
                }

                if(hjoin_phone.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_phone.requestFocus();
                    return;
                }

                if(hjoin_bank.getSelectedItem().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "은행을 선택하세요", Toast.LENGTH_SHORT).show();
                    hjoin_bank.requestFocus();
                    return;
                }

                if(hjoin_account.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "계좌번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_account.requestFocus();
                    return;
                }

                if(hjoin_introduce.getText().toString() == null){
                    Toast.makeText(HInfoUpdate.this, "자기소개를 입력하세요", Toast.LENGTH_SHORT).show();
                    hjoin_introduce.requestFocus();
                    return;
                }

                if( !hjoin_password.getText().toString().equals(hjoin_pwck.getText().toString())){
                    Toast.makeText(HInfoUpdate.this, "비밀번호 불일치", Toast.LENGTH_SHORT).show();
                    hjoin_pwck.requestFocus();
                    return;
                }

                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/hmemberInfoEdit.do";
                requestUrl += "?member_pass=" + hjoin_password.getText() + "&member_email=" + hjoin_email.getText();
                requestUrl += "&member_age=" + hjoin_age.getSelectedItem() + "&member_phone=" + hjoin_phone.getText() + "&member_name=" + hjoin_name.getText();
                requestUrl += "&member_bank=" + hjoin_bank.getSelectedItem() + "&member_account=" + hjoin_account.getText() + "&member_introduce=" + hjoin_introduce.getText() + "&member_id=" + hjoin_id.getText();;


                Log.d(TAG, requestUrl);

                new HInfoUpdate.AsyncHttpRequest().execute(requestUrl);

                Toast.makeText(getApplicationContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HelperMain.class);
                intent.putExtra("member_id", id);
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
                Log.i("TAG일까나", sBuffer.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return sBuffer.toString();
        }
    }
}