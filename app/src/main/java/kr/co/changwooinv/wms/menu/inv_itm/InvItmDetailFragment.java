package kr.co.changwooinv.wms.menu.inv_itm;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
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

import java.util.ArrayList;
import java.util.List;

import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.common.SharedData;
import kr.co.changwooinv.wms.common.Utils;
import kr.co.changwooinv.wms.custom.CommonFragment;
import kr.co.changwooinv.wms.honeywell.AidcReader;
import kr.co.changwooinv.wms.menu.popup.OneBtnPopup;
import kr.co.changwooinv.wms.menu.popup.TwoBtnPopup;
import kr.co.changwooinv.wms.model.InvItmScanModel;
import kr.co.changwooinv.wms.model.ResultModel;
import kr.co.changwooinv.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Create date: 2022.07.04
* Description: 입고관리 디테일
*
* */

public class InvItmDetailFragment extends CommonFragment {

    Context mContext;
    String barcodeScan, wh_code, loc_code, userID, itm_code;
    TextView item_date, tv_wh, tv_loc, tv_itm_qty;
    RecyclerView inv_itm_listView;

    InvItmScanModel mInvItmModel = null;
    InvItmScanModel.Item order = null;
    List<InvItmScanModel.Item> mInvItmList;
    int mPosition = -1;

    ListAdapter mAdapter;

    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_inv_itm_detail, container, false);

        Bundle arguments = getArguments();


        item_date = v.findViewById(R.id.item_date);
        tv_wh = v.findViewById(R.id.tv_wh);
        tv_loc = v.findViewById(R.id.tv_loc);
        tv_itm_qty = v.findViewById(R.id.tv_itm_qty);
        inv_itm_listView = v.findViewById(R.id.inv_itm_listView);

        inv_itm_listView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        inv_itm_listView.setAdapter(mAdapter);



        if (arguments != null){
            mInvItmModel = (InvItmScanModel) arguments.getSerializable("model");
            mPosition = arguments.getInt("position");
            order = mInvItmModel.getItems().get(mPosition);
            wh_code = arguments.getString("wh_code");
            loc_code = arguments.getString("loc_code");
            tv_wh.setText(wh_code);
            tv_loc.setText(loc_code);
            tv_itm_qty.setText(order.getItm_Code() + "    " + order.getQTY());
            item_date.setText(order.getDate_dt());
            itm_code = order.getItm_Code();
            InvItmList();
        }

        userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

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

    /**
     * 입고관리 리스트
     */
    private void InvItmList() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<InvItmScanModel> call = service.InvItmList("sp_pda_inItmSum_List", userID, item_date.getText().toString(), wh_code, loc_code, "B", itm_code );

        call.enqueue(new Callback<InvItmScanModel>() {
            @Override
            public void onResponse(Call<InvItmScanModel> call, Response<InvItmScanModel> response) {
                if (response.isSuccessful()) {
                    mInvItmModel = response.body();
                    final InvItmScanModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mInvItmModel != null) {
                        if (mInvItmModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                if (mAdapter.itemsList != null){
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                                mInvItmList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    InvItmScanModel.Item item = (InvItmScanModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                        } /*else {
                            Utils.Toast(mContext, model.getMSG());
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();

                        }*/
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }


            @Override
            public void onFailure(Call<InvItmScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close InvItmList

    /**
     * 리스트내역 삭제
     */
    private void ListDelete(int no) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<InvItmScanModel> call = service.InvItmDelete("sp_pda_in_clo_Delete", userID, item_date.getText().toString(), wh_code, loc_code, itm_code, no);

        call.enqueue(new Callback<InvItmScanModel>() {
            @Override
            public void onResponse(Call<InvItmScanModel> call, Response<InvItmScanModel> response) {
                if (response.isSuccessful()) {
                    mInvItmModel = response.body();
                    InvItmScanModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mInvItmList = model.getItems();
                            //mAdapter.notifyDataSetChanged();
                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "삭제되었습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<InvItmScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, mContext.getString(R.string.error_network));
            }
        });
    }//Close




    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<InvItmScanModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<InvItmScanModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<InvItmScanModel.Item> getData() {
            return itemsList;
        }

        public void addData(InvItmScanModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_inv_itm_detail_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final InvItmScanModel.Item item = itemsList.get(position);

            holder.tv_qty.setText(Float.toString(item.getQTY()));
            holder.tv_box.setText(Integer.toString(item.getBox()));
            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTwoBtnPopup = new TwoBtnPopup(mActivity, "삭제제하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                itemsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, itemsList.size());
                                mTwoBtnPopup.hideDialog();
                                ListDelete(item.getSEQ());
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    });
                }
            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_qty;
            TextView tv_box;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                tv_qty = view.findViewById(R.id.tv_qty);
                tv_box = view.findViewById(R.id.tv_box);
                bt_delete = view.findViewById(R.id.bt_delete);


                /*view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what = getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });*/
            }
        }
    }//Close Adapter












}//Close Fragment
