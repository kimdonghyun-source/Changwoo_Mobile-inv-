package kr.co.changwooinv.wms.model;

import java.util.List;

public class InvItmScanModel extends ResultModel {

    List<InvItmScanModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품목
        String Itm_Code;
        //수량
        float QTY;
        //박스
        int Box;
        //일자
        String Date_dt;
        //순번(디테일)
        int SEQ;

        public String getItm_Code() {
            return Itm_Code;
        }

        public void setItm_Code(String itm_Code) {
            Itm_Code = itm_Code;
        }

        public float getQTY() {
            return QTY;
        }

        public void setQTY(float QTY) {
            this.QTY = QTY;
        }

        public int getBox() {
            return Box;
        }

        public void setBox(int box) {
            Box = box;
        }

        public String getDate_dt() {
            return Date_dt;
        }

        public void setDate_dt(String date_dt) {
            Date_dt = date_dt;
        }

        public int getSEQ() {
            return SEQ;
        }

        public void setSEQ(int SEQ) {
            this.SEQ = SEQ;
        }
    }//Close model

}//Close Model
