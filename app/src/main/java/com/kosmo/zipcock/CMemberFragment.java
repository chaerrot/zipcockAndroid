package com.kosmo.zipcock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CMemberFragment extends AppCompatActivity {

    private static final String TAG = "zipcock";

    EditText cId, cPass, cPass2, cEmail, cName, cPhone;
    Spinner cSex, cAge;
    TextView passCheck;
    Button check, member;
    ProgressDialog dialog;

    String[] sex = {"성별" ,"남자", "여자"}; // 0 남자, 1여자
    String[] age = {"나이", "10대", "20대", "30대", "40대", "50대", "기타"}; // 숫자 변경 후 서버에서 다시 처리


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_c_member);

        cId = findViewById(R.id.cId);
        cPass = findViewById(R.id.cPass);
        cPass2 = findViewById(R.id.cPass2);
        cEmail = findViewById(R.id.cEmail);
        cName = findViewById(R.id.cName);
        cPhone = findViewById(R.id.cPhone);

        cSex = findViewById(R.id.cSex);
        cAge = findViewById(R.id.cAge);

        check = findViewById(R.id.check);
        member = findViewById(R.id.member);
        passCheck = findViewById(R.id.passCheck);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("아이디 확인중..");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");

        /* 비밀번호 일치여부 */
        cPass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cPass2.getText().toString().equals(cPass.getText().toString())) {
                    passCheck.setText("비밀번호가 일치합니다.");
                }
                else {
                    passCheck.setText("비밀번호가 일치하지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* 성별, 나이 */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, sex
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cSex.setAdapter(adapter);
        cSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getSex;
                if(position == 1) {
                    getSex = "1";
                }
                else if(position == 2) {
                    getSex = "2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, age
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        cAge.setAdapter(adapter2);
        cAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /********************** 아이디 중복확인 버튼 리스너*****************************/
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = getResources().getString(R.string.ip);
                String requestUrl = "http://"+ip+":8081/zipcock/android/memberCheck.do";
                requestUrl +="?member_id="+cId.getText().toString();
                Log.d(TAG, requestUrl);

                new AsyncHttpRequest().execute(requestUrl);
            }
        });

        /**************** 회원가입 버튼 리스너 *******************/
        member.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(cId.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    cId.requestFocus();
                    return;
                }
                if(cPass.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show();
                    cPass.requestFocus();
                    return;
                }
                if(cPass2.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "패스워드확인을 해주세요", Toast.LENGTH_SHORT).show();
                    cPass2.requestFocus();
                    return;
                }
                if(passCheck.getText().toString().equals("비밀번호가 일치하지 않습니다.")) {
                    Toast.makeText(getApplicationContext(), "비밀번호 일치여부 확인해주세요", Toast.LENGTH_SHORT).show();
                    cPass2.requestFocus();
                    return;
                }
                if(cEmail.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    cEmail.requestFocus();
                    return;
                }
                if(cName.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    cName.requestFocus();
                    return;
                }
                if(cAge.getSelectedItem().toString() == "성별") {
                    Toast.makeText(getApplicationContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
                    cAge.requestFocus();
                    return;
                }
                if(cSex.getSelectedItem().toString() == "나이") {
                    Toast.makeText(getApplicationContext(), "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
                    cSex.requestFocus();
                    return;
                }
                if(cPhone.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    cPhone.requestFocus();
                    return;
                }

                String getSex = "";
                if(cSex.getSelectedItem().toString().equals("남자")) {
                    getSex = "0";
                }
                else if(cSex.getSelectedItem().toString().equals("여자")) {
                    getSex = "1";
                }

                String getAge = "";
                if(cAge.getSelectedItem().toString().equals("10대")) {
                    getAge = "10";
                }
                else if(cAge.getSelectedItem().toString().equals("20대")) {
                    getAge = "20";
                }
                else if(cAge.getSelectedItem().toString().equals("30대")) {
                    getAge = "30";
                }
                else if(cAge.getSelectedItem().toString().equals("40대")) {
                    getAge = "40";
                }
                else if(cAge.getSelectedItem().toString().equals("50대")) {
                    getAge = "50";
                }

                String ip = getResources().getString(R.string.ip);
                String Url = "http://"+ip+":8081/zipcock/android/cMemberJoin.do";
                Url += "?member_id="+cId.getText().toString();
                Url += "&member_pass="+cPass.getText().toString();
                Url += "&member_email="+cEmail.getText().toString();
                Url += "&member_age="+getAge;
                Url += "&member_sex="+getSex;
                Url += "&member_phone="+cPhone.getText().toString();
                Url += "&member_name="+cName.getText().toString();
                Log.d(TAG, Url);

                new AsyncHttpRequest2().execute(Url);
            }
        });

    } //// onCreate 끝


    //////////////////////////////////////////////////////


    /********* 아이디 중복확인 *******/
    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
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
                connection.setRequestMethod("POST");
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(TAG, s);
            dialog.dismiss();
            String result = jsonParser(s);

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("isCheck"));
            if (success==1){
                JSONObject idChe = jsonObject.getJSONObject("idList");
                String idCheck = idChe.toString();
                String id = idChe.getString("member_id").toString();

                Toast.makeText(getApplicationContext(), id+"는 이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                sb.append("사용가능한 아이디 입니다.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }


    /******* 사용자 회원가입 **************/
    class AsyncHttpRequest2 extends AsyncTask<String, Void, String> {
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
                connection.setRequestMethod("POST");
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

        //서버 통신 후 보여지는 거
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i(TAG, s);
            dialog.dismiss();
            String result = jsonParser2(s);

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }
    public String jsonParser2(String data){
        StringBuffer sb = new StringBuffer();
        try {
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("result"));
            if (success==1){

                Toast.makeText(getApplicationContext(), "가입완료! 환영합니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                sb.append("회원가입 불가!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    



}