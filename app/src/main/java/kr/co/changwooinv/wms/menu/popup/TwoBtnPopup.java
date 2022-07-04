package kr.co.changwooinv.wms.menu.popup;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.changwooinv.wms.R;

public class TwoBtnPopup {
    Activity mActivity;

    Dialog dialog;
    Handler mHandler;

    public TwoBtnPopup(Activity activity, String message, int title, Handler handler){
        mActivity = activity;
        mHandler = handler;
        showPopUpDialog(activity, message, title);
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowDialog(){
        if(dialog != null && dialog.isShowing()){
            return true;
        }else{
            return false;
        }
    }

    private void showPopUpDialog(Activity activity, String message, int title){

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.popup_message);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        TextView tv_message = dialog.findViewById(R.id.tv_message);
        tv_message.setText(message);

        dialog.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj = "확인";
                mHandler.sendMessage(msg);
            }
        });

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj = "취소";
                mHandler.sendMessage(msg);
                hideDialog();
            }
        });

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                msg.obj = "종료";
                mHandler.sendMessage(msg);
                hideDialog();
            }
        });

        dialog.show();
    }
}
