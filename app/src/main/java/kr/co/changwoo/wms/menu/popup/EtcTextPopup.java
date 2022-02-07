package kr.co.changwoo.wms.menu.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.changwoo.wms.R;
import kr.co.changwoo.wms.common.SharedData;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.SinCstModel;
import kr.co.changwoo.wms.model.SinListModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtcTextPopup {

    Activity mActivity;
    OneBtnPopup mOneBtnPopup;
    Context mContext;

    Dialog dialog;
    List<SinCstModel.Item> mWhModel;
    Handler mHandler;
    ListView mListView;
    String date, cst_cd, cst_nm;
    EditText tv_etc;

    public EtcTextPopup(Activity activity, int title, String getTime, String cst_code, String cst_name, Handler handler){
        mActivity = activity;
        mHandler = handler;
        showPopUpDialog(activity, title, getTime, cst_code, cst_name);
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

    private void showPopUpDialog(final Activity activity, int title, String getTime, String cst_code, String cst_name){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.popup_etc_text);
        date = getTime;
        cst_cd = cst_code;
        cst_nm = cst_name;

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        tv_etc = dialog.findViewById(R.id.tv_etc);
        iv_title.setBackgroundResource(title);

        /*dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });*/

        dialog.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.d("cst_code::", cst_cd);
                Log.d("cst_name::", cst_nm);
                Log.d("etc::", tv_etc.getText().toString());*/

                if (tv_etc.getText().toString().equals("")){
                    mOneBtnPopup = new OneBtnPopup(mActivity, "비고사항을 입력해 주세요.", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mOneBtnPopup.hideDialog();
                            }
                        }
                    });
                    //Utils.Toast(activity, "비고사항을 입력해 주세요.");
                    return;
                }
                requestSinSave();
            }
        });

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        //requestLocation();
        dialog.show();
    }

    /**
     * 입고마감
     */
    private void requestSinSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String userID = (String) SharedData.getSharedData(mActivity, SharedData.UserValue.USER_ID.name(), "");
        Call<SinListModel> call = service.sp_pda_sin_clo("sp_pda_sin_clo", userID, date, cst_cd, userID, tv_etc.getText().toString());

        call.enqueue(new Callback<SinListModel>() {
            @Override
            public void onResponse(Call<SinListModel> call, Response<SinListModel> response) {
                if (response.isSuccessful()) {
                    SinListModel model = response.body();
                    if (model != null) {
                        //mShipScanModel.getFlag().equals("OK")
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(mActivity, "'"+date+"'일 "+ cst_nm + "입고 등록이 완료 되었습니다.",
                                    R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        Message msg1 = mHandler.obtainMessage();
                                        msg1.what = 1;
                                        msg1.obj = "ok";
                                        mHandler.sendMessage(msg1);
                                        hideDialog();
                                    }
                                }
                            });


                        } else {
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SinListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                //Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close 입고마감

}
