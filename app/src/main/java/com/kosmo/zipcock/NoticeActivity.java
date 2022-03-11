package com.kosmo.zipcock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    private static final String TAG = "zipcock";

    //위젯과 리스트데이터 정리
    ListView listView;
    ProgressDialog dialog;//대화상자

    ArrayList<String> TitleL = new ArrayList<String>();
    ArrayList<String> IdL = new ArrayList<String>();
    ArrayList<String> IdxL = new ArrayList<String>();
    ArrayList<String> ContentL = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ip = getResources().getString(R.string.ip);
        setContentView(R.layout.activity_notice);

        //ProgressDialog객체생성
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setTitle("서버 데이터 수신");
        dialog.setMessage("서버로부터 데이터 수신중입니다.");
        //서버로부터 가져온 JSON을 데이터를 기반으로 출력하기
        new AsyncHttpRequest().execute(
                "http://"+ip+":8081/zipcock/android/mboardList.do"
        );

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TitleL);


        ListView listView = findViewById(R.id.Noticelist);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), NoticeViewActivity.class);
                intent.putExtra("mboard_num", IdxL.get(position));
                intent.putExtra("mboard_title", TitleL.get(position));
                intent.putExtra("mboard_id", IdL.get(position));
                intent.putExtra("mboard_content", ContentL.get(position));

                startActivity(intent);
            }
        });
    }


    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;

        public CustomList(Activity context){
            super(context, R.layout.notice_list, IdxL);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.notice_list,null,true);

            //위젯가져오기
            TextView Title = (TextView)rowView.findViewById(R.id.Title);
            TextView Id = (TextView)rowView.findViewById(R.id.Id);
            TextView Idx = (TextView)rowView.findViewById(R.id.Idx);
            TextView Content = (TextView)rowView.findViewById(R.id.content);

            //imageView.setImageResource(images[position]);
            Title.setText(TitleL.get(position));
            Id.setText(IdL.get(position));
            Idx.setText(IdxL.get(position));
            Content.setText(ContentL.get(position));

            return rowView;
        }
    }//CustomList



    class AsyncHttpRequest extends AsyncTask<String,Void,String>
    {

        //실행전 호출되는 메소드
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //대화창이 없다면 띄워준다
            if(!dialog.isShowing()){
                dialog.show();
            }
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

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),"UTF-8")
                    );
                    String data;
                    while((data=reader.readLine())!=null){
                        jsonString.append(data+"\r\n");
                    }
                    reader.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //JSON Array 파싱
            try {
                //서버에서 가져온 데이터 출력
                Log.i("com.zipcock.json", jsonString.toString());



                JSONArray jsonArray = new JSONArray(jsonString.toString());
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    /*//getInt(인덱스) : JSON배열에서 정수를 가져올때 사용
                    int tempNum = jsonArray.getInt(i);
                    Log.i("KOSMO37>JSON1", "파싱데이터:" + tempNum);*/

                    JSONObject NoticeObj = jsonArray.getJSONObject(i);

                    TitleL.add(NoticeObj.getString("mboard_title"));
                    IdL.add(NoticeObj.getString("mboard_id"));
                    IdxL.add(NoticeObj.getString("mboard_num"));
                    ContentL.add(NoticeObj.getString("mboard_content"));

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return jsonString.toString();
        }


        //doInBackground 메소드 정상종료시 호출되는 메소드
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();//대화창닫기


            //커스텀뷰
            CustomList customList = new CustomList(NoticeActivity.this);
            listView = (ListView)findViewById(R.id.Noticelist);
            listView.setAdapter(customList);

        }

    }////AsyncHttpRequest class
}