package kr.co.changwooinv.wms.menu.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;


import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.common.Define;
import kr.co.changwooinv.wms.custom.CommonCompatActivity;
import kr.co.changwooinv.wms.menu.login.LoginActivity;
import kr.co.changwooinv.wms.menu.popup.TwoBtnPopup;


public class MainActivity extends CommonCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.act_main);



        findViewById(R.id.bt_menu_1).setOnClickListener(onClickListener); //재고확인
        findViewById(R.id.bt_menu_2).setOnClickListener(onClickListener); //입고관리
        findViewById(R.id.bt_menu_3).setOnClickListener(onClickListener); //출고관리
        findViewById(R.id.bt_menu_4).setOnClickListener(onClickListener); //출고관리
        findViewById(R.id.bt_menu_5).setOnClickListener(onClickListener); //출고관리

        findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TwoBtnPopup(MainActivity.this, "로그아웃 하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            goLogin();
                        }
                    }
                });
            }
        });
    }

    private void goLogin(){
        Intent i = new Intent(mContext,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            Intent intent = new Intent(mContext, BaseActivity.class);
            switch (view){



                //재고확인
                case R.id.bt_menu_1:
                    intent.putExtra("menu", Define.MENU_INV_OK);
                    break;

                //입고관리
                case R.id.bt_menu_2:
                    intent.putExtra("menu", Define.MENU_INV_ITM);
                    break;

                //출고관리
                case R.id.bt_menu_3:
                    intent.putExtra("menu", Define.MENU_SHIP);
                    break;

                //이동관리
                case R.id.bt_menu_4:
                    intent.putExtra("menu", Define.MENU_MOVE);
                    break;

                //재고실사
                case R.id.bt_menu_5:
                    intent.putExtra("menu", Define.MENU_INV_LOT);
                    break;


            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };
}
