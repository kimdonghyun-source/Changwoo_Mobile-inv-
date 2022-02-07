package kr.co.changwoo.wms.menu.ship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import kr.co.changwoo.wms.common.Define;
import kr.co.changwoo.wms.common.SharedData;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.custom.CommonFragment;
import kr.co.changwoo.wms.honeywell.AidcReader;
import kr.co.changwoo.wms.menu.main.BaseActivity;
import kr.co.changwoo.wms.menu.popup.OneBtnPopup;
import kr.co.changwoo.wms.menu.sin.SinFragment;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.ShipBoxModel;
import kr.co.changwoo.wms.model.ShipDetailModel;
import kr.co.changwoo.wms.model.ShipNoModel;
import kr.co.changwoo.wms.model.ShipScanModel;
import kr.co.changwoo.wms.model.SinListModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShipFragment extends CommonFragment {

    Context mContext;
    List<String> mIncode;
    String barcodeScan, getTime, userID;
    TextView tv_ship_no, tv_cst_name;
    EditText et_box_no, et_barcode;
    RecyclerView Ship_list;
    ImageButton bt_barcode, bt_next;

    ShipScanModel mScanModel;
    List<ShipScanModel.Item> mScanList;
    ListAdapter mAdapter;

    ShipBoxModel mBoxModel;
    List<ShipBoxModel.Item> mBoxList;

    ShipNoModel mShipNoModel;
    List<ShipNoModel.Item> mShipListModel;

    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;
    OneBtnPopup mOneBtnPopup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mIncode = new ArrayList<>();


    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship, container, false);

        tv_ship_no = v.findViewById(R.id.tv_ship_no);
        tv_cst_name = v.findViewById(R.id.tv_cst_name);
        //et_box_no = v.findViewById(R.id.et_box_no);
        et_barcode = v.findViewById(R.id.et_barcode);
        Ship_list = v.findViewById(R.id.Ship_list);
        bt_barcode = v.findViewById(R.id.bt_barcode);
        bt_next = v.findViewById(R.id.bt_next);

        //et_barcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        bt_barcode.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);

        Ship_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        Ship_list.setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        Ship_list.setLayoutManager(mLayoutManager);

        /*et_box_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_box_no.getWindowToken(), 0);
                    if (tv_ship_no.getText().equals("")) {
                        Utils.Toast(mContext, "출하의뢰서를 먼저 스캔해주세요.");
                    } else if (et_box_no.getText().toString().equals("")) {
                        Utils.Toast(mContext, "BOXNO를 입력해주세요.");
                    } else {
                        if (mAdapter.getItemCount() > 0) {
                            mAdapter.clearData();
                            mAdapter.itemsList.clear();
                            mBoxList.clear();
                            mAdapter.notifyDataSetChanged();
                        }

                        BoxReadList();
                    }

                }
                return true;
            }
        });*/

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);

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

                    if (barcodeScan.substring(0, 1).equals("*")) {
                        barcodeScan = barcode.replace("*", "");
                    } else {
                        barcodeScan = barcode;
                    }

                    Log.d("바코드번호", barcodeScan);
                    if (barcodeScan.length() == 12) {

                        ShipList(barcodeScan);

                    } else {
                        if (tv_ship_no.getText().toString().equals("")) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하의뢰서를 스캔해주세요.", R.drawable.popup_title_alert, new Handler() {
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
                            return;
                        }
                        /*if (et_box_no.getText().toString().equals("")) {
                            Utils.Toast(mContext, "BOXNO를 입력해주세요.");
                            sound_pool.play(soundId, 1f, 1f, 0, 1, 1f);
                            mediaPlayer = MediaPlayer.create(mContext, R.raw.beepum);
                            mediaPlayer.start();
                            return;
                        }*/
                        if (barcodeScan.length() == 16) {

                            Ship_itm_req(barcodeScan);
                            //et_barcode.setText(barcodeScan);

                        } else {
                            String str1 = barcodeScan;
                            String word1 = str1.split("    ")[0];
                            String word2 = str1.split("    ")[1];

                            Ship_itm_name(word1, Integer.parseInt(word2));
                            //et_barcode.setText(barcodeScan);
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
                /*case R.id.bt_box_end:
                    if (mAdapter.getItemCount() > 0) {
                        //et_box_no.setFocusable(true);
                        mAdapter.clearData();
                        mAdapter.itemsList.clear();
                        mBoxList.clear();
                        mAdapter.notifyDataSetChanged();
                        //hideSoftInputFromWindow
                        *//*InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et_box_no, 0);*//*
                    } else {
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "내역이 없습니다.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }
                    break;*/

                /*case R.id.bt_list:
                    if (tv_ship_no.getText().equals("")) {
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "출하의뢰서를 먼저 스캔해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    } else {
                        Intent intent = new Intent(getActivity(), BaseActivity.class);
                        intent.putExtra("menu", Define.MENU_SHIP_LIST);
                        Bundle extras = new Bundle();
                        extras.putString("ship_no", tv_ship_no.getText().toString());
                        extras.putString("cst_name", tv_cst_name.getText().toString());
                        extras.putString("ship_date", mShipNoModel.getItems().get(0).getShip_date());
                        extras.putString("ship_seq", mShipNoModel.getItems().get(0).getShip_no());
                        intent.putExtra("args", extras);

                        if (mAdapter.getItemCount() > 0) {
                            mAdapter.clearData();
                            mAdapter.itemsList.clear();
                            mAdapter.notifyDataSetChanged();
                            //et_box_no.setText("");
                        }

                        startActivityForResult(intent, 100);
                    }

                    break;*/

                case R.id.bt_barcode:
                    if (et_barcode.getText().toString().equals("")) {
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
                    if (et_barcode.getText().toString().substring(0, 1).equals("*")) {
                        barcodeScan = et_barcode.getText().toString().replace("*", "");
                    } else {
                        barcodeScan = et_barcode.getText().toString();
                    }

                    if (barcodeScan.length() == 12) {

                        ShipList(barcodeScan);

                    } else {
                        if (tv_ship_no.getText().toString().equals("")) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하의뢰서를 스캔해주세요.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            return;
                        }
                        /*if (et_box_no.getText().toString().equals("")) {
                            Utils.Toast(mContext, "BOXNO를 입력해주세요.");
                            return;
                        }*/
                        if (barcodeScan.length() == 16) {

                            Ship_itm_req(barcodeScan);
                            //et_barcode.setText(barcodeScan);

                        } else {
                            String str1 = barcodeScan;
                            try {
                                String word1 = str1.split("    ")[0];
                                String word2 = str1.split("    ")[1];

                                Ship_itm_name(word1, Integer.parseInt(word2));
                            }catch (Exception e){
                                e.printStackTrace();
                                mOneBtnPopup = new OneBtnPopup(getActivity(), "형식이 올바르지 않습니다.\n문자와 숫자 사이 간격은 4자리입니다.", R.drawable.popup_title_alert, new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == 1) {
                                            mOneBtnPopup.hideDialog();
                                        }
                                    }
                                });
                            }


                            //et_barcode.setText(barcodeScan);
                        }


                    }

                    break;

                case R.id.bt_next:
                    if (mAdapter.getItemCount() <= 0){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "출하등록할 내역이 없습니다.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }

                    for (int i = 0; i < mAdapter.getItemCount(); i ++){
                        if (mBoxList.get(i).getChk_yn().equals("N")){
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "출하등록 불가.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            return;
                        }
                    }

                    requestSinSave();

                    break;
            }

        }
    };//Close onClick

    /**
     * 출하등록
     */
    private void requestSinSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        String emp_code = (String) SharedData.getSharedData(mContext, SharedData.UserValue.EMP_CODE.name(), "");
        Call<ShipDetailModel> call = service.sp_pda_ship_clo("sp_pda_ship_clo", userID, getTime, mShipNoModel.getItems().get(0).getShip_date(), mShipNoModel.getItems().get(0).getShip_no(), emp_code);

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


    /**
     * 출하관리 리스트
     */
    private void ShipList(final String bar) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipNoModel> call = service.sp_pda_ship_scan("sp_pda_ship_scan", userID, getTime, bar);

        call.enqueue(new Callback<ShipNoModel>() {
            @Override
            public void onResponse(Call<ShipNoModel> call, Response<ShipNoModel> response) {
                if (response.isSuccessful()) {
                    mShipNoModel = response.body();
                    final ShipNoModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mShipNoModel != null) {
                        if (mShipNoModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                tv_cst_name.setText(mShipNoModel.getItems().get(0).getCst_name());
                                tv_ship_no.setText(bar);
                                //et_box_no.setText("");
                                if (mAdapter.getItemCount() > 0) {
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    //mScanList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            BoxReadList();
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
            public void onFailure(Call<ShipNoModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 의뢰순번 스캔(16자리)
     */
    private void Ship_itm_req(final String bar) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipScanModel> call = service.sp_pda_itm_req("sp_pda_itm_req", userID, getTime,
                mShipNoModel.getItems().get(0).getShip_date(), mShipNoModel.getItems().get(0).getShip_no(), bar);

        call.enqueue(new Callback<ShipScanModel>() {
            @Override
            public void onResponse(Call<ShipScanModel> call, Response<ShipScanModel> response) {
                if (response.isSuccessful()) {
                    mScanModel = response.body();
                    final ShipScanModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mScanModel != null) {
                        if (mScanModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                et_barcode.setText(mScanModel.getItems().get(0).getItm_name() + "    " + mScanModel.getItems().get(0).getSreq_qty());
                                BoxReadList();

                            }

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            //Utils.Toast(mContext, model.getMSG());
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
            public void onFailure(Call<ShipScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 의뢰순번 스캔(16자리이상 BOX포장지)
     */
    private void Ship_itm_name(final String bar, int qty) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipScanModel> call = service.sp_pda_itm_name("sp_pda_itm_name", userID, getTime,
                mShipNoModel.getItems().get(0).getShip_date(), mShipNoModel.getItems().get(0).getShip_no(), bar, qty);

        call.enqueue(new Callback<ShipScanModel>() {
            @Override
            public void onResponse(Call<ShipScanModel> call, Response<ShipScanModel> response) {
                if (response.isSuccessful()) {
                    mScanModel = response.body();
                    final ShipScanModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mScanModel != null) {
                        if (mScanModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                et_barcode.setText(mScanModel.getItems().get(0).getItm_name() + "    " + mScanModel.getItems().get(0).getSreq_qty());
                                BoxReadList();

                            }

                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            //Utils.Toast(mContext, model.getMSG());
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
            public void onFailure(Call<ShipScanModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * BOXNO 입력시 조회
     */
    private void BoxReadList() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipBoxModel> call = service.sp_pda_ship_box_read("sp_pda_ship_list", userID, getTime,
                mShipNoModel.getItems().get(0).getShip_date(), mShipNoModel.getItems().get(0).getShip_no());

        call.enqueue(new Callback<ShipBoxModel>() {
            @Override
            public void onResponse(Call<ShipBoxModel> call, Response<ShipBoxModel> response) {
                if (response.isSuccessful()) {
                    mBoxModel = response.body();
                    final ShipBoxModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mBoxModel != null) {
                        if (mBoxModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {
                                if (mAdapter.getItemCount() > 0) {
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                                mBoxList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ShipBoxModel.Item item = (ShipBoxModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                    //et_barcode.setText(mBoxModel.getItems().get(i).getItm_name() + "    " + mBoxModel.getItems().get(i).getShip_qty());
                                }
                                mAdapter.notifyDataSetChanged();
                                Ship_list.setAdapter(mAdapter);
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
            public void onFailure(Call<ShipBoxModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 리스트내역 삭제
     */
    private void DetailDelete(int no) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ShipBoxModel> call = service.sp_pda_sreqdel("sp_pda_sreqdel", userID, getTime, no);

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
                            //mAdapter.notifyDataSetChanged();
                        } else {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), model.getMSG(), R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        mOneBtnPopup.hideDialog();
                                    }
                                }
                            });
                            //Utils.Toast(mContext, model.getMSG());
                        }
                    }
                } else {
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ShipBoxModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, mContext.getString(R.string.error_network));
            }
        });
    }//Close


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<ShipBoxModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<ShipBoxModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ShipBoxModel.Item> getData() {
            return itemsList;
        }

        public void addData(ShipBoxModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_ship_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ShipBoxModel.Item item = itemsList.get(position);

            holder.tv_no.setText(Integer.toString(position + 1));
            holder.tv_itm_name.setText(item.getItm_name());
            holder.tv_order_qty.setText(Integer.toString(item.getSreq_qty()));
            holder.tv_out_qty.setText(Integer.toString(item.getShip_qty()));

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

            //특정 데이터시 텍스트 색 변경
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                if (itemsList.get(i).getChk_yn().equals("N")) {
                    if (position == i) {
                        holder.tv_no.setTextColor(Color.RED);
                        holder.tv_itm_name.setTextColor(Color.RED);
                        holder.tv_order_qty.setTextColor(Color.RED);
                        holder.tv_out_qty.setTextColor(Color.RED);
                    }
                }

            }//Close for

        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_no;
            TextView tv_itm_name;
            TextView tv_order_qty;
            TextView tv_out_qty;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                tv_no = view.findViewById(R.id.tv_no);
                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_order_qty = view.findViewById(R.id.tv_order_qty);
                tv_out_qty = view.findViewById(R.id.tv_out_qty);
                bt_delete = view.findViewById(R.id.bt_delete);


            }
        }
    }//Close Adapter


}//Close Fragment
