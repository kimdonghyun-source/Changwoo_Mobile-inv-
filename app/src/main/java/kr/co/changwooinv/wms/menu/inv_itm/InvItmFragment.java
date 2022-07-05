package kr.co.changwooinv.wms.menu.inv_itm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.honeywell.aidc.BarcodeReadEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.changwooinv.wms.GlobalApplication;
import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.common.Define;
import kr.co.changwooinv.wms.common.SharedData;
import kr.co.changwooinv.wms.common.Utils;
import kr.co.changwooinv.wms.custom.CommonFragment;
import kr.co.changwooinv.wms.honeywell.AidcReader;
import kr.co.changwooinv.wms.menu.main.BaseActivity;
import kr.co.changwooinv.wms.menu.main.MainActivity;
import kr.co.changwooinv.wms.menu.popup.OneBtnPopup;
import kr.co.changwooinv.wms.menu.popup.TwoBtnPopup;
import kr.co.changwooinv.wms.menu.popup.WareHouseSearchPopup;
import kr.co.changwooinv.wms.model.InvItmScanModel;
import kr.co.changwooinv.wms.model.ResultModel;
import kr.co.changwooinv.wms.model.UserInfoModel;
import kr.co.changwooinv.wms.model.WareHouseModel;
import kr.co.changwooinv.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
* Create date: 2022.07.04
* Description: 입고관리
*
* */

public class InvItmFragment extends CommonFragment {
    Context mContext;
    String barcodeScan, wh_code, wh_name, loc_code, loc_name, userID, m_date;
    TextView item_date, tv_wh, tv_loc, tv_serial;
    RecyclerView inv_itm_listView;
    ImageButton btn_next;

    WareHouseSearchPopup mWareHouseSearchPopup;
    OneBtnPopup mOneBtnPopup;
    TwoBtnPopup mTwoBtnPopup;

    DatePickerDialog.OnDateSetListener callbackMethod;
    SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;

    WareHouseModel.Item mWareHouseModel;
    List<WareHouseModel.Item> mWareHouseList;

    InvItmScanModel mInvItmModel;
    List<InvItmScanModel.Item> mInvItmList;

