package com.kosmo.zipcock;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CockList extends LinearLayout {

    //XML레이아웃에서 출력할 항목을 멤버변수로 선언한다.
    TextView mission_category; //심부름 카테고리
    TextView mission_status;
    TextView mission_name; //내용


    //생성자 메서드
    public CockList(Context context){
        super(context);

        //LayoutInflater : java파일하고 xml파일하고 연결하는 거라고 볼수있따.
        //레이아웃 전개를 위해 LayoutInflater객체를 생성한다.
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //부분뷰로 제작한 XML파일을 해당 Java파일에 전개한다.(XML과 Java를 연결하는 부분)
        inflater.inflate(R.layout.cock_list, this, true);
        //데이터를 출력할 위젯을 얻어온다.
        mission_category = findViewById(R.id.mission_category);
        mission_status = findViewById(R.id.mission_status);
        mission_name = findViewById(R.id.mission_name);


    }

    public void setCategory(String category){
        mission_category.setText(category);
    }
    public void setStatus(String status){
        mission_status.setText(status);
    }
    public void setTitle(String title){
        mission_name.setText(title);
    }

}
