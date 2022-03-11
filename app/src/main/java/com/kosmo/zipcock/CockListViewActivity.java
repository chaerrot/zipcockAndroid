package com.kosmo.zipcock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;

public class CockListViewActivity extends AppCompatActivity {

    public static final String TAG = "zipcock";
    SupportMapFragment mapFragment;
    GoogleMap map;
    ProgressDialog dialog;

    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cock_list_view);

        String ip = getResources().getString(R.string.ip);

        SharedPreferences pref = getSharedPreferences("user_id",MODE_PRIVATE);
        String member_id = pref.getString("member_id",null);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("처리중");
        dialog.setMessage("심부름 매칭중입니다.");

        Button reg_button = (Button) findViewById(R.id.reg_button);

        final Intent intent = getIntent();
        final String num = intent.getStringExtra("mission_num");
        final String category = intent.getStringExtra("mission_category");
        final String name = intent.getStringExtra("mission_name");
        final String status = intent.getStringExtra("mission_status");
        Log.i(TAG, name);

        /*경유지*/
        final String wpoint = intent.getStringExtra("mission_waypoint");

        int way = wpoint.lastIndexOf("|");
        String way_rest = wpoint.substring(1, way);
        String way_2 = wpoint.substring(way+1);
        int way_num = way_rest.lastIndexOf("|");
        String way_1 = way_rest.substring(way_num+1);

        /*경유지 없으면 경유지란 안보이게 하기*/
        final String waypoint = way_1 +" "+ way_2;
        if(waypoint.equals(" ")){
            (findViewById(R.id.waypointFrame)).setVisibility(View.GONE);
        }

        int w = wpoint.indexOf("|");
        String w1 = wpoint.substring(0, w); //위도
        String w_rest = wpoint.substring(w+1);
        int w_num = w_rest.indexOf("|");
        String w2 = w_rest.substring(0, w_num); //경도

        final String wayLatLng1 = w1;
        final String wayLatLng2 = w2;

        /*도착지*/
        final String epoint = intent.getStringExtra("mission_end");
        Log.i(TAG, "epoint="+epoint);
        int end = epoint.lastIndexOf("|");
        String end_rest = epoint.substring(1, end);
        String end_2 = epoint.substring(end+1);
        int end_num = end_rest.lastIndexOf("|");
        String end_1 = end_rest.substring(end_num+1);

        final String endpoint = end_1 +"\n "+ end_2;

        int e = epoint.indexOf("|");
        String e1 = epoint.substring(0, e); //위도
        String e_rest = epoint.substring(e+1);
        int e_num = e_rest.indexOf("|");
        String e2 = e_rest.substring(0, e_num); //경도

        final String endLatLng1 = e1;
        final String endLatLng2 = e2;
        Log.i(TAG, "e1="+e1);
        Log.i(TAG, "e2="+e2);


        final String mission = intent.getStringExtra("mission_mission");
        final String reservation= intent.getStringExtra("mission_reservation");

        final String time = intent.getStringExtra("mission_time");
        final String cost = intent.getStringExtra("mission_cost");
        final String content = intent.getStringExtra("mission_content");

        try{
            ((TextView)findViewById(R.id.mission_num)).setText(String.format(num));
            ((TextView)findViewById(R.id.mission_category)).setText(String.format(category));
            ((TextView)findViewById(R.id.mission_name)).setText(String.format(name));
            ((TextView)findViewById(R.id.mission_waypoint)).setText(String.format(waypoint));
            ((TextView)findViewById(R.id.mission_end)).setText(String.format(endpoint));

            /*예약인지, 지금 즉시 인지*/
            if(mission.equals("1")){
                ((TextView)findViewById(R.id.mission_mission)).setText("지금 즉시");
                ((TextView)findViewById(R.id.mission_reservation)).setText(" ");
            }
            else{
                ((TextView)findViewById(R.id.mission_mission)).setText("예약일");
                ((TextView)findViewById(R.id.mission_reservation)).setText(String.format(reservation));
            }

            /*소요시간*/
            String t = time;
            switch (t){
                case "1" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10분 이내");
                    break;
                case "2" :
                    ((TextView)findViewById(R.id.mission_time)).setText("10 ~ 20분");
                    break;
                case "3" :
                    ((TextView)findViewById(R.id.mission_time)).setText("20 ~ 40분");
                    break;
                case "4" :
                    ((TextView)findViewById(R.id.mission_time)).setText("40 ~ 60분");
                    break;
                case "5" :
                    ((TextView)findViewById(R.id.mission_time)).setText("60분 이상");
                    break;
                default:
                    Log.i(TAG, "잘못적음");
                    break;
            }


            ((TextView)findViewById(R.id.mission_cost)).setText(String.format(cost)+"원");
            ((TextView)findViewById(R.id.mission_content)).setText(String.format(content));
        }
        catch (NullPointerException exception){
            Log.i(TAG, "null임");
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready...");
                map = googleMap;

                String name = intent.getStringExtra("mission_name");
                String category = intent.getStringExtra("mission_category");

                LatLng aLatLng = new LatLng(Double.parseDouble(e2),Double.parseDouble(e1));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(aLatLng);
                markerOptions.snippet(category);
                markerOptions.title(name);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
                map.addMarker(markerOptions);

                //카메라 이동
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(aLatLng, 16));
                //지도유형
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //마커클릭에 대한 리스너
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(), "부탁해요 셔틀콕! : "
                                        +marker.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        //true로 하면 InfoWindow가 안나온다.
                        return false;
                    }
                });
                /*
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(), "심부름 금액 : "
                                        +marker.getSnippet(),
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });

        try{
            MapsInitializer.initialize(this);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CockListViewActivity.this)
                    .setTitle("지원 확인")
                    .setMessage("지원하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String requestUrl =
                                    "http://" + ip + ":8081/zipcock/android/matching.do";
                            requestUrl += "?mission_Hid=" + member_id;
                            requestUrl += "&mission_num=" + num;
                            Log.d(TAG, requestUrl);

                            new AsyncHttpRequest().execute(requestUrl);
                            Toast.makeText(getApplicationContext(),
                                    "매칭완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HelperMain.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),
                                    "취소하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            }
        });
    }
    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing())
                dialog.show();
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

                Log.i(TAG, sBuffer.toString());

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
        }
    }
}