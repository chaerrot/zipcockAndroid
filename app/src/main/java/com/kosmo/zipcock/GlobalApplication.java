package com.kosmo.zipcock;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override public void onCreate() {
        super.onCreate(); KakaoSdk.init(this, "0f59ea6fd622d1210b2b8d378ecb16d0");
    }


}
