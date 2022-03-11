package com.kosmo.zipcock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.navi.Constants;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.Location;
import com.kakao.sdk.navi.model.NaviOption;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "zipcock";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");

    Button complete;

    EditText et;
    ListView listView;

    ArrayList<MessageItem> messageItems=new ArrayList<>();
    ChatAdapter adapter;

    //Firebase Database 관리 객체참조변수
    FirebaseDatabase firebaseDatabase;
    //'chat'노드의 참조객체 참조변수]
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //제목줄 제목을 닉네임으로(또는 채팅방)
        //getSupportActionBar().setTitle(G.nickName);

        //채팅방 구분..
//        chatRef = firebaseDatabase.getReference("chat");

        final Intent intent = getIntent();
        SharedPreferences pref = getSharedPreferences("user_id", MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);
        G.nickName = member_id;

        ImageView iv_category = findViewById(R.id.iv_category);
        String category = intent.getStringExtra("mission_category");
        String chatroom = intent.getStringExtra("mission_num");
        String name = intent.getStringExtra("mission_name");
        String time = intent.getStringExtra("mission_time");
        String cost = intent.getStringExtra("mission_cost");
        String content = intent.getStringExtra("mission_content");
        String status = intent.getStringExtra("mission_status");

        String hid = intent.getStringExtra("mission_Hid");
        String mid = intent.getStringExtra("mission_id");

        if (hid.contains(member_id)){
            ((TextView)findViewById(R.id.tv_room_name)).setText(String.format(mid));
            ((TextView)findViewById(R.id.other_userId)).setText(String.format(mid));
        }
        else {
            ((TextView)findViewById(R.id.tv_room_name)).setText(String.format(hid));
            ((TextView)findViewById(R.id.other_userId)).setText(String.format(hid));
            if(status.equals("4")) {
                ((Button) findViewById(R.id.btn_missionComplete)).setVisibility(View.VISIBLE);
            }
        }

        try{
            ((TextView)findViewById(R.id.mission_name)).setText(String.format(name));

            String p = category;
            if (p.startsWith("벌레,쥐잡기")){
                iv_category.setImageDrawable(getDrawable(R.drawable.bug));
            } else if(p.startsWith("청소")){
                iv_category.setImageDrawable(getDrawable(R.drawable.clean));
            } else if(p.startsWith("배달")){
                iv_category.setImageDrawable(getDrawable(R.drawable.delivery));
            } else if(p.startsWith("설치")){
                iv_category.setImageDrawable(getDrawable(R.drawable.installation));
            } else if(p.startsWith("돌봄")){
                iv_category.setImageDrawable(getDrawable(R.drawable.together));
            } else if(p.startsWith("역활")){
                iv_category.setImageDrawable(getDrawable(R.drawable.role));
            } else if(p.startsWith("과외")){
                iv_category.setImageDrawable(getDrawable(R.drawable.lesson));
            } else {
                iv_category.setImageDrawable(getDrawable(R.drawable.etc));
            }

            switch (time){
                case "1" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10분 이내,");
                    break;
                case "2" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10 ~ 20분,");
                    break;
                case "3" :
                    ((TextView)findViewById(R.id.mission_time)).setText("20 ~ 40분,");
                    break;
                case "4" :
                    ((TextView)findViewById(R.id.mission_time)).setText("40 ~ 60분,");
                    break;
                case "5" :
                    ((TextView)findViewById(R.id.mission_time)).setText("60분 이상,");
                    break;
                default:
                    Log.i(TAG, "잘못적음");
                    break;
            }

            ((TextView)findViewById(R.id.mission_cost)).setText(String.format(cost)+"원");
            ((TextView)findViewById(R.id.mission_content)).setText(String.format(content));
        }
        catch (NullPointerException exception){
            Log.i(TAG, "null 주의");
        }


        et = findViewById(R.id.et);
        listView = findViewById(R.id.listview);
        adapter = new ChatAdapter(messageItems, getLayoutInflater());
        listView.setAdapter(adapter);
        complete = (Button) findViewById(R.id.btn_missionComplete);
        String ip = getResources().getString(R.string.ip);

        //Firebase DB관리 객체와 'chat'노드 참조객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
//        chatRef = firebaseDatabase.getReference("chat");
        chatRef = firebaseDatabase.getReference("chat").child(chatroom);

        Button button = findViewById(R.id.clickSend);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("완료 확인")
                        .setMessage("심부름을 완료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String requestUrl =
                                        "http://" + ip + ":8081/zipcock/android/complete.do";
                                requestUrl += "?mission_num=" + chatroom;
                                Log.d(TAG, requestUrl);

                                new AsyncHttpRequest().execute(requestUrl);
                                Toast.makeText(getApplicationContext(),
                                        "심부름을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ZipMain.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ChatActivity.this,
                                        "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSend();
            }
        });


        //firebaseDB에서 채팅 메세지들 실시간 읽어오기..
        //'chat'노드에 저장되어 있는 데이터들을 읽어오기
        //chatRef에 데이터가 변경되는 것으 듣는 리스너 추가
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    clickSend();
                    return true;
                }
                return false;
            }
        });

        chatRef.addChildEventListener(new ChildEventListener() {
            //새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                MessageItem messageItem= dataSnapshot.getValue(MessageItem.class);

                //새로운 메세지를 리스트뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem);

                //리스트뷰를 갱신
                adapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size()-1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void clickSend() {

        //firebase DB에 저장할 값들( 닉네임, 메세지, 프로필 이미지URL, 시간)
        String nickName = G.nickName;
        String message= et.getText().toString();
      //  String profileUrl= G.profileUrl;

        //메세지 작성 시간 문자열로..
        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE); //14:16
        String.format(time, simpleDateFormat);



        //firebase DB에 저장할 값(MessageItem객체) 설정
        MessageItem messageItem= new MessageItem(nickName,message,time);

        //'chat'노드에 MessageItem객체를 통해
        chatRef.push().setValue(messageItem);
        Log.i(TAG, "chatRef: "+chatRef);

        //EditText에 있는 글씨 지우기
        et.setText("");

        //소프트키패드를 안보이도록..
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);


    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
/*            if (!dialog.isShowing())
                dialog.show();*/
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
            try {
                JSONObject jsonObject = new JSONObject(sBuffer.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return sBuffer.toString();
        }

        //doInBackground메소드가 정상적으로 종료되었을때 호출되는 메소드
        @Override
        protected void onPostExecute(String s) {
        }

    }
}