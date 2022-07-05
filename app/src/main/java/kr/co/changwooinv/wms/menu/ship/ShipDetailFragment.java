package kr.co.changwooinv.wms.menu.ship;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.honeywell.aidc.BarcodeReadEvent;

import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.custom.CommonFragment;
import kr.co.changwooinv.wms.honeywell.AidcReader;

/** Create date: 2022.07.04
 * Description: 출고관리 디테일
 *
 * */

public class ShipDetailFragment extends CommonFragment {

    Context mContext;
    String barcodeScan;
    TextView item_date, tv_wh, tv_loc, tv_itm_qty;
    RecyclerView ship_listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_ship_detail, container, false);

        item_date = v.findViewById(R.id.item_date);
        tv_wh = v.findViewById(R.id.tv_wh);
        tv_loc = v.findViewById(R.id.tv_loc);
        tv_itm_qty = v.findViewById(R.id.tv_itm_qty);
        ship_listView = v.findViewById(R.id.ship_listView);

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

}//Close Fragment
