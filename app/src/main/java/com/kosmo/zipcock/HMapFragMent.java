package com.kosmo.zipcock;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.navi.Constants;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.Location;
import com.kakao.sdk.navi.model.NaviOption;

import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HMapFragMent extends Fragment {
    private static final String TAG = "zipcock";

    //위젯과 리스트데이터 정리
    ListView listView;
    SearchView searchView;

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
        String mission_id = pref.getString("member_id", null);

        Log.i("아디", mission_id);

        String requestUrl =
                "http://"+ip+":8081/zipcock/android/HsimList.do";
        requestUrl += "?mission_Hid=" + mission_id.toString();

        new AsyncHttpRequest().execute(requestUrl);

        ViewGroup viewGroup =
                (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        return viewGroup;
    }


    private void searchList() {


    }

    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;

        public CustomList(Activity context){
            super(context, R.layout.cock_list, status);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.cock_list,null,true);

            //위젯가져오기
            TextView Category = (TextView)rowView.findViewById(R.id.mission_category);
            TextView Num = (TextView)rowView.findViewById(R.id.mission_num);
            TextView Status = (TextView)rowView.findViewById(R.id.mission_status);
            TextView Name = (TextView)rowView.findViewById(R.id.mission_name);

            //뷰에 내용 삽입하기
            Category.setText(category.get(position));
            Num.setText(num.get(position));
            Status.setText(status.get(position));
            Name.setText(name.get(position));

            String s = status.get(position);


            if(s.equals("1")){
                Status.setText("매칭중");
                Status.setTextColor(Color.RED);
            }else if(s.equals("2")){
                Status.setText("매칭완료");
            }else if(s.equals("3")) {
                Status.setText("종료됨");
            }else if(s.equals("4")) {
                Status.setText("배달완료됨");
            }



            String p = category.get(position);
            if (p.startsWith("벌레,쥐잡기")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null,  getResources().getDrawable(R.drawable.bug),null,null);
            } else if(p.startsWith("청소")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.clean),null,null);
            } else if(p.startsWith("배달")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.delivery),null,null);
            } else if(p.startsWith("설치")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.installation),null,null);
            } else if(p.startsWith("돌봄")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.together),null,null);
            } else if(p.startsWith("역할")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.role),null,null);
            } else if(p.startsWith("과외")){
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.lesson),null,null);
            } else {
                Category.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.etc),null,null);
            }


            return rowView;
        }

        @Override
        public boolean isEnabled(int position){ // 선택불가
            return true;
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
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                /*out.write(strings[1].getBytes());
                out.write("&".getBytes());
                out.write(strings[2].getBytes());*/
                out.flush();
                out.close();

                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "심부름 리스트 불러오기 성공");
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
                    Log.i(TAG, "심부름 리스트 불러오기 안됨");
                }

                Log.i("심부름 리스트", jsonString.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //JSON Array 파싱
            try {
                //서버에서 가져온 데이터 출력
                Log.i("com.zipcock.json", jsonString.toString());

                JSONArray jsonArray = new JSONArray(jsonString.toString());

                category.clear();
                num.clear();
                status.clear();
                name.clear();
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

                    category.add(missionObj.getString("mission_category"));
                    num.add(missionObj.getString("mission_num"));
                    status.add(missionObj.getString("mission_status"));
                    name.add(missionObj.getString("mission_name"));
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
            HMapFragMent.CustomList customList = new HMapFragMent.CustomList(getActivity());
            listView = getView().findViewById(R.id.cocklist);
            listView.setAdapter(customList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent=new Intent(getActivity().getApplicationContext(), HMapviewMain.class);

                    intent.putExtra("mission_num", num.get(position));
                    intent.putExtra("mission_category", category.get(position));
                    intent.putExtra("mission_name", name.get(position));
                    intent.putExtra("mission_waypoint", waypoint.get(position));
                    intent.putExtra("mission_end", end.get(position));
                    intent.putExtra("mission_mission", mission.get(position));
                    intent.putExtra("mission_reservation", reservation.get(position));
                    intent.putExtra("mission_time", time.get(position));
                    intent.putExtra("mission_cost", cost.get(position));
                    intent.putExtra("mission_content", content.get(position));
                    intent.putExtra("mission_status", status.get(position));

                    startActivity(intent);

                }
            });
        }
    }////AsyncHttpRequest class
}
