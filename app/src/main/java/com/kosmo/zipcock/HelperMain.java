package com.kosmo.zipcock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class HelperMain extends AppCompatActivity {
    private static final String TAG= "lecture";

    TabLayout tabLayout;

    HMainFragment hmainFragment;
    ChatFragment chatFragment;
    HMapFragMent hmapFragment;
    HMypageFragment hmypageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_main);

        tabLayout=findViewById(R.id.Htabmenu);

        hmainFragment = new HMainFragment();
        chatFragment = new ChatFragment();
        hmapFragment = new HMapFragMent();
        hmypageFragment = new HMypageFragment();

        getSupportFragmentManager()
                .beginTransaction().replace(R.id.container, hmainFragment).commit();



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "POS:"+tab.getPosition());

                switch (tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, hmainFragment).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, chatFragment).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, hmapFragment).commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, hmypageFragment).commit();
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