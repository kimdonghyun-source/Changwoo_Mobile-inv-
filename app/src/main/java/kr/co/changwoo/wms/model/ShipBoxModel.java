package kr.co.changwoo.wms.model;

import java.util.List;

public class ShipBoxModel extends ResultModel{

    List<ShipBoxModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //번호
        int pda_seq;
        //일자
        String sreq_date;
        //의뢰서 번호
        String sreq_no;
        //의뢰서 순번
        String sreq_seq;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //타입
        String bprt_type;
        //as
        String itm_alias;
        //지시량
        int sreq_qty;
        //미지시량
        int sreq_rqty;
        //출하량
        int ship_qty;

        public int getPda_seq() {
            return pda_seq;
        }

        public void setPda_seq(int pda_seq) {
            this.pda_seq = pda_seq;
        }

        public String getSreq_date() {
            return sreq_date;
        }

        public void setSreq_date(String sreq_date) {
            this.sreq_date = sreq_date;
        }

        public String getSreq_no() {
            return sreq_no;
        }

        public void setSreq_no(String sreq_no) {
            this.sreq_no = sreq_no;
        }

        public String getSreq_seq() {
            return sreq_seq;
        }

        public void setSreq_seq(String sreq_seq) {
            this.sreq_seq = sreq_seq;
        }

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

        public String getBprt_type() {
            return bprt_type;
        }

        public void setBprt_type(String bprt_type) {
            this.bprt_type = bprt_type;
        }

        public String getItm_alias() {
            return itm_alias;
        }

        public void setItm_alias(String itm_alias) {
            this.itm_alias = itm_alias;
        }

        public int getSreq_qty() {
            return sreq_qty;
        }

        public void setSreq_qty(int sreq_qty) {
            this.sreq_qty = sreq_qty;
        }

        public int getSreq_rqty() {
            return sreq_rqty;
        }

        public void setSreq_rqty(int sreq_rqty) {
            this.sreq_rqty = sreq_rqty;
        }

        public int getShip_qty() {
            return ship_qty;
        }

        public void setShip_qty(int ship_qty) {
            this.ship_qty = ship_qty;
        }
    }
}
