package kr.co.changwooinv.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;


import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.common.SharedData;
import kr.co.changwooinv.wms.menu.login.LoginActivity;


public class SplashActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_splash);

        mContext = this;

        //ImageView iv_splash = findViewById(R.id.iv_splash);

        SharedData.setSharedData(mContext, SharedData.UserValue.IS_LOGIN.name(), false);

        Handler delayHandler = new Handler(Looper.getMainLooper());
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 1000);
    }
}
