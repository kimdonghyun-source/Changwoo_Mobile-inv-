package kr.co.ajcc.wms.menu.registration;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.custom.MergeAdapter;
import kr.co.ajcc.wms.menu.popup.LocationListPopup;
import kr.co.ajcc.wms.menu.popup.OneBtnPopup;
import kr.co.ajcc.wms.spinner.SpinnerAdapter;
import kr.co.ajcc.wms.model.RegistrationModel;

/**
 * 입고등록
 */

public class RegistrationFragment extends CommonFragment {
    Context mContext;

    LocationListPopup mLocationListPopup;
    OneBtnPopup mOneBtnPopup;

    Spinner mSpinner;
    int mSpinnerSelect = 0;

    MergeAdapter mMergeAdapter;

    EditText et_location;

    ListView mListView;
    //리스트가 없을때 보여지는 text
    TextView tv_empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_registration, container, false);

        v.findViewById(R.id.bt_change).setOnClickListener(onClickListener);
        v.findViewById(R.id.bt_search).setOnClickListener(onClickListener);
        v.findViewById(R.id.bt_next).setOnClickListener(onClickListener);

        List<String> list = new ArrayList<String>();
        list.add("창고 1");
        list.add("창고 2");
        list.add("창고 3");
        list.add("창고 4");
        list.add("창고 5");

        mSpinner =  v.findViewById(R.id.spinner);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, list, mSpinner);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(onItemSelectedListener);
        mSpinner.setSelection(mSpinnerSelect);

        et_location = v.findViewById(R.id.et_location);

        tv_empty = v.findViewById(R.id.tv_empty);
        mListView = v.findViewById(R.id.listview);
        mMergeAdapter = new MergeAdapter();

        return v;
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //최초에 setOnItemSelectedListener 하면 이벤트가 들어오기 때문에
            //onResume에서 mSpinnerSelect에 현재 선택된 position을 넣고 여기서 비교
            if(mSpinnerSelect == position)return;

            mSpinnerSelect = position;

            String item = (String) mSpinner.getSelectedItem();
            Utils.Toast(mContext, item+" 선택");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_change:
                    break;
                case R.id.bt_search:
                    ArrayList<String> list = new ArrayList<>();
                    list.add("로케이션 1");
                    list.add("로케이션 2");
                    list.add("로케이션 3");
                    list.add("로케이션 4");
                    list.add("로케이션 5");
                    mLocationListPopup = new LocationListPopup(getActivity(), list, R.drawable.popup_title_searchloc, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                String result = (String)msg.obj;
                                et_location.setText(result);
                                mLocationListPopup.hideDialog();

                                for(int i = 0; i < 3; i++) {
                                    RegistrationModel model = new RegistrationModel();
                                    model.setProduct("품명 "+(i+1));
                                    model.setStandard("규격 "+(i+1));
                                    model.setCount(1000-(i*100));

                                    RegistrationView view = new RegistrationView(getActivity());
                                    view.setData(model);

                                    mMergeAdapter.addView(view);
                                }

                                mListView.setAdapter(mMergeAdapter);
                                mMergeAdapter.notifyDataSetChanged();

                                tv_empty.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    break;
                case R.id.bt_next:
                    mOneBtnPopup = new OneBtnPopup(getActivity(), "품질 검사 완료 후 입고등록을 하십시오.", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                String result = (String)msg.obj;
                                Utils.Toast(mContext, result);
                                mOneBtnPopup.hideDialog();
                            }
                        }
                    });
                    break;
            }
        }
    };
}