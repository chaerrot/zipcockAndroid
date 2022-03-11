package com.kosmo.zipcock;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class HMypageFragment extends Fragment {
    private static final String TAG = "lecture";

    TextView idtext,nametext,pointview,simview;

    String name = new String();
    String point = new String();
    String mission = new String();
    String status = new String();
    String pass = new String();
    String email = new String();
    String age = new String();
    String phone = new String();
    String bank = new String();
    String account = new String();
    String introduce = new String();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String ip = getResources().getString(R.string.ip);

        SharedPreferences pref = getActivity().getSharedPreferences("user_id",MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);

        String requestUrl =
                "http://"+ip+":8081/zipcock/android/memberInfo.do";
        requestUrl += "?member_id=" + member_id.toString();
        Log.d(TAG, requestUrl);

        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_h_mypage, container, false);

        idtext = rootView.findViewById(R.id.member_id);
        nametext = rootView.findViewById(R.id.member_name);
        pointview = rootView.findViewById(R.id.pointView);
        simview = rootView.findViewById(R.id.simView);

        idtext.setText(member_id+"님 환영합니다.");

        Button memDel = (Button) rootView.findViewById(R.id.memberDel);
        Button notBu = (Button) rootView.findViewById(R.id.notice) ;
        Button faqBu = (Button) rootView.findViewById(R.id.faq);
        Button update = (Button) rootView.findViewById(R.id.hupdate);
        Button RevBut = (Button) rootView.findViewById(R.id.RevBut);

        new AsyncHttpRequest().execute(requestUrl);


        //정보수정
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), HInfoUpdate.class);
                intent.putExtra("member_id", member_id);
                intent.putExtra("member_name", name);
                intent.putExtra("member_pass", pass);
                intent.putExtra("member_email", email);
                intent.putExtra("member_age", age);
                intent.putExtra("member_phone", phone);
                intent.putExtra("member_bank", bank);
                intent.putExtra("member_account", account);
                intent.putExtra("member_introduce", introduce);
                startActivity(intent);
            }
        });

        //리뷰페이지
        RevBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), ReviewActivity.class);
                intent.putExtra("member_id", member_id);
                startActivity(intent);
            }
        });


        //회원탈퇴
        memDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("탈퇴 확인")
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String member_id = pref.getString("member_id",null);
                                String requestUrl =
                                        "http://"+ip+":8081/zipcock/android/ImemberDelete.do";
                                requestUrl += "?member_id="+ member_id;

                                Log.d(TAG, requestUrl);

                                new AsyncHttpRequest().execute(requestUrl);

                                Toast.makeText(getActivity().getApplicationContext(),
                                        "회원탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });



        faqBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity().getApplicationContext(), faqFragment.class);
                startActivity(intent1);
            }
        });

        notBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity().getApplicationContext(), NoticeActivity.class);
                startActivity(intent2);
            }
        });

        return rootView;

    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
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

            try {
                JSONObject jsonObject = new JSONObject(sBuffer.toString());
                int success = Integer.parseInt(jsonObject.getString("isLogin"));
                if (success==1) {
                    JSONObject mInfo = jsonObject.getJSONObject("memberInfo");
                    String memberInfo = mInfo.toString();
                    name = mInfo.getString("member_name").toString();
                    point = mInfo.getString("member_point").toString();
                    mission = mInfo.getString("member_missionC").toString();
                    status = mInfo.getString("member_status").toString();
                    pass = mInfo.getString("member_pass").toString();
                    email = mInfo.getString("member_email").toString();
                    age = mInfo.getString("member_age").toString();
                    phone = mInfo.getString("member_phone").toString();
                    bank = mInfo.getString("member_bank").toString();
                    account = mInfo.getString("member_account").toString();
                    introduce = mInfo.getString("member_introduce").toString();

                }
                else {
                    Log.i(TAG,"멤버정보 불러오기 실패");
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return sBuffer.toString();
        }

        //doInBackground메소드가 정상적으로 종료되었을때 호출되는 메소드
        @Override
        protected void onPostExecute(String s) {
            Log.i(TAG, s);
            nametext.setText(name);
            pointview.setText(point+"P");
            simview.setText(mission+"건");

        }
    }

}
