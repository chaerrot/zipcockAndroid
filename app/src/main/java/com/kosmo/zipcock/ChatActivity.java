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

    //Firebase Database ?????? ??????????????????
    FirebaseDatabase firebaseDatabase;
    //'chat'????????? ???????????? ????????????]
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //????????? ????????? ???????????????(?????? ?????????)
        //getSupportActionBar().setTitle(G.nickName);

        //????????? ??????..
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
            if (p.startsWith("??????,?????????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.bug));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.clean));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.delivery));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.installation));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.together));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.role));
            } else if(p.startsWith("??????")){
                iv_category.setImageDrawable(getDrawable(R.drawable.lesson));
            } else {
                iv_category.setImageDrawable(getDrawable(R.drawable.etc));
            }

            switch (time){
                case "1" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10??? ??????,");
                    break;
                case "2" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10 ~ 20???,");
                    break;
                case "3" :
                    ((TextView)findViewById(R.id.mission_time)).setText("20 ~ 40???,");
                    break;
                case "4" :
                    ((TextView)findViewById(R.id.mission_time)).setText("40 ~ 60???,");
                    break;
                case "5" :
                    ((TextView)findViewById(R.id.mission_time)).setText("60??? ??????,");
                    break;
                default:
                    Log.i(TAG, "????????????");
                    break;
            }

            ((TextView)findViewById(R.id.mission_cost)).setText(String.format(cost)+"???");
            ((TextView)findViewById(R.id.mission_content)).setText(String.format(content));
        }
        catch (NullPointerException exception){
            Log.i(TAG, "null ??????");
        }


        et = findViewById(R.id.et);
        listView = findViewById(R.id.listview);
        adapter = new ChatAdapter(messageItems, getLayoutInflater());
        listView.setAdapter(adapter);
        complete = (Button) findViewById(R.id.btn_missionComplete);
        String ip = getResources().getString(R.string.ip);

        //Firebase DB?????? ????????? 'chat'?????? ???????????? ????????????
        firebaseDatabase = FirebaseDatabase.getInstance();
//        chatRef = firebaseDatabase.getReference("chat");
        chatRef = firebaseDatabase.getReference("chat").child(chatroom);

        Button button = findViewById(R.id.clickSend);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("?????? ??????")
                        .setMessage("???????????? ?????????????????????????")
                        .setPositiveButton("???", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String requestUrl =
                                        "http://" + ip + ":8081/zipcock/android/complete.do";
                                requestUrl += "?mission_num=" + chatroom;
                                Log.d(TAG, requestUrl);

                                new AsyncHttpRequest().execute(requestUrl);
                                Toast.makeText(getApplicationContext(),
                                        "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ZipMain.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ChatActivity.this,
                                        "?????????????????????.", Toast.LENGTH_SHORT).show();
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


        //firebaseDB?????? ?????? ???????????? ????????? ????????????..
        //'chat'????????? ???????????? ?????? ??????????????? ????????????
        //chatRef??? ???????????? ???????????? ?????? ?????? ????????? ??????
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
            //?????? ????????? ?????? ??? ValueListener??? ????????? ?????? ???????????? ???????????? ?????? ?????? ???
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //?????? ????????? ?????????(??? : MessageItem??????) ????????????
                MessageItem messageItem= dataSnapshot.getValue(MessageItem.class);

                //????????? ???????????? ??????????????? ???????????? ?????? ArrayList??? ??????
                messageItems.add(messageItem);

                //??????????????? ??????
                adapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size()-1); //??????????????? ????????? ????????? ????????? ?????? ??????
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

        //firebase DB??? ????????? ??????( ?????????, ?????????, ????????? ?????????URL, ??????)
        String nickName = G.nickName;
        String message= et.getText().toString();
      //  String profileUrl= G.profileUrl;

        //????????? ?????? ?????? ????????????..
        Calendar calendar = Calendar.getInstance(); //?????? ????????? ????????? ?????? ??????
        String time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE); //14:16
        String.format(time, simpleDateFormat);



        //firebase DB??? ????????? ???(MessageItem??????) ??????
        MessageItem messageItem= new MessageItem(nickName,message,time);

        //'chat'????????? MessageItem????????? ??????
        chatRef.push().setValue(messageItem);
        Log.i(TAG, "chatRef: "+chatRef);

        //EditText??? ?????? ?????? ?????????
        et.setText("");

        //????????????????????? ???????????????..
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
                    Log.i(TAG, "HTTP OK ??????");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                } else {
                    Log.i(TAG, "HTTP OK ??????");
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

        //doInBackground???????????? ??????????????? ?????????????????? ???????????? ?????????
        @Override
        protected void onPostExecute(String s) {
        }

    }
}