package kr.co.changwoo.wms.menu.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.changwoo.wms.R;
import kr.co.changwoo.wms.common.SharedData;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.ShipBoxModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipListPopup {

    Activity mActivity;
    Dialog dialog;
    String getTime, userID;
    Handler mHandler;
    ListAdapter mAdapter;
    ShipBoxModel mBoxModel;
    List<ShipBoxModel.Item> mBoxList;
    String s_ship_date, s_ship_no;

    public ShipListPopup(Activity activity, int title, String ship_date, String ship_no, Handler handler) {
        mActivity = activity;
        mHandler = handler;
        showPopUpDialog(activity, title, ship_date, ship_no);

    }



    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();

        }
    }

    public boolean isShowDialog() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    private void showPopUpDialog(Activity activity, int title, String ship_date, String ship_no) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.popup_ship_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);
        userID = (String) SharedData.getSharedData(mActivity, SharedData.UserValue.USER_ID.name(), "");
        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        //tv_cst_nm = dialog.findViewById(R.id.tv_cst_nm);
        ListView listView = dialog.findViewById(R.id.list);
        mAdapter = new ListAdapter();
        listView.setAdapter(mAdapter);
        s_ship_date = ship_date;
        s_ship_no = ship_no;

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        sp_pda_reqlist_popup();

        dialog.show();
    }//Close Show popup


    /**
     * 출고지시서 조회
     */
    private void sp_pda_reqlist_popup() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Call<ShipBoxModel> call = service.sp_pda_pop_ship_list("sp_pda_ship_list", userID, getTime, s_ship_date, s_ship_no);

        call.enqueue(new Callback<ShipBoxModel>() {
            @Override
            public void onResponse(Call<ShipBoxModel> call, Response<ShipBoxModel> response) {
                if (response.isSuccessful()) {
                    mBoxModel = response.body();
                    ShipBoxModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mBoxList = model.getItems();
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Utils.Toast(mActivity, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mActivity, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ShipBoxModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mActivity, mActivity.getString(R.string.error_network));
            }
        });
    }//Close

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        List<ShipBoxModel.Item> itemsList;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mBoxList == null) {
                return 0;
            }
            return mBoxList.size();
        }


        @Override
        public ShipBoxModel.Item getItem(int position){
            return mBoxList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_ship_list, null);
                v.setTag(holder);

                holder.tv_no = v.findViewById(R.id.tv_no);
                holder.tv_itm_name = v.findViewById(R.id.tv_itm_name);
                holder.tv_order_qty = v.findViewById(R.id.tv_order_qty);
                holder.tv_out_qty = v.findViewById(R.id.tv_out_qty);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            final ShipBoxModel.Item data = mBoxList.get(position);
            holder.tv_no.setText(Integer.toString(position + 1));
            holder.tv_itm_name.setText(data.getItm_name());
            holder.tv_order_qty.setText(Integer.toString(data.getSreq_qty()));
            holder.tv_out_qty.setText(Integer.toString(data.getShip_qty()));


            //특정 데이터시 텍스트 색 변경
            try{
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (mBoxList.get(i).getChk_yn().equals("N")) {
                        if (position == i) {
                            holder.tv_no.setTextColor(Color.RED);
                            holder.tv_itm_name.setTextColor(Color.RED);
                            holder.tv_order_qty.setTextColor(Color.RED);
                            holder.tv_out_qty.setTextColor(Color.RED);
                        }
                    }

                }//Close for
            }catch (Exception e){
                e.printStackTrace();
            }


            return v;
        }

        class ViewHolder {
            TextView tv_no;
            TextView tv_itm_name;
            TextView tv_order_qty;
            TextView tv_out_qty;
        }
    }//Close Adapter


}//Close popup