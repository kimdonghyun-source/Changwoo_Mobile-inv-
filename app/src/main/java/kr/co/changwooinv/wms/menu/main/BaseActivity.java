package kr.co.changwooinv.wms.menu.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import kr.co.changwooinv.wms.GlobalApplication;

import kr.co.changwooinv.wms.R;
import kr.co.changwooinv.wms.common.Define;
import kr.co.changwooinv.wms.custom.BusProvider;
import kr.co.changwooinv.wms.custom.CommonCompatActivity;
import kr.co.changwooinv.wms.custom.CommonFragment;
import kr.co.changwooinv.wms.menu.inv_itm.InvItmFragment;
import kr.co.changwooinv.wms.menu.inv_lot.InvLotFragment;
import kr.co.changwooinv.wms.menu.inv_ok.InvOkFragment;
import kr.co.changwooinv.wms.menu.move.MoveFragment;
import kr.co.changwooinv.wms.menu.popup.TwoBtnPopup;
import kr.co.changwooinv.wms.menu.ship.ShipFragment;
import kr.co.changwooinv.wms.model.UserInfoModel;


public class BaseActivity extends CommonCompatActivity {
    Context mContext;

    //사이드 메뉴 레이아웃
    DrawerLayout drawer;
    View drawer_layout;
    //사이드 메뉴 리스트
    //DrawerMenu mDrawerMenu;
    //좌측 메뉴 리스트 아답터
    ListAdapter mAdapter;
    //다른 메뉴 이동시 묻는 팝업
    TwoBtnPopup mTwoBtnPopup;
    //메뉴 타이틀
    ImageView iv_title;
    //GNB 배경 이미지(피킹은 햄버거버튼 사용 안하기 때문)
    ImageView iv_gnb;
    ImageButton bt_drawer;
    ImageButton bt_print;
    //선택된 메뉴 postion
    int mSelectMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        mContext = BaseActivity.this;

        findViewById(R.id.bt_back).setOnClickListener(onClickListener);

        drawer = findViewById(R.id.drawer);
        drawer_layout = findViewById(R.id.drawer_layout);
        bt_drawer = findViewById(R.id.bt_drawer);
        bt_drawer.setOnClickListener(onClickListener);
        bt_print = findViewById(R.id.bt_print);
        bt_print.setOnClickListener(onClickListener);
        findViewById(R.id.bt_close).setOnClickListener(onClickListener);

        ArrayList<String> list = new ArrayList<>();
        list.add("재고확인");
        list.add("입고관리");
        list.add("출고관리");
        list.add("이동관리");
        list.add("재고실사");


        ListView listView = findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mAdapter.setData(list);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        GlobalApplication application = (GlobalApplication) getApplicationContext();
        UserInfoModel.Items model = application.getUserInfoModel();

        TextView tv_name = findViewById(R.id.tv_name);
//        tv_name.setText(model.getDpt_name()+" "+model.getEmp_name());

        iv_title = findViewById(R.id.iv_title);
        iv_gnb = findViewById(R.id.iv_gnb);

        int menu = getIntent().getIntExtra("menu", 0);

        Bundle args = getIntent().getBundleExtra("args");

        mSelectMenu = menu - 2;

        switch (menu) {


            //재고확인
            case Define.MENU_INV_OK: {
                CommonFragment fragment = new InvOkFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_INV_OK, R.id.fl_content);
                break;
            }

