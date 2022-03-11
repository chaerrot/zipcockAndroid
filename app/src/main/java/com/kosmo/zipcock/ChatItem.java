package com.kosmo.zipcock;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatItem extends LinearLayout {

    TextView titleView;
    TextView roomIdView;

    //생성자 메서드
    public ChatItem(Context context){
        super(context);

        //LayoutInflater : java파일하고 xml파일하고 연결하는 거라고 볼수있다.
        //레이아웃 전개를 위해 LayoutInflater객체를 생성한다.
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //부분뷰로 제작한 XML파일을 해당 Java파일에 전개한다.(XML과 Java를 연결하는 부분)
        inflater.inflate(R.layout.chat_item, this, true);
        //데이터를 출력할 위젯을 얻어온다.
        titleView = findViewById(R.id.mission_name);
        roomIdView = findViewById(R.id.chat_roomId);



    }


    //각 항목을 설정한 Setter()메서드 정의ㄴ
//    public void setTitle(String title){
//        titleView.setText(title);
//    }
//    public void setHid(String hid){
//        hidView.setText(hid);
//    }

}
