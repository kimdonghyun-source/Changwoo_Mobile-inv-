package kr.co.changwoo.wms.menu.ship;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.changwoo.wms.R;
import kr.co.changwoo.wms.common.SharedData;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.custom.CommonFragment;
import kr.co.changwoo.wms.honeywell.AidcReader;
import kr.co.changwoo.wms.menu.popup.OneBtnPopup;
import kr.co.changwoo.wms.menu.popup.TwoBtnPopup;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.ShipBoxModel;
import kr.co.changwoo.wms.model.ShipDetailModel;
import kr.co.changwoo.wms.model.SinListModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipDeatilFragment extends CommonFragment {

    Context mContext;
    List<String> mIncode;
    String barcodeScan, ship_no, cst_name, ship_date, ship_seq, getTime;
    TextView tv_cst_name, tv_ship_no;
    ShipDetailModel mDetailModel;
    List<ShipDetailModel.Item> mDetailList;
    ListAdapter mAdapter;
    RecyclerView ShipDetail_list;
    ImageButton bt_next;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup twoBtnPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();


    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship_detail, container, false);

        Bundle arguments = getArguments();
        ship_no = arguments.getString("ship_no");
        cst_name = arguments.getString("cst_name");
        ship_date = arguments.getString("ship_date");
        ship_seq = arguments.getString("ship_seq");

        tv_ship_no = v.findViewById(R.id.tv_ship_no);
        tv_cst_name = v.findViewById(R.id.tv_cst_name);
        ShipDetail_list = v.findViewById(R.id.ShipDetail_list);
        bt_next = v.findViewById(R.id.bt_next);

        bt_next.setOnClickListener(onClickListener);

        ShipDetail_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        ShipDetail_list.setAdapter(mAdapter);

        tv_ship_no.setText(ship_no);
        tv_cst_name.setText(cst_name);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);

        BoxReadList();

        return v;

    }//Close onCreateView



    @Override
    public void onResume() {
        super.onResume();
        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    BarcodeReadEvent event = (BarcodeReadEvent) msg.obj;
                    String barcode = event.getBarcodeData();
                    barcodeScan = barcode;






                }
            }
        });

    }//Close onResume

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_next:
                    if (mAdapter.getItemCount() <= 0){
                        Utils.Toast(mContext, "출하등록할 내역이 없습니다.");
                        return;
                    }
                    requestSinSave();
                    break;
            }

        }
    };//Close onClick


    /**
     * 출하예정LIST
     */
    private void BoxReadList() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipDetailModel> call = service.sp_pda_ship_list("sp_pda_ship_list", "",  getTime, ship_date, ship_seq);

        call.enqueue(new Callback<ShipDetailModel>() {
            @Override
            public void onResponse(Call<ShipDetailModel> call, Response<ShipDetailModel> response) {
                if (response.isSuccessful()) {
                    mDetailModel = response.body();
                    final ShipDetailModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mDetailModel != null) {
                        if (mDetailModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {

                                mDetailList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipDetailModel.Item item = (ShipDetailModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                }
                                mAdapter.notifyDataSetChanged();
                                ShipDetail_list.setAdapter(mAdapter);
                            }

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
            public void onFailure(Call<ShipDetailModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 출하등록
     */
    private void requestSinSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        Call<ShipDetailModel> call = service.sp_pda_ship_clo("sp_pda_ship_clo", "", getTime, ship_date, ship_seq);

        call.enqueue(new Callback<ShipDetailModel>() {
            @Override
            public void onResponse(Call<ShipDetailModel> call, Response<ShipDetailModel> response) {
                if (response.isSuccessful()) {
                    ShipDetailModel model = response.body();
                    if (model != null) {
                        //mShipScanModel.getFlag().equals("OK")
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "'"+tv_ship_no.getText().toString() +"'의 출하등록이 완료 되었습니다.",
                                    R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                        getActivity().finish();
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
            public void onFailure(Call<ShipDetailModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close 제품출하등록


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<ShipDetailModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<ShipDetailModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ShipDetailModel.Item> getData() {
            return itemsList;
        }

        public void addData(ShipDetailModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_ship_detail_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ShipDetailModel.Item item = itemsList.get(position);

            holder.tv_box_no.setText(Integer.toString(item.getBox_no()));
            holder.tv_itm_name.setText(item.getItm_name());
            holder.tv_order_qty.setText(Integer.toString(item.getSreq_qty()));
            holder.tv_out_qty.setText(Integer.toString(item.getShip_qty()));

        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_itm_name;
            TextView tv_order_qty;
            TextView tv_out_qty;
            TextView tv_box_no;

            public ViewHolder(View view) {
                super(view);

                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_order_qty = view.findViewById(R.id.tv_order_qty);
                tv_out_qty = view.findViewById(R.id.tv_out_qty);
                tv_box_no = view.findViewById(R.id.tv_box_no);


            }
        }
    }//Close Adapter



}//Close Fragment
