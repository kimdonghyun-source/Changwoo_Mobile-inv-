package kr.co.changwoo.wms.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CommonFragment extends Fragment {

    public Context mContext = null;
    public Dialog dialog = null;
    public Handler handler = null;
    public CommonFragment parentContent = null;

    public int finishedCount = 0;

    LoadingDialog sLoadingDialog;

    @Override
    public void onAttach(Activity activity){
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.mContext = activity;
        this.handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if(sLoadingDialog != null && !sLoadingDialog.isShowing() || sLoadingDialog == null) {
            sLoadingDialog = LoadingDialog.create(getActivity(), null, null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        parentContent	=	null;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

    }

    public void onBackContent(){
        // TODO Auto-generated method stub

    }

    public void callNewClear(){

    }

    public void callRefresh(){
    }

    public void callResume(){
    }
    /**
     * @return the parentContent
     */
    public CommonFragment getParentContent() {
        return parentContent;
    }

    /**
     * @param parentContent the parentContent to set
     */
    public void setParentContent(CommonFragment parentContent) {
        this.parentContent = parentContent;
    }

    public void hideInput(EditText mEdit){
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(),0);
    }

    public void showInput(EditText mEdit){
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEdit, 0);
    }

    public CommonFragment getContent(String tag){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CommonFragment basecontent = (CommonFragment)fm.findFragmentByTag(tag);
        return basecontent;
    }

    public void replaceContent(CommonFragment content, String tag, int layout_id){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(layout_id,content, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commitAllowingStateLoss();
    }

    public void addContent(CommonFragment content, String tag, int layout_id){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        CommonFragment basecontent = getContent(tag);

        if(fm.getBackStackEntryCount() > 0)
            fm.popBackStack();

        ft.add(layout_id, content, tag);
        ft.addToBackStack(tag);

        /*if(basecontent == null){
            ft.add(layout_id, content, tag);
            ft.addToBackStack(tag);
        } else {
            for(int i = 0;i < fm.getBackStackEntryCount(); i++){
                fm.popBackStack();
            }
            ft.add(layout_id, content, tag);
            ft.addToBackStack(tag);
        }*/
        ft.commitAllowingStateLoss();
    }

    //기존 fragment와 새로운 fragment를 교체
    public void changeContent(CommonFragment content, String tag, int layout_id){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        CommonFragment basecontent = getContent(tag);

        if(fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
        }
        ft.add(layout_id, content, tag);
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();
    }

    public void goContent(CommonFragment content, String backTag, String tag, int layout_id){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(layout_id, content, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(backTag);
        ft.commitAllowingStateLoss();
    }

    protected void contentPopBackStack(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    protected void contentHide(CommonFragment content){
        if(content==null)return;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(content);
        ft.commitAllowingStateLoss();
    }

    protected void contentShow(CommonFragment content){
        if(content==null)return;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(content);
        ft.commitAllowingStateLoss();
    }

    public void showLoadingBar(Context context){

        if(context != null && sLoadingDialog != null && !sLoadingDialog.isShowing()) {
            sLoadingDialog.show();
        }
    }

    public void dismissLoadingBar(Context context){
        if(context != null && sLoadingDialog != null && sLoadingDialog.isShowing()) {
            sLoadingDialog.dismiss();
        }
    }

    //상단 움직이는 영역 스크롤좌표 가져오기
    public interface OnHeaderScrollListener {
        //edit : 헤더 높이 계산한 스크롤 좌표
        void onScrollY(int edit);
    }
}
