package kr.co.changwoo.wms.menu.popup;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.changwoo.wms.R;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.model.SinCstModel;

public class LocationSinCstList {

    Activity mActivity;

    Dialog dialog;
    List<SinCstModel.Item> mWhModel;
    Handler mHandler;
    ListView mListView;
    ListAdapter mAdapter;

    public LocationSinCstList(Activity activity, List<SinCstModel.Item> list, int title, Handler handler){
        mActivity = activity;
        mWhModel = list;
        mHandler = handler;
        showPopUpDialog(activity, title);
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

    private void showPopUpDialog(Activity activity, int title){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.popup_location_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        //팝업을 맨 위로 올려야 함.
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView iv_title = dialog.findViewById(R.id.iv_title);
        iv_title.setBackgroundResource(title);

        List<String> list = new ArrayList<>();
        for (SinCstModel.Item item : mWhModel)
            list.add(item.getCst_code());


        mListView = dialog.findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mListView.setAdapter(mAdapter);

        dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDialog();
            }
        });

        //requestLocation();
        dialog.show();
    }


    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ListAdapter() {
            mInflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            if (mWhModel == null) {
                return 0;
            }

            return mWhModel.size();
        }

        @Override
        public SinCstModel.Item getItem(int position) {
            return mWhModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ListAdapter.ViewHolder holder;
            if (v == null) {
                holder = new ListAdapter.ViewHolder();
                v = mInflater.inflate(R.layout.cell_pop_cst_location, null);
                v.setTag(holder);

                holder.tv_code = v.findViewById(R.id.tv_code);
                holder.tv_name = v.findViewById(R.id.tv_name);
            } else {
                holder = (ListAdapter.ViewHolder) v.getTag();
            }

            final SinCstModel.Item item = mWhModel.get(position);
            holder.tv_code.setText(item.getCst_code());
            holder.tv_name.setText(item.getCst_name());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = item;
                    mHandler.sendMessage(msg);

                }
            });

            return v;
        }

        class ViewHolder {
            TextView tv_code;
            TextView tv_name;
        }
    }


}
