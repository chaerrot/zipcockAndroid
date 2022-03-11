package com.kosmo.zipcock;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HMainFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "zipcock";

    //위젯과 리스트데이터 정리
    ListView listView;
    SearchView searchView;
    MapView mapView = null;

    //위치
    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions myLocationMarker;

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

        new AsyncHttpRequest().execute(
                "http://"+ip+":8081/zipcock/android/missionList.do"
                //"http://192.168.219.121:8081/zipcock/android/missionList.do"
        );

        ViewGroup viewGroup =
                (ViewGroup) inflater.inflate(R.layout.fragment_h_main, container, false);

        searchView = viewGroup.findViewById(R.id.searchForm);

        mapView = (MapView)viewGroup.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // 검색 버튼이 눌러졌을 때 이벤트 처리
                //Toast.makeText(getActivity().getApplicationContext(), "검색 처리됨 : " + query, Toast.LENGTH_SHORT).show();

                String requestUrl =
                        "http://"+ip+":8081/zipcock/android/missionListSearch.do";
                requestUrl += "?searchTxt="+ query;

                new AsyncHttpRequest().execute(requestUrl);

                Log.i(TAG, requestUrl);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }

        });


        Button button = viewGroup.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMyLocation();
            }
        });

        //권한 체크 후 사용자에 의해 취소되었다면 다시 요청
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        try{
            MapsInitializer.initialize(getActivity());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return viewGroup;
    }


    //내 위치 요청
    private String requestMyLocation(){
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        try{
            long minTime = 10000;
            float minDistance = 0;
            //GPS정보제공자를 통해 내 위치를 얻어온다.
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, minTime, minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {

                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {

                        }
                    }
            );

            //GPS를 통해 확인된 마지막 내 위치값을 가져온다(캐쉬값)
            //GPS를 바로 내가 가져오지 못하는 경우 캐쉬로 된 마지막 정보값을 가져온다는 의미
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null){
                showCurrentLocation(lastLocation);
            }
            //GPS -> 캐쉬 -> WIFI등을 통해서 어떻게든 내 위치를 찾아내겠다는 강력한 의지!!!
            //네트워크를 통한 내 위치 확인. Wi-Fi 혹은 무선 인터넷을 통해 확인
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, minTime, minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            showCurrentLocation(location);
                        }
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }
                        @Override
                        public void onProviderEnabled(@NonNull String provider) {
                        }
                        @Override
                        public void onProviderDisabled(@NonNull String provider) {

                        }
                    }
            );
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        return null;
    }

    private void showCurrentLocation(Location location){
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        //애니메이션 효과가 있는 카메라
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17)); //숫자는 확대정도
        //에니메이션 효과 없는 카메라
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 16)); //숫자는 확대정도

        showMyLocationMarker(location);
    }

    //내 위치에 아이콘(마커 혹은 플레그) 표시
    private void showMyLocationMarker(Location location){
        if(myLocationMarker == null){
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude())); //서버쪽으로 아이디랑 같이 올리면 시간대별로 배달기사의 이동경로를 볼수있다.
            myLocationMarker.title("현재위치\n");
            myLocationMarker.snippet("GPS로 확인한 위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            map.addMarker(myLocationMarker);
        }
        else{
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    //수명주기 함수를 통해 추가적인 본인위치 확인(반드시 필요한 것은 아님)
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()실행");

        if(map != null){
            Log.i(TAG, "권한 체크 후 onPause()실행");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()실행");

        if(map != null){
            Log.i(TAG, "권한 체크 후 onResume()실행");
            requestMyLocation();
        }
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
            }else if(s.equals("3")){
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
            if(status.get(position).equals("2") || status.get(position).equals("3")){
                return false;
            }
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
            CustomList customList = new CustomList(getActivity());
            listView = getView().findViewById(R.id.cocklist);
            listView.setAdapter(customList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent=new Intent(getActivity().getApplicationContext(), CockListViewActivity.class);

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
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Log.d(TAG, "GoogleMap is ready...");
        map = googleMap;
        //내위치요청
        requestMyLocation();

        //LatLng GASAN = new LatLng(37.478836235029746, 126.87868018447071);

        for(int idx=0; idx<category.size(); idx++){

            final String epoint = end.get(idx);
            Log.i(TAG, "epoint="+epoint);
            int end = epoint.lastIndexOf("|");
            String end_rest = epoint.substring(1, end);
            String end_2 = epoint.substring(end+1);
            int end_num = end_rest.lastIndexOf("|");
            String end_1 = end_rest.substring(end_num+1);

            int e = epoint.indexOf("|");
            String e1 = epoint.substring(0, e); //위도
            String e_rest = epoint.substring(e+1);
            int e_num = e_rest.indexOf("|");
            String e2 = e_rest.substring(0, e_num); //경도

            final String endLatLng1 = e1;
            final String endLatLng2 = e2;

            LatLng aLatLng = new LatLng(Double.parseDouble(e2),Double.parseDouble(e1));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(aLatLng);
            markerOptions.title(category.get(idx));
            markerOptions.snippet(name.get(idx));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            map.addMarker(markerOptions);

            //카메라 이동
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(aLatLng, 16));
            //지도유형
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}