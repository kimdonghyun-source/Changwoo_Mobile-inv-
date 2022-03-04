package kr.co.changwoo.wms.menu.itm;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import kr.co.changwoo.wms.common.SharedData;
import kr.co.changwoo.wms.common.Utils;
import kr.co.changwoo.wms.custom.CommonFragment;
import kr.co.changwoo.wms.honeywell.AidcReader;
import kr.co.changwoo.wms.menu.popup.OneBtnPopup;
import kr.co.changwoo.wms.menu.popup.TwoBtnPopup;
import kr.co.changwoo.wms.menu.ship.ShipFragment;
import kr.co.changwoo.wms.model.ItmModel;
import kr.co.changwoo.wms.model.ResultModel;
import kr.co.changwoo.wms.model.ShipBoxModel;
import kr.co.changwoo.wms.model.ShipDetailModel;
import kr.co.changwoo.wms.model.ShipScanModel;
import kr.co.changwoo.wms.network.ApiClientService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItmFragment extends CommonFragment {
    Context mContext;
    EditText et_etc, et_scan;
    ImageButton bt_next;
    RecyclerView itm_listview;
    String barcodeScan, getTime, userID;
    private SoundPool sound_pool;
    int soundId;
    MediaPlayer mediaPlayer;
    ItmModel mItmModel;
    List<ItmModel.Item> mItmList;
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
        View v = inflater.inflate(R.layout.frag_itm, container, false);

        itm_listview = v.findViewById(R.id.itm_listview);
        et_etc = v.findViewById(R.id.et_etc);
        et_scan = v.findViewById(R.id.et_scan);
        bt_next = v.findViewById(R.id.bt_next);

        bt_next.setOnClickListener(onClickListener);

        itm_listview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(getActivity());
        itm_listview.setAdapter(mAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        itm_listview.setLayoutManager(mLayoutManager);

        sound_pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sound_pool.load(mContext, R.raw.beepum, 1);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);

        userID = (String) SharedData.getSharedData(mContext, SharedData.UserValue.USER_ID.name(), "");

        ItmReadList(getTime);

        return v;

    }//Close onCreateView


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.bt_next:
                    if (mAdapter.getItemCount() == 0) {
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "품목을 스캔해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }

                    if (et_etc.getText().toString().equals("")){
                        mOneBtnPopup = new OneBtnPopup(getActivity(), "상단의 비고를 입력해주세요.", R.drawable.popup_title_alert, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    mOneBtnPopup.hideDialog();
                                }
                            }
                        });
                        return;
                    }

                    requestSinSave();


                    break;

            }

        }
    };

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

                    /*String str1 = barcodeScan;
                    String word1 = str1.split("    ")[0];
                    String word2 = str1.split("    ")[1];*/

                    try {
                        String str1 = barcode;
                        String word1 = str1.split("    ")[0];
                        String word2 = str1.split("    ")[1];

                        //et_scan.setText(barcode);
                        itm_in(word1, Integer.parseInt(word2));
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
        });

    }//Close onResume

    /**
     * 품목관리 조회
     */
    private void ItmReadList(String date) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ItmModel> call = service.sp_pda_list_in("sp_pda_list_in", userID, date);

        call.enqueue(new Callback<ItmModel>() {
            @Override
            public void onResponse(Call<ItmModel> call, Response<ItmModel> response) {
                if (response.isSuccessful()) {
                    mItmModel = response.body();
                    final ItmModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mItmModel != null) {
                        if (mItmModel.getFlag() == ResultModel.SUCCESS) {
                            if (model.getItems().size() > 0) {
                                if (mAdapter.getItemCount() > 0) {
                                    mAdapter.clearData();
                                    mAdapter.itemsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                                mItmList = model.getItems();
                                for (int i = 0; i < model.getItems().size(); i++) {
                                    ItmModel.Item item = (ItmModel.Item) model.getItems().get(i);
                                    mAdapter.addData(item);
                                    //et_barcode.setText(mBoxModel.getItems().get(i).getItm_name() + "    " + mBoxModel.getItems().get(i).getShip_qty());
                                }
                                mAdapter.notifyDataSetChanged();
                                itm_listview.setAdapter(mAdapter);
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
            public void onFailure(Call<ItmModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 의뢰품명 스캔
     */
    private void itm_in(final String bar, int qty) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ItmModel> call = service.sp_pda_itm_in("sp_pda_itm_in", userID, getTime, bar, qty);

        call.enqueue(new Callback<ItmModel>() {
            @Override
            public void onResponse(Call<ItmModel> call, Response<ItmModel> response) {
                if (response.isSuccessful()) {
                    mItmModel = response.body();
                    final ItmModel model = response.body();
                    Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (mItmModel != null) {
                        if (mItmModel.getFlag() == ResultModel.SUCCESS) {

                            if (model.getItems().size() > 0) {
                                et_scan.setText(mItmModel.getItems().get(0).getItm_name() + "    " + mItmModel.getItems().get(0).getIn_qty());
                                ItmReadList(getTime);
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
            public void onFailure(Call<ItmModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close


    /**
     * 리스트내역 삭제
     */
    private void itm_del_in(int no) {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ItmModel> call = service.sp_pda_itm_del_in("sp_pda_del_in", userID, getTime, no);

        call.enqueue(new Callback<ItmModel>() {
            @Override
            public void onResponse(Call<ItmModel> call, Response<ItmModel> response) {
                if (response.isSuccessful()) {
                    mItmModel = response.body();
                    ItmModel model = response.body();
                    //Utils.Log("model ==> :" + new Gson().toJson(model));
                    if (model != null) {
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mItmList = model.getItems();
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
            public void onFailure(Call<ItmModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, mContext.getString(R.string.error_network));
            }
        });
    }//Close

    /**
     * 출하등록
     */
    private void requestSinSave() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        Call<ItmModel> call = service.sp_pda_clo_in("sp_pda_clo_in", userID, getTime, et_etc.getText().toString());

        call.enqueue(new Callback<ItmModel>() {
            @Override
            public void onResponse(Call<ItmModel> call, Response<ItmModel> response) {
                if (response.isSuccessful()) {
                    ItmModel model = response.body();
                    if (model != null) {
                        //mShipScanModel.getFlag().equals("OK")
                        if (model.getFlag() == ResultModel.SUCCESS) {
                            mOneBtnPopup = new OneBtnPopup(getActivity(), "'" + et_etc.getText().toString() + "'의 입력이 마감 되었습니다.",
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
            public void onFailure(Call<ItmModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }//Close 제품출하등록


    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        List<ItmModel.Item> itemsList;
        Activity mActivity;
        Handler mHandler = null;

        public ListAdapter(Activity context) {
            mActivity = context;
        }

        public void setData(List<ItmModel.Item> list) {
            itemsList = list;
        }

        public void clearData() {
            if (itemsList != null) itemsList.clear();
        }

        public void setRetHandler(Handler h) {
            this.mHandler = h;
        }

        public List<ItmModel.Item> getData() {
            return itemsList;
        }

        public void addData(ItmModel.Item item) {
            if (itemsList == null) itemsList = new ArrayList<>();
            itemsList.add(item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int z) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_itm, viewGroup, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ItmModel.Item item = itemsList.get(position);

            holder.tv_no.setText(Integer.toString(position + 1));
            holder.tv_itm_name.setText(item.getItm_name());
            holder.tv_qty.setText(Integer.toString(item.getIn_qty()));

            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemsList.size());
                    mAdapter.notifyDataSetChanged();
                    itm_del_in(item.getPda_seq());
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null == itemsList ? 0 : itemsList.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_no;
            TextView tv_itm_name;
            TextView tv_qty;
            ImageButton bt_delete;

            public ViewHolder(View view) {
                super(view);

                tv_no = view.findViewById(R.id.tv_no);
                tv_itm_name = view.findViewById(R.id.tv_itm_name);
                tv_qty = view.findViewById(R.id.tv_qty);
                bt_delete = view.findViewById(R.id.bt_delete);
            }
        }
    }//Close Adapter


}//Close Fragment
