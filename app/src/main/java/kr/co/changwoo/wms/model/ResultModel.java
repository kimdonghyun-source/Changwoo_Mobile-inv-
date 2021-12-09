package kr.co.changwoo.wms.model;

import java.io.Serializable;

public class ResultModel implements Serializable {
    public static final int SUCCESS = 0;
    int Flag;
    String MSG;

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }
}