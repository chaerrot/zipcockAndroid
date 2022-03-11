package com.kosmo.zipcock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MemberChoice extends AppCompatActivity {

    private static final String TAG = "lecture";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_member_choice);

        /*회원가입 고르는 버튼*/
        Button cMember = (Button)findViewById(R.id.cMember);
        Button hMember = (Button)findViewById(R.id.hMember);

        /* 사용자 회원가입 */
        cMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CMemberFragment.class);
                startActivity(intent);
            }
        });

        /* 헬퍼 회원가입 */
        hMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HMemberFragment.class);
                startActivity(intent);
            }
        });

    }
}