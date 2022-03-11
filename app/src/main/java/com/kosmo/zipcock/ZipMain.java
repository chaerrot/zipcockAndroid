package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ZipMain extends AppCompatActivity {

    private static final String TAG= "lecture";

    TabLayout tabLayout;
    MainFragment mainFragment;
    ChatFragment chatFragment;
    MapFragment mapFragment;
    MypageFragment mypageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_main);


        tabLayout=findViewById(R.id.tabmenu);

        mainFragment = new MainFragment();
        chatFragment = new ChatFragment();
        mapFragment = new MapFragment();
        mypageFragment = new MypageFragment();

        getSupportFragmentManager()
                .beginTransaction().replace(R.id.container, mainFragment).commit();



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "POS:"+tab.getPosition());

                switch (tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, mainFragment).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, chatFragment).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, mapFragment).commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, mypageFragment).commit();
                        break;
                    default:
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}