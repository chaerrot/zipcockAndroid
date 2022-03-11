package com.kosmo.zipcock;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class faqFragment extends AppCompatActivity {
    private RecyclerView recyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_fragment);
        recyclerview = findViewById(R.id.recyclerview);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<faqAdapter.Item> data = new ArrayList<>();
        data.add(new faqAdapter.Item(faqAdapter.HEADER, "Q. 집콕은 어떤 서비스인가요?"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "A. '집콕'은 믿을 수 있는 심부름 문화를 위한"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "심부름 대행 플랫폼 서비스입니다 :-)"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "집콕을 통해 생활 속 불편함을 해결하고"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "편리한 생활을 즐기세요~"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "또한 셔틀콕은 고객의 심부름을 수행함으로써"));
        data.add(new faqAdapter.Item(faqAdapter.CHILD, "보람을 느끼고 수익을 창출할수도 있습니다!"));

        faqAdapter.Item places = new faqAdapter.Item(faqAdapter.HEADER, "Q. 어떤 심부름이 가능한가요?");
        places.invisibleChildren = new ArrayList<>();
        places.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "A. 홈페이지나 앱에 접속하시면"));
        places.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "심부름 가능한 항목을 확인할 수 있습니다."));
        places.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "대부분 심부름이 가능하지만, 담배나 주류의 경우"));
        places.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "고객님의 신분증을 보여주셔야합니다."));
        data.add(places);
        recyclerview.setAdapter(new faqAdapter(data));

        faqAdapter.Item places0 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 결제는 어떻게 하나요?");
        places0.invisibleChildren = new ArrayList<>();
        places0.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "A. 심부름 등록시 결제가 바로 진행되며"));
        places0.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "취소에 따른 환불은 결제사마다 차이가 있으며,"));
        places0.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "영업일 기준 1~3일정도 소요될 수 있습니다."));
        data.add(places0);
        recyclerview.setAdapter(new faqAdapter(data));

        faqAdapter.Item places1 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 심부름비는 어떻게 정하나요?");
        places1.invisibleChildren = new ArrayList<>();
        places1.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "A. 심부름비는 최소 3,000원부터 가능합니다."));
        places1.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "물품/음식비는 심부름비에 포함해서 결제해주세요."));
        data.add(places1);
        recyclerview.setAdapter(new faqAdapter(data));


        faqAdapter.Item places2 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 매칭된 셔틀콕과는 어떻게 연락하나요?");
        places2.invisibleChildren = new ArrayList<>();
        places2.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "A. 심부름이 매칭되면 지원한 셔틀콕과"));
        places2.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "1:1 채팅이 활성화됩니다."));
        data.add(places2);
        recyclerview.setAdapter(new faqAdapter(data));

        faqAdapter.Item places4 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 이미 수락한 심부름을 취소해도 괜찮나요?");
        places4.invisibleChildren = new ArrayList<>();
        places4.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "A. 수락한 심부름을 헬퍼가 취소할 수는 없습니다."));
        places4.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "취소 혹은 노쇼할 경우 헬퍼 활동이 정지되며"));
        places4.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "수익금 인출 신청 또한 불가능한 점을 유의바랍니다."));
        data.add(places4);
        recyclerview.setAdapter(new faqAdapter(data));


        faqAdapter.Item places3 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 셔틀콕은 어떻게 되나요?");
        places3.invisibleChildren = new ArrayList<>();
        places3.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "웹페이지의 상단에 Join 버튼을 누르신다음, "));
        places3.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "셔틀콕 회원가입을 누르신 후"));
        places3.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "인적사항 등을 적어주시면 됩니다."));
        data.add(places3);
        recyclerview.setAdapter(new faqAdapter(data));


        faqAdapter.Item places5 = new faqAdapter.Item(faqAdapter.HEADER, "Q. 회원탈퇴는 어떻게 하나요?");
        places5.invisibleChildren = new ArrayList<>();
        places5.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "[마이페이지 -> 회원탈퇴]에서 가능합니다."));
        places5.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "탈퇴시점에서 모든 개인정보는 삭제됩니다."));
        places5.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "탈퇴 전 불편사항이 있으시다면 고객센터에"));
        places5.invisibleChildren.add(new faqAdapter.Item(faqAdapter.CHILD, "문의주시면 해결해드리겠습니다 :-)"));
        data.add(places5);
        recyclerview.setAdapter(new faqAdapter(data));

    }
}

