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
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.navi.Constants;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.Location;
import com.kakao.sdk.navi.model.NaviOption;

import net.daum.mf.map.api.MapView;

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


public class HMapviewMain extends AppCompatActivity {
    Timer timer = new Timer();
    TimerTask timerTask;

    ProgressDialog dialog;

    int timercheck = 0;
    int success;
    JSONObject dInfo;

    int mission_num;

    String helplocation_x;
    String helplocation_y;

    String waycheck;
    String waypoint_x;
    String waypoint_y;
    String waypoint;
    String[] way;

    String endpoint_x;
    String endpoint_y;
    String endpoint;
    String[] end;

    String missionName;
    String missionContent;

    TextView missionname, missioncontent;
    Button delstart, complete;

    String num;

    private static final String TAG = "lecture";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hmapview_main);

        String ip = getResources().getString(R.string.ip);

        SharedPreferences pref = getSharedPreferences("user_id", MODE_PRIVATE);
        String member_id = pref.getString("member_id", null);

        final Intent intent = getIntent();
        num = intent.getStringExtra("mission_num");

/*        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_email);
        dialog.setTitle("????????? ?????? ???????????????");
        dialog.setMessage("??????????????? ????????? ???????????? ????????????.");*/



        missionname = (TextView) findViewById(R.id.missionname);
        missioncontent = (TextView) findViewById(R.id.missioncontent);
        delstart = (Button) findViewById(R.id.delstart);
        complete = (Button) findViewById(R.id.complete);

        String requestUrl =
                "http://" + ip + ":8081/zipcock/android/delstart.do";
        requestUrl += "?mission_num=" + num;
        Log.d(TAG, requestUrl);

        new AsyncHttpRequest().execute(requestUrl);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HMapviewMain.this)
                    .setTitle("?????? ??????")
                    .setMessage("???????????? ?????????????????????????")
                    .setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (timerTask != null) {
                                timerTask.cancel();
                                timercheck = 2;

                                String requestUrl =
                                        "http://" + ip + ":8081/zipcock/android/Hcomplete.do";
                                requestUrl += "?mission_num=" + num;
                                Log.d(TAG, requestUrl);

                                new AsyncHttpRequest().execute(requestUrl);
                                Toast.makeText(getApplicationContext(),
                                        "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HelperMain.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(HMapviewMain.this,
                                        "?????? ???????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(HMapviewMain.this,
                                    "?????????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            }
        });
    }


    @Override
    public void onDestroy() {
/*        if(!(timerTask==null)) {
            timerTask.cancel();
        }*/
        super.onDestroy();
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
                success = Integer.parseInt(jsonObject.getString("success"));
                if (success == 1) {
                    dInfo = jsonObject.getJSONObject("getMission");

                    mission_num = dInfo.getInt("mission_num");

                    end = dInfo.getString("mission_end").toString().split("\\|");
                    endpoint_x = end[1];
                    endpoint_y = end[0];
                    endpoint = end[2] + end[3];

                    waycheck = dInfo.getString("mission_waypoint").toString();
                    if (!(waycheck.equals("null") || waycheck.equals("||"))) {
                        way = dInfo.getString("mission_waypoint").toString().split("\\|");
                        waypoint_x = way[1];
                        waypoint_y = way[0];
                        waypoint = way[2] + way[3];
                    }


                    missionContent = dInfo.getString("mission_content").toString();
                    missionName = dInfo.getString("mission_name").toString();


                } else {
                    Log.i(TAG, "??????????????? ???????????? ??????");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return sBuffer.toString();
        }

        //doInBackground???????????? ??????????????? ?????????????????? ???????????? ?????????
        @Override
        protected void onPostExecute(String s) {

            Log.i(TAG, s);
            /*            dialog.dismiss();*/

            if (dInfo != null) {
                missionname.setText(missionName);
                missioncontent.setText(missionContent);


                delstart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (timercheck == 1) {
                            Toast.makeText(HMapviewMain.this,
                                    "???????????? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        } else if (timercheck == 2) {
                            Toast.makeText(HMapviewMain.this,
                                    "???????????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(HMapviewMain.this)
                            .setTitle("?????? ??????")
                            .setMessage("???????????? ?????????????????????????")
                            .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        //????????????
                                        if (NaviClient.getInstance().isKakaoNaviInstalled(HMapviewMain.this)) {
                                            LocationManager locationManager =
                                                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                            LocationListener locationListener = new LocationListener() {
                                                @Override
                                                public void onLocationChanged(@NonNull android.location.Location location) {
                                                    helplocation_y = String.valueOf(location.getLongitude()).toString();//?????? y
                                                    helplocation_x = String.valueOf(location.getLatitude()).toString();//?????? x
                                                }
                                                @Override
                                                public void onProviderEnabled(@NonNull String provider) {

                                                }

                                                @Override
                                                public void onProviderDisabled(@NonNull String provider) {

                                                }

                                                @Override
                                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                                }
                                            };

                                            int permissionCheck = ContextCompat.checkSelfPermission(HMapviewMain.this, Manifest.permission.ACCESS_FINE_LOCATION);

                                            if(permissionCheck == PackageManager.PERMISSION_DENIED){ //?????? ?????? ??????

                                                //?????? ?????? ??????
                                                ActivityCompat.requestPermissions(HMapviewMain.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                            }
                                            else if(permissionCheck
                                                    == PackageManager.PERMISSION_GRANTED){
                                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                        1000,
                                                        1,locationListener);
                                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                        1000,
                                                        1,locationListener);

                                                Toast.makeText(getApplicationContext(),
                                                        "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                                                timerTask = createTimerTask();
                                                timer.schedule(timerTask, 100, 3000);
                                                timercheck = 1;


                                                if (!(waycheck.equals("null") || waycheck.equals("||"))) {
                                                startActivity(
                                                        NaviClient.getInstance().navigateIntent(
                                                                new Location(endpoint, endpoint_y, endpoint_x),
                                                                new NaviOption(CoordType.WGS84),
                                                                new ArrayList(
                                                                        Collections.singleton(new Location(waypoint, waypoint_y, waypoint_x))
                                                                )
                                                        )
                                                );
                                            } else {
                                                startActivity(
                                                        NaviClient.getInstance().navigateIntent(
                                                                new Location(endpoint, endpoint_y, endpoint_x),
                                                                new NaviOption(CoordType.WGS84)
                                                        )
                                                );
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),
                                                "?????????????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                        startActivity(
                                            new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(Constants.WEB_NAVI_INSTALL)
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        );
                                    }
                                }
                            })
                            .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(HMapviewMain.this,
                                            "?????????????????????.", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                        }
                    }
                });
            } else if (dInfo == null) {
                delstart.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                missioncontent.setTextSize(30);
                missioncontent.setWidth(300);
                missioncontent.setText("???????????? ???????????? ????????????!");
            }
        }

    }

    private TimerTask createTimerTask() {
        String ip = getResources().getString(R.string.ip);

        SharedPreferences pref = getSharedPreferences("user_id", MODE_PRIVATE);
        String member_id = pref.getString("member_id", null);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Handler mHandler = new Handler(Looper.getMainLooper());

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String helpLocation = helplocation_y + "|" + helplocation_x;
                        String insertUrl =
                                "http://" + ip + ":8081/zipcock/android/insertLocation.do";
                        insertUrl += "?mission_start=" + helpLocation.toString();
                        insertUrl += "&mission_Hid=" + member_id.toString();
                        insertUrl += "&mission_num=" + mission_num;
                        Log.d(TAG, insertUrl);
                        new AsyncHttpRequest().execute(insertUrl);

                    }

                }, 0);
                Log.i("?????????", "1");
                Log.i("?????????2", "??????" + helplocation_x + "??????" + helplocation_y);

            }
        };
        return timerTask;
    }
}