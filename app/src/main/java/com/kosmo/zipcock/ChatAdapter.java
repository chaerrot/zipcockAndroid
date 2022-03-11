package com.kosmo.zipcock;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private static final String TAG = "zipcock";

    ArrayList<MessageItem> messageItems;
    LayoutInflater layoutInflater;

    public ChatAdapter(ArrayList<MessageItem> messageItems, LayoutInflater layoutInflater) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        MessageItem item=messageItems.get(position);


        Log.i(TAG, "messageItem: "+item.toString());
        Log.i(TAG, "내아이디: "+G.nickName);
        Log.i(TAG, "메세지 작성자: "+item.getName());



        //재활용할 뷰는 사용하지 않음!!
        View itemView=null;

        //만들어진 itemView에 값들 설정
//        TextView tv_room_name = itemView.findViewById(R.id.tv_room_name);


        //메세지가 내 메세지인지??
        if(item.getName().equals(G.nickName)){
            itemView= layoutInflater.inflate(R.layout.my_msgbox,viewGroup,false);
        }else{
            itemView= layoutInflater.inflate(R.layout.other_msgbox,viewGroup,false);
        }

        ImageView iv= itemView.findViewById(R.id.iv);
        TextView tvName= itemView.findViewById(R.id.tv_name);
        TextView tvMsg= itemView.findViewById(R.id.tv_msg);
        TextView tvTime= itemView.findViewById(R.id.tv_time);
        TextView tvLastmessage = itemView.findViewById(R.id.chat_lastMessage);

        tvName.setText(item.getName());
        tvMsg.setText(item.getMessage());
        tvTime.setText(item.getTime());


        //Glide.with(itemView).load(item.getPofileUrl()).into(iv);

        return itemView;
    }
}