    ListAdapter mAdapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_inv_itm, container, false);

        item_date = v.findViewById(R.id.item_date);
        tv_wh = v.findViewById(R.id.tv_wh);
        tv_loc = v.findViewById(R.id.tv_loc);
        tv_serial = v.findViewById(R.id.tv_serial);
        inv_itm_listView = v.findViewById(R.id.inv_itm_listView);
        btn_next = v.findViewById(R.id.btn_next);

        item_date.setOnClickListener(onClickListener);
        tv_wh.setOnClickListener(onClickListener);
        tv_loc.setOnClickListener(onClickListener);
        btn_next.setOnClickListener(onClickListener);

        inv_itm_listView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        inv_itm_listView.setAdapter(mAdapter);

        int year1 = Integer.parseInt(yearFormat.format(currentTime));
        int month1 = Integer.parseInt(monthFormat.format(currentTime));
        int day1 = Integer.parseInt(dayFormat.format(currentTime));

        String formattedMonth = "" + month1;
        String formattedDayOfMonth = "" + day1;
        if (month1 < 10) {

            formattedMonth = "0" + month1;
        }
        if (day1 < 10) {
            formattedDayOfMonth = "0" + day1;
        }

        item_date.setText(year1 + "-" + formattedMonth + "-" + formattedDayOfMonth);

        this.InitializeListener();

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

        userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        mAdapter.setRetHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what >= 0) {
                    InvItmDetailGo(msg.what);
                }
            }
        });

        return v;

    }//Close onCreateView

    @Override
    public void onResume() {
        super.onResume();

        if (wh_code != null && loc_code != null){
            InvItmList(wh_code, tv_loc.getText().toString());
        }

        AidcReader.getInstance().claim(mContext);
        AidcReader.getInstance().setListenerHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {

                    BarcodeReadEvent event = (BarcodeReadEvent) msg.obj;
                    String barcode = event.getBarcodeData();
                    barcodeScan = barcode;

                    if (tv_wh.getText().toString().equals("")){
                        Utils.Toast(mContext, "창고를 선택해 주세요.");
                        return;
                    }

                    if (tv_loc.getText().toString().equals("")){
                        Utils.Toast(mContext, "위치를 선택해 주세요.");
                        return;
                    }

                    try {
                        String str1 = barcode;
                        String word1 = str1.split("    ")[0];
                        String word2 = str1.split("    ")[1];

                        tv_serial.setText(barcode);
                        ItmListInsert(word1, Integer.parseInt(word2));

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
            }
        });

    }//Close onResume

    private void InvItmDetailGo(int position) {
        List<InvItmScanModel.Item> datas = new ArrayList<>();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra("menu", Define.MENU_INV_ITM_DETAIL);

        Bundle extras = new Bundle();
        extras.putSerializable("model", mInvItmModel);
        extras.putSerializable("position", position);
        extras.putString("wh_code", wh_code);
        extras.putString("loc_code", loc_code);
        intent.putExtra("args", extras);
        startActivityForResult(intent, 100);

    }//Close


    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

                int month = monthOfYear + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + dayOfMonth;

                if (month < 10) {

                    formattedMonth = "0" + month;
                }
                if (dayOfMonth < 10) {

                    formattedDayOfMonth = "0" + dayOfMonth;
                }

                item_date.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);

            }
        };
    }

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_date:
                    int c_year = Integer.parseInt(item_date.getText().toString().substring(0, 4));
                    int c_month = Integer.parseInt(item_date.getText().toString().substring(5, 7));
                    int c_day = Integer.parseInt(item_date.getText().toString().substring(8, 10));
                    DatePickerDialog dialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, callbackMethod, c_year, c_month - 1, c_day);
                    dialog.show();
                    break;

                case R.id.tv_wh:
                    requestWhlist();
                    break;

                case R.id.tv_loc:
                    if (tv_wh.getText().equals("")){
                        Utils.Toast(mContext, "창고를 선택해 주세요.");
                        return;
                    }else{
                        requestLoclist();
                    }
                    break;

                case R.id.btn_next:
                    if (mAdapter.itemsList != null){
                        mTwoBtnPopup = new TwoBtnPopup(getActivity(), "입고 등록하시겠습니까?", R.drawable.popup_title_alert, new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                if (msg.what == 1){
                                    InvItmCloInsert();
                                    mTwoBtnPopup.hideDialog();
                                }
                            }
                        });

                    }else{
                        Utils.Toast(mContext, "데이터를 조회해주세요.");
                    }

                    break;


            }
        }
    };//Close onClick



    /**
     * 창고리스트
     */
    private void requestWhlist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Call<WareHouseModel> call = service.sp_pda_WareLoc_Search("sp_pda_WareHouse_Search", "WH", "");

        call.enqueue(new Callback<WareHouseModel>() {
            @Override
            public void onResponse(Call<WareHouseModel> call, Response<WareHouseModel> response) {
                if (response.isSuccessful()) {
                    WareHouseModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mWareHouseSearchPopup = new WareHouseSearchPopup(getActivity(), model.getItems(), R.drawable.popup_title_wh_text, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    WareHouseModel.Item item = (WareHouseModel.Item) msg.obj;
                                    mWareHouseModel = item;
                                    tv_wh.setText(mWareHouseModel.getName());
                                    //mAdapter.notifyDataSetChanged();
                                    wh_code = mWareHouseModel.getCode();
                                    wh_name = mWareHouseModel.getName();
                                    mWareHouseSearchPopup.hideDialog();


                                }
                            });
                            mWareHouseList = model.getItems();


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
            public void onFailure(Call<WareHouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close requestWhlist


    /**
     * 위치리스트
     */
    private void requestLoclist() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);
        Call<WareHouseModel> call = service.sp_pda_WareLoc_Search("sp_pda_WareHouse_Search", "LC", wh_code);

        call.enqueue(new Callback<WareHouseModel>() {
            @Override
            public void onResponse(Call<WareHouseModel> call, Response<WareHouseModel> response) {
                if (response.isSuccessful()) {
                    WareHouseModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mWareHouseSearchPopup = new WareHouseSearchPopup(getActivity(), model.getItems(), R.drawable.popup_title_loc_text, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    WareHouseModel.Item item = (WareHouseModel.Item) msg.obj;
                                    mWareHouseModel = item;
                                    tv_loc.setText(mWareHouseModel.getName());
                                    //mAdapter.notifyDataSetChanged();
                                    loc_code = mWareHouseModel.getCode();
                                    loc_name = mWareHouseModel.getName();
                                    mWareHouseSearchPopup.hideDialog();
                                    InvItmList(wh_code, loc_code);

                                }
                            });
                            mWareHouseList = model.getItems();


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
            public void onFailure(Call<WareHouseModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close requestLoclist

    /**
     * 입고 품목스캔(insert)
     */
    private void ItmListInsert(String bar, int qty) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        m_date = item_date.getText().toString().replace("-", "");

        Call<InvItmScanModel> call = service.InvItmInsert("sp_pda_inItmChk_Insert", userID, m_date, wh_code, loc_code, bar, qty);

        call.enqueue(new Callback<InvItmScanModel>() {
            @Override
            public void onResponse(Call<InvItmScanModel> call, Response<InvItmScanModel> response) {
                if (response.isSuccessful()) {
                    mInvItmModel = response.body();
                    final InvItmScanModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mInvItmModel != null) {
                        if (mInvItmModel.getFlag() == ResultModel.SUCCESS) {
                            InvItmList(wh_code, loc_code);
                            /*if (model.getItems().size() > 0) {
                            }*/

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
            public void onFailure(Call<InvItmScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close ItmListInsert


    /**
     * 입고관리 리스트
     */
    private void InvItmList(String wh, String loc) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        m_date = item_date.getText().toString().replace("-", "");

        Call<InvItmScanModel> call = service.InvItmList("sp_pda_inItmSum_List", userID, m_date, wh, loc, "A", "" );

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

                        } else {
                            if (mAdapter.itemsList != null){
                                mAdapter.clearData();
                                mAdapter.itemsList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
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
            public void onFailure(Call<InvItmScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close InvItmList

    /**
     * 입고등록
     */
    private void InvItmCloInsert() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        m_date = item_date.getText().toString().replace("-", "");

        Call<ResultModel> call = service.InvItmCloInsert("sp_pda_in_clo_Insert", userID, m_date, wh_code, loc_code);

        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if(response.isSuccessful()){
                    ResultModel model = response.body();
                    if (model != null) {
                        //mShipScanModel.getFlag().equals("OK")
                        if(model.getFlag() == ResultModel.SUCCESS) {
                           //Utils.Toast(mContext, model.getMSG());
                           mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler(){
                               @Override
                               public void handleMessage(@NonNull Message msg) {
                                   if (msg.what == 1){
                                       mOneBtnPopup.hideDialog();
                                       getActivity().finish();
                                   }
                               }
                           });

                        }else{
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }


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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_inv_itm_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final InvItmScanModel.Item item = itemsList.get(position);

            holder.tv_no.setText(Integer.toString(position+1));
            holder.tv_itm_name.setText(item.getItm_Code());
            holder.tv_qty.setText(Float.toString(item.getQTY()));
            holder.tv_box.setText(Integer.toString(item.getBox()));

        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_no;
            TextView tv_itm_name;
            TextView tv_qty;
            TextView tv_box;

            public ViewHolder(View view) {
                super(view);

                tv_no = view.findViewById(R.id.tv_no);
                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_qty = view.findViewById(R.id.tv_qty);
                tv_box = view.findViewById(R.id.tv_box);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = new Message();
                        msg.obj = itemsList.get(getAdapterPosition());
                        msg.what = getAdapterPosition();
                        mHandler.sendMessage(msg);
                    }
                });
            }
        }
    }//Close Adapter





}//Close Fragment
