package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MapviewMain extends AppCompatActivity {
    Timer timer = new Timer();
    TimerTask timerTask;

    int timercheck = 0;

    MapView mapView;
    ViewGroup mapViewContainer;
    MapPoint waymark;
    MapPoint endmark;
    MapPoint startmark;

    ProgressDialog dialog;
    int success;
    JSONObject dInfo;

    String startcheck;
    Double startpoint_x;
    Double startpoint_y;
    String[] start;

    String waycheck;
    Double waypoint_x;
    Double waypoint_y;
    String waypoint;
    String[] way;

    Double endpoint_x;
    Double endpoint_y;
    String endpoint;
    String[] end;

    TextView missionContent;

    String num;

    private static final String TAG = "lecture";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview_main);

        missionContent = (TextView) findViewById(R.id.missioncontent);

        final Intent intent = getIntent();
        num = intent.getStringExtra("mission_num");

        String ip = getResources().getString(R.string.ip);
        SharedPreferences pref = getSharedPreferences("user_id", Context.MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);

        String requestUrl =
                "http://"+ip+":8081/zipcock/android/helperLocation.do";
        requestUrl += "?mission_num=" + num;
        Log.d(TAG, requestUrl);

        Log.i("dInfo값", String.valueOf(dInfo));


        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("헬퍼위치 불러오는중.");
        dialog.setMessage("헬퍼위치를 불러오고있습니다.");


        //지도부분
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
/*        mapView.setMapViewEventListener((MapViewEventListener) this);*/

        if(timercheck == 0) {
            timerTask = newTimeTask();
            timer.schedule(timerTask, 100, 3000);
        }
        new AsyncHttpRequest().execute(requestUrl);
    }

    @Override
    public void onDestroy() {
        timercheck=0;
        timerTask.cancel();
        super.onDestroy();
    }

    public TimerTask newTimeTask(){
        String ip = getResources().getString(R.string.ip);

        SharedPreferences pref = getSharedPreferences("user_id", MODE_PRIVATE);
        String member_id = pref.getString("member_id", null);

        TimerTask LocalTimerTask = new TimerTask() {
            @Override
            public void run() {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String requestUrl =
                                "http://" + ip + ":8081/zipcock/android/helperLocation.do";
                        requestUrl += "?mission_num=" + num.toString();
                        Log.d(TAG, requestUrl);
                        new AsyncHttpRequest().execute(requestUrl);
                        Log.i("타이머", "1");
                    }
                }, 0);
            }
        };
        return LocalTimerTask;
    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
        }

        TimerTask TT=new TimerTask() {
            @Override
            public void run() {
                Log.i("타이머","1");
            }
        };

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
                success = Integer.parseInt(jsonObject.getString("isLogin"));
                if (success==1) {
                    dInfo = jsonObject.getJSONObject("missionList");
                    end = dInfo.getString("mission_end").toString().split("\\|");
                    endpoint_x = Double.parseDouble(end[1]);
                    endpoint_y = Double.parseDouble(end[0]);
                    endmark = MapPoint.mapPointWithGeoCoord(endpoint_x, endpoint_y);

                    startcheck = dInfo.getString("mission_start").toString();
                    waycheck = dInfo.getString("mission_waypoint").toString();
                    if(!(startcheck.equals("null") || startcheck.equals("||"))) {
                        start = startcheck.split("\\|");
                        startpoint_x = Double.parseDouble(start[1]);
                        startpoint_y = Double.parseDouble(start[0]);
                        startmark = MapPoint.mapPointWithGeoCoord(startpoint_x, startpoint_y);
                    }
                    if(!(waycheck.equals("null") || waycheck.equals("||"))) {
                        way = waycheck.split("\\|");
                        waypoint_x = Double.parseDouble(way[1]);
                        waypoint_y = Double.parseDouble(way[0]);
                        waymark = MapPoint.mapPointWithGeoCoord(waypoint_x, waypoint_y);
                    }

                }
                else {
                    Log.i(TAG,"심부름정보 불러오기 실패");
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
            dialog.dismiss();

            if(dInfo!=null) {
                MapPOIItem marker = new MapPOIItem();
                mapView.setMapCenterPoint(endmark, true);
                MapPoint mapPoint = endmark;
                marker.setItemName("도착지");
                marker.setTag(0);
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                marker.setCustomImageResourceId(R.drawable.endmark);


                mapView.addPOIItem(marker);

                if(!(startcheck.equals("null") || startcheck.equals("||"))) {
                    MapPOIItem startmarker = new MapPOIItem();
                    mapView.setMapCenterPoint(startmark, true);
                    mapPoint = startmark;
                    startmarker.setItemName("헬퍼");
                    startmarker.setTag(0);
                    startmarker.setMapPoint(mapPoint);
                    startmarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    startmarker.setCustomImageResourceId(R.drawable.helpermark);

                    mapView.addPOIItem(startmarker);
                }
                if(!(waycheck.equals("null") || waycheck.equals("||"))) {
                    MapPOIItem waymarker = new MapPOIItem();
                    mapView.setMapCenterPoint(waymark, true);
                    mapPoint = waymark;
                    waymarker.setItemName("경유지");
                    waymarker.setTag(0);
                    waymarker.setMapPoint(mapPoint);
                    waymarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    waymarker.setCustomImageResourceId(R.drawable.waymark);
                    mapView.addPOIItem(waymarker);
                }

                mapView.fitMapViewAreaToShowAllPOIItems();

                missionContent.setVisibility(View.GONE);
            }
            else if(dInfo==null){
                mapView.setVisibility(View.GONE);
                missionContent.setText("진행중인 심부름이 없습니다!");
                if (timerTask != null) {
                    timerTask.cancel();
                }
            }

        }
    }
}