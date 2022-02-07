package kr.co.changwoo.wms.menu.sin;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import kr.co.changwoo.wms.menu.popup.EtcTextPopup;
import kr.co.changwoo.wms.menu.popup.LocationSinCstList;
import kr.co.changwoo.wms.menu.popup.OneBtnPopup;
import kr.co.changwoo.wms.menu.popup.TwoBtnPopup;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.SinCstModel;
import kr.co.changwoo.wms.model.SinListModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SinFragment extends CommonFragment {

    Context mContext;
    List<String> mIncode;
    String barcodeScan, beg_barcode, cst_code = null, cst_name = null, getTime, userID;
    EditText et_cst, et_scan;
    ImageButton bt_cst, bt_next, bt_barcode;
    RecyclerView sin_listview;

    SinCstModel.Item mCstModel;
    List<SinCstModel.Item> mCstList;
    LocationSinCstList mLocationCstPopup;

    SinListModel mSinModel;
    List<SinListModel.Item> mSinList;

    ListAdapter mAdapter;

    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;
    EtcTextPopup mEtcTextPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();


    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_sin, container, false);

        et_cst = v.findViewById(R.id.et_cst);
        bt_cst = v.findViewById(R.id.bt_cst);
        et_scan = v.findViewById(R.id.et_scan);
        sin_listview = v.findViewById(R.id.sin_listview);
        bt_next = v.findViewById(R.id.bt_next);
        bt_barcode = v.findViewById(R.id.bt_barcode);


        et_cst.setOnClickListener(onClickListener);
        bt_cst.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);
        bt_barcode.setOnClickListener(onClickListener);

        sin_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        sin_listview.setAdapter(mAdapter);

        requestCreateCstlist();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);

        ItmListSearch();

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

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

                    if (cst_code == null){
                        Utils.Toast(mContext, "거래처를 선택해주세요.");
                        return;
                    }else{

                        try {
                            String str1 = barcode;
                            String word1 = str1.split("    ")[0];
                            String word2 = str1.split("    ")[1];

                            et_scan.setText(barcode);
                            OsrList(word1, Integer.parseInt(word2));
                        }catch (Exception e){
                            e.printStackTrace();
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "형식이 올바르지 않습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            //Utils.Toast(mContext, "형식이 올바르지 않습니다.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                        }


                    }



                }
            }
        });

    }//Close onResume

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.et_cst:
                    requestCstlist();
                    break;

                case R.id.bt_cst:
                    requestCstlist();
                    break;

                case R.id.bt_next:
                   //requestSinSave();
                    if (mAdapter.getItemCount() == 0){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "입고 등록할 품목을 스캔해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }
                    mEtcTextPopup = new EtcTextPopup(getActivity(), R.drawable.popup_title_etc_text, getTime, cst_code, cst_name, new Handler() {
                        @Override
                        public void handleMessage(Message msg1) {
                            getActivity().finish();
                                /*mAdapter.clearData();
                                mAdapter.notifyDataSetChanged();
                                et_scan.setText("");*/

                        }
                    });
                    break;

                case R.id.bt_barcode:

                    if (et_scan.getText().toString().equals("")){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "바코드 번호를 입력해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }
                    barcodeScan = et_scan.getText().toString();

                    if (cst_code == null){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "거래처를 선택해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }else{
                        try {
                            String str1 = barcodeScan;
                            String word1 = str1.split("    ")[0];
                            String word2 = str1.split("    ")[1];

                            et_scan.setText(barcodeScan);
                            OsrList(word1, Integer.parseInt(word2));

                        }catch (Exception e){
                            e.printStackTrace();
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "형식이 올바르지 않습니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });

                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                        }

                    }

                    break;
            }

        }
    };//Close onClick


    /**
     * 거래처리스트
     */
    private void requestCstlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Call<SinCstModel> call = service.sp_pda_cst_code("sp_pda_cst_list", "");

        call.enqueue(new Callback<SinCstModel>() {
            @Override
            public void onResponse(Call<SinCstModel> call, Response<SinCstModel> response) {
                if (response.isSuccessful()) {
                    SinCstModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mLocationCstPopup = new LocationSinCstList(getActivity(), model.getItems(), R.drawable.popup_title_whlist, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    SinCstModel.Item item = (SinCstModel.Item) msg.obj;
                                    mCstModel = item;
                                    et_cst.setText("[" + mCstModel.getCst_code() + "] " + mCstModel.getCst_name());
                                    //mAdapter.notifyDataSetChanged();
                                    cst_code = mCstModel.getCst_code();
                                    cst_name = mCstModel.getCst_name();
                                    mLocationCstPopup.hideDialog();


                                }
                            });
                            mCstList = model.getItems();


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
            public void onFailure(Call<SinCstModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 거래처리스트(화면호출시)
     */
    private void requestCreateCstlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Call<SinCstModel> call = service.sp_pda_cst_code("sp_pda_cst_list", "");

        call.enqueue(new Callback<SinCstModel>() {
            @Override
            public void onResponse(Call<SinCstModel> call, Response<SinCstModel> response) {
                if (response.isSuccessful()) {
                    SinCstModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            et_cst.setText("[" + model.getItems().get(0).getCst_code() + "] " + model.getItems().get(0).getCst_name());
                            //mAdapter.notifyDataSetChanged();
                            cst_code = model.getItems().get(0).getCst_code();
                            cst_name = model.getItems().get(0).getCst_name();
                            mCstList = model.getItems();

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
            public void onFailure(Call<SinCstModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 입고관리 리스트
     */
    private void OsrList(final String bar, int qty) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<SinListModel> call = service.sp_pda_itm_chk("sp_pda_itmchk", userID, getTime, bar, qty );

        call.enqueue(new Callback<SinListModel>() {
            @Override
            public void onResponse(Call<SinListModel> call, Response<SinListModel> response) {
                if (response.isSuccessful()) {
                    mSinModel = response.body();
                    final SinListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mSinModel != null) {
                        if (mSinModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                if (mAdapter.getItemCount() > 0) {
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mSinList.clear();
                                }
                                ItmListSearch();
                            }

                        } else {
                            Utils.Toast(mContext, model.getMSG());
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();

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
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 입고내역 조회
     */
    private void ItmListSearch() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<SinListModel> call = service.sp_pda_itm_search("sp_pda_itmList", userID, getTime);

        call.enqueue(new Callback<SinListModel>() {
            @Override
            public void onResponse(Call<SinListModel> call, Response<SinListModel> response) {
                if (response.isSuccessful()) {
                    mSinModel = response.body();
                    final SinListModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mSinModel != null) {
                        if (mSinModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {

                                mSinList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    SinListModel.Item item = (SinListModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                }
                                mAdapter.notifyDataSetChanged();
                                sin_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<SinListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 입고마감
     */
    /*private void requestSinSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        String userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");
        Call<SinListModel> call = service.sp_pda_sin_clo("sp_pda_sin_clo", userID, getTime, cst_code, userID, "");

        call.enqueue(new Callback<SinListModel>() {
            @Override
            public void onResponse(Call<SinListModel> call, Response<SinListModel> response) {
                if (response.isSuccessful()) {
                    SinListModel model = response.body();
                    if (model != null) {
                        //mShipScanModel.getFlag().equals("OK")
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "'"+getTime+"'일 "+ cst_name + "입고 등록이 완료 되었습니다.",
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
            public void onFailure(Call<SinListModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close 입고마감*/

    /**
     * 리스트내역 삭제
     */
    private void DetailDelete(int no) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<SinListModel> call = service.sp_pda_itm_del("sp_pda_itmDel", userID, getTime, no);

        call.enqueue(new Callback<SinListModel>() {
            @Override
            public void onResponse(Call<SinListModel> call, Response<SinListModel> response) {
                if (response.isSuccessful()) {
                    mSinModel = response.body();
                    SinListModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mSinList = model.getItems();
                            //mAdapter.notifyDataSetChanged();
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
                Utils.Toast(mContext, mContext.getString(R.string.error_network));
            }
        });
    }//Close

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<SinListModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<SinListModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<SinListModel.Item> getData() {
            return itemsList;
        }

        public void addData(SinListModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_sin_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final SinListModel.Item item = itemsList.get(position);

            holder.tv_itm_name.setText(item.getItm_name());
            holder.tv_in_qty.setText(Integer.toString(item.getIn_qty()));
            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemsList.size());
                    mAdapter.notifyDataSetChanged();
                    DetailDelete(item.getPda_seq());
                }
            });


        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_itm_name;
            TextView tv_in_qty;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_in_qty = view.findViewById(R.id.tv_in_qty);
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
