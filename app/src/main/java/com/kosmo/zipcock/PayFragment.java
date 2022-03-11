package com.kosmo.zipcock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iamport.sdk.data.sdk.IamPortRequest;
import com.iamport.sdk.data.sdk.PG;
import com.iamport.sdk.data.sdk.PayMethod;
import com.iamport.sdk.domain.core.Iamport;

import java.util.Date;

import kotlin.Unit;


public class PayFragment extends AppCompatActivity {

    private static final String TAG = "zipcock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay);

        Intent intent = getIntent(); // 폼값 받기위한 선언
        String title = intent.getStringExtra("title");
        String cost = intent.getStringExtra("cost");

        Iamport.INSTANCE.init(this);

        IamPortRequest request = IamPortRequest.builder()
                .pg(PG.kcp.makePgRawName(""))
                .pay_method(PayMethod.card.name())
                .name(title) //심부름 이름
                .merchant_uid("mid_" + (new Date()).getTime())
                .amount(cost) // 심부름 가격
                .buyer_name("집콕").build(); // 결제하는 사람 이름

        Iamport.INSTANCE.payment("imp98305900", null, null, request,
                iamPortApprove -> {
                    // (Optional) CHAI 최종 결제전 콜백 함수.
                    return Unit.INSTANCE;
                }, iamPortResponse -> {
                    // 최종 결제결과 콜백 함수.x
                    String responseText = iamPortResponse.toString();
                    Log.d("IAMPORT_SAMPLE", responseText);

                    if(iamPortResponse.getImp_success() == true) {
                        Toast.makeText(getApplicationContext(), "심부름 등록완료! 감사합니다.", Toast.LENGTH_LONG).show();

                        Intent intent1 = new Intent(getApplicationContext(), ZipMain.class);
                        startActivity(intent1);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "결제를 취소하셨습니다.", Toast.LENGTH_LONG).show();

                        Intent intent1 = new Intent(getApplicationContext(), Request.class);
                        startActivity(intent1);
                    }
                    return Unit.INSTANCE;
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Iamport.INSTANCE.close();
    }

}




