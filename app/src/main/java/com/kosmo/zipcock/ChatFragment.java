package com.kosmo.zipcock;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static final String TAG = "zipcock";

    FirebaseDatabase database;
    DatabaseReference reference;

    ListView listView;


    ArrayList<String> hid = new ArrayList<String>();
    ArrayList<String> mid = new ArrayList<String>();

//    ArrayList<String> message = new ArrayList<String>();

    ArrayList<String> category = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> num = new ArrayList<String>();

    ArrayList<String> waypoint = new ArrayList<String>();
    ArrayList<String> end = new ArrayList<String>();
    ArrayList<String> mission = new ArrayList<String>();
    ArrayList<String> reservation = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> cost = new ArrayList<String>();
    ArrayList<String> content = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String ip = getResources().getString(R.string.ip);
        SharedPreferences pref = getActivity().getSharedPreferences("user_id", MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);

        String requestUrl =
                "http://"+ip+":8081/zipcock/android/chatList.do";
        requestUrl += "?mission_id=" + member_id.toString() +"&mission_Hid="+ member_id.toString();
        new AsyncHttpRequest().execute(requestUrl);

        Log.i(TAG, requestUrl);
        ViewGroup viewGroup =
                (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);


        return viewGroup;
    }

    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;

        public CustomList(Activity context){
            super(context, R.layout.chat_item, status);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.chat_item,null,true);

            //위젯가져오기
            //ImageView Category = (ImageView)rowView.findViewById(R.id.mission_category);
            TextView Title = (TextView)rowView.findViewById(R.id.mission_name);
            TextView RoomId = (TextView)rowView.findViewById(R.id.chat_roomId);
//            TextView Message = (TextView)rowView.findViewById(R.id.chat_message);

            //영화목록 뷰에 내용 삽입하기
            //Category.setImageResource(Integer.parseInt(category.get(position)));
            Title.setText(name.get(position));
            Log.i(TAG, "hid: "+hid);
            SharedPreferences pref = getActivity().getSharedPreferences("user_id", MODE_PRIVATE);
            String member_id = pref.getString("member_id",null);
            Log.i(TAG, "member_id: "+member_id);

            if (hid.contains(member_id)){
                RoomId.setText(mid.get(position));
            }
            else {
                RoomId.setText(hid.get(position));
            }

            return rowView;
        }
    }//CustomList

    class AsyncHttpRequest extends AsyncTask<String,Void,String> {

        //실행전 호출되는 메소드
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //실행
        @Override
        protected String doInBackground(String... strings) {

            StringBuffer jsonString = new StringBuffer();

            try{
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                /*out.write(strings[1].getBytes());
                out.write("&".getBytes());
                out.write(strings[2].getBytes());*/
                out.flush();
                out.close();

                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "내 심부름 리스트 불러오기 성공");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),"UTF-8")
                    );
                    String data;
                    while((data=reader.readLine())!=null){
                        jsonString.append(data+"\r\n");
                    }
                    reader.close();
                }
                else {
                    Log.i(TAG, "내 심부름 리스트 불러오기 실패");
                }

                Log.i(TAG, jsonString.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //JSON Array 파싱
            try {
                //서버에서 가져온 데이터 출력
                Log.i("com.zipcock.json", jsonString.toString());

                JSONArray jsonArray = new JSONArray(jsonString.toString());

                name.clear();
                mid.clear();
                hid.clear();

                category.clear();
                num.clear();
                status.clear();
                waypoint.clear();
                end.clear();
                mission.clear();
                reservation.clear();
                time.clear();
                cost.clear();
                content.clear();

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject missionObj = jsonArray.getJSONObject(i);

                    name.add(missionObj.getString("mission_name"));
                    mid.add(missionObj.getString("mission_id"));
                    hid.add(missionObj.getString("mission_Hid"));

                    category.add(missionObj.getString("mission_category"));
                    num.add(missionObj.getString("mission_num"));
                    status.add(missionObj.getString("mission_status"));
                    waypoint.add(missionObj.getString("mission_waypoint"));
                    end.add(missionObj.getString("mission_end"));
                    mission.add(missionObj.getString("mission_mission"));
                    reservation.add(missionObj.getString("mission_reservation"));
                    time.add(missionObj.getString("mission_time"));
                    cost.add(missionObj.getString("mission_cost"));
                    content.add(missionObj.getString("mission_content"));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return jsonString.toString();
        }


        //doInBackground 메소드 정상종료시 호출되는 메소드
        @Nullable
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //커스텀뷰
            ChatFragment.CustomList customList = new ChatFragment.CustomList(getActivity());
            listView = getView().findViewById(R.id.chat_listview);
            listView.setAdapter(customList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //선택한 항목의 position값 출력
//                    Toast.makeText(getActivity().getBaseContext(),
//                            "[position="+position+"=>]"+"아이디:"+name.get(position),
//                            Toast.LENGTH_SHORT).show();
       /*             String requestUrl =
                            "http://192.168.219.102:8081/zipcock/android/missionView.do";
                    requestUrl += "?mission_num=" + num.get(position);
                    //Log.d(TAG, requestUrl);
                    //new AsyncHttpRequest().execute(requestUrl);*/

                    Intent intent=new Intent(getActivity().getApplicationContext(), ChatActivity.class);

                    intent.putExtra("mission_num", num.get(position));
                    intent.putExtra("mission_category", category.get(position));
                    intent.putExtra("mission_name", name.get(position));
                    intent.putExtra("mission_id", mid.get(position));
                    intent.putExtra("mission_Hid", hid.get(position));
                    intent.putExtra("mission_waypoint", waypoint.get(position));
                    intent.putExtra("mission_end", end.get(position));
                    intent.putExtra("mission_mission", mission.get(position));
                    intent.putExtra("mission_reservation", reservation.get(position));
                    intent.putExtra("mission_time", time.get(position));
                    intent.putExtra("mission_cost", cost.get(position));
                    intent.putExtra("mission_content", content.get(position));
                    intent.putExtra("mission_status", status.get(position));
//                    Bundle argu = new Bundle();
//                    argu.putString("ChatItem", mission_Hid.toString() );

                    // 이동할 Fragment 선언
//                    ChatActivity chatActivity = new ChatActivity();
//                    chatMsgFragment = new ChatMsgFragment();

//                    chatMsgFragment.setArguments(argu);

//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left,
//                                    R.anim.slide_in_left, R.anim.slide_in_right)
//                            .replace(R.id.zipcockmain, chatMsgFragment, TAG)
//                            .addToBackStack(null)
//                            .commit();
//                            /******repalce 뒤에 뭘 넣어야하지******/

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    SharedPreferences pref = getActivity().getSharedPreferences("user_id", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("nickName",G.nickName);
                    editor.commit();

                    firebaseDatabase.getInstance();
                    reference = firebaseDatabase.getReference("message");

                    startActivity(intent);

                }
            });
        }
    }////AsyncHttpRequest class

}