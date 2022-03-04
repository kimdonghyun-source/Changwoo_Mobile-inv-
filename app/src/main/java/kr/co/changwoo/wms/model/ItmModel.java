package kr.co.changwoo.wms.model;

import java.util.List;

public class ItmModel extends ResultModel {

    List<ItmModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //수량
        int in_qty;
        //순번
        int pda_seq;

        public String getItm_code() {
            return itm_code;
        }

        public void setItm_code(String itm_code) {
            this.itm_code = itm_code;
        }

        public String getItm_name() {
            return itm_name;
        }

        public void setItm_name(String itm_name) {
            this.itm_name = itm_name;
        }

        public int getIn_qty() {
            return in_qty;
        }

        public void setIn_qty(int in_qty) {
            this.in_qty = in_qty;
        }

        public int getPda_seq() {
            return pda_seq;
        }

        public void setPda_seq(int pda_seq) {
            this.pda_seq = pda_seq;
        }
    }
}