            //입고관리
            case Define.MENU_INV_ITM: {
                CommonFragment fragment = new InvItmFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_INV_ITM, R.id.fl_content);
                break;
            }

            //출고관리
            case Define.MENU_SHIP: {
                CommonFragment fragment = new ShipFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                break;
            }

            //이동관리
            case Define.MENU_MOVE: {
                CommonFragment fragment = new MoveFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_MOVE, R.id.fl_content);
                break;
            }

            //재고실사
            case Define.MENU_INV_LOT: {
                CommonFragment fragment = new InvLotFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_INV_LOT, R.id.fl_content);
                break;
            }


        }

        setTitleImage(menu);
    }

    private void setTitleImage(int menu) {
        //메뉴별 타이틀 이미지
        int image = 0;
        //자재불출과 제품출고에 있는 피킹 상단엔 좌측메뉴가 노출되지 않기 때문에 분기해야함
        int gnb = R.drawable.titilbar;
        int isDrawer = View.VISIBLE;
        int isLock = DrawerLayout.LOCK_MODE_UNLOCKED;

        //프린터 화면에서만 노출하면 되기 때문에 gone 처리 후 프린터 화면 진입 시 visible
        bt_print.setVisibility(View.GONE);

        switch (menu) {

            //재고확인
            case Define.MENU_INV_OK: {
                image = R.drawable.changwoo_title1;
                break;
            }

            //입고관리
            case Define.MENU_INV_ITM: {
                image = R.drawable.changwoo_title2;
                break;
            }

            //출고관리
            case Define.MENU_SHIP: {
                image = R.drawable.changwoo_title3;
                break;
            }

            //이동관리
            case Define.MENU_MOVE: {
                image = R.drawable.changwoo_title4;
                break;
            }

            //재고실사
            case Define.MENU_INV_LOT: {
                image = R.drawable.changwoo_title5;
                break;
            }



        }

        iv_title.setBackgroundResource(image);
        iv_gnb.setBackgroundResource(gnb);
        bt_drawer.setVisibility(isDrawer);
        drawer.setDrawerLockMode(isLock);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_back:
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                mTwoBtnPopup.hideDialog();
                                finish();
                            }
                        }
                    });
                    //backPressed();
                    break;
                case R.id.bt_drawer:
                    drawer.openDrawer(drawer_layout);
                    break;
                case R.id.bt_close:
                    drawer.closeDrawers();
                    break;
                case R.id.bt_print:
                    BusProvider.getInstance().post(0);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, "작업 내용이 취소됩니다.\n 뒤로가시겠습니까?", R.drawable.popup_title_alert, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mTwoBtnPopup.hideDialog();
                    finish();
                }
            }
        });
    }

    public void backPressed() {
        try {
            //사이드 메뉴가 열려있으면 닫아준다.
            if (drawer.isDrawerOpen(drawer_layout)) {
                drawer.closeDrawers();
                return;
            }

            FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
            int count = mFragmentManager.getBackStackEntryCount();

            if (count >= 1) {
                mFragmentManager.popBackStack();
            } else {
                //finish();
            }
        } catch (Exception e) {
            //finish();
        }
    }

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ArrayList<String> mList;

        public void setData(ArrayList<String> list) {
            mList = list;
        }

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }

            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_drawer, null);
                v.setTag(holder);

                holder.tv_menu = v.findViewById(R.id.tv_menu);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (mSelectMenu == position) {
                holder.tv_menu.setSelected(true);
            } else {
                holder.tv_menu.setSelected(false);
            }

            final String menu = mList.get(position);
            holder.tv_menu.setText((position + 1) + ". " + menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectMenu == position) {
                        drawer.closeDrawer(drawer_layout);
                        return;
                    }
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, menu + " 메뉴로 이동하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                //팝업 닫기
                                mTwoBtnPopup.hideDialog();
                                //좌측메뉴 닫기
                                drawer.closeDrawer(drawer_layout);
                                //선택한 메뉴 기억
                                mSelectMenu = position;
                                //0이 없고 입고등록이 삭제되어 +2 해줘야함
                                switch (position + 2) {

                                    //재고확인
                                    case Define.MENU_INV_OK: {
                                        CommonFragment fragment = new InvOkFragment();
                                        replaceContent(fragment, Define.TAG_INV_OK, R.id.fl_content);
                                        break;
                                    }

                                    //입고관리
                                    case Define.MENU_INV_ITM: {
                                        CommonFragment fragment = new InvItmFragment();
                                        replaceContent(fragment, Define.TAG_INV_ITM, R.id.fl_content);
                                        break;
                                    }

                                    //출고관리
                                    case Define.MENU_SHIP: {
                                        CommonFragment fragment = new ShipFragment();
                                        replaceContent(fragment, Define.TAG_SHIP, R.id.fl_content);
                                        break;
                                    }

                                    //이동관리
                                    case Define.MENU_MOVE: {
                                        CommonFragment fragment = new MoveFragment();
                                        replaceContent(fragment, Define.TAG_MOVE, R.id.fl_content);
                                        break;
                                    }

                                    //재고실사
                                    case Define.MENU_INV_LOT: {
                                        CommonFragment fragment = new InvLotFragment();
                                        replaceContent(fragment, Define.TAG_INV_LOT, R.id.fl_content);
                                        break;
                                    }



                                }
                                setTitleImage(position + 2);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            return v;
        }

        class ViewHolder {
            TextView tv_menu;
        }
    }
}
