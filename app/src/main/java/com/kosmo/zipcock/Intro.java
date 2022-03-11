package com.kosmo.zipcock;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kosmo.zipcock.databinding.ActivityIntroBinding;

/*
Manifest.xml파일을 수정하여 Intro액티비티가 가장 먼저 실행되도록
설정한다.
 */
public class Intro extends AppCompatActivity {

    //일정시간 이후 실행하기 위해 Handler객체를 생성한다.
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //인트로 화면 이후에 메인액티비티를 실행하기 위해 인텐트 객체를 생성한다.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //준비한 액티비티를 실행한다.
            startActivity(intent);
            /*
            액티비티가 실행될때 애니메이션 효과를 부여한다.
            인자1 : 새롭게 실행되는 액티비티의 애니메이션 효과
            인자2 : 종료되는 액티비티에 적용될 효과
             */
            overridePendingTransition(R.anim.slide_in_down, R.anim.hold);
            //인트로 액티비티를 종료한다.(다시 돌아갈 이유가 없기 때문에...)
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //인트로 액티비티가 실행될때 상단의 액션바를 숨김처리 한다.
    }
    //액티비티 실행시 3번째로 호출되는 수명주기 메서드
    @Override
    protected void onResume() {
        super.onResume();
        //Intro액티비티에 진입한 후 2초후에 runnable객체를 실행하여 MainActivity를 띄운다.
        handler.postDelayed(runnable, 2000);
    }
    //액티비티 종료시 호출되는 수명주기 메서드
    @Override
    protected void onPause() {
        super.onPause();
        //Intro가 종료될때 handler에 예약해놓은 작업을 취소한다.
        handler.removeCallbacks(runnable);
    }
}