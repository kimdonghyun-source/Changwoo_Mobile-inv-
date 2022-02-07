package kr.co.changwoo.wms.model;

import java.util.List;

public class ShipDetailModel extends ResultModel {

    List<ShipDetailModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class  Item extends ResultModel{
        //번호
        int pda_seq;
        //일자
        String ship_date;
        //의뢰서 번호
        String ship_no;
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
        //box_no
        int box_no;

        public int getPda_seq() {
            return pda_seq;
        }

        public void setPda_seq(int pda_seq) {
            this.pda_seq = pda_seq;
        }

        public String getShip_date() {
            return ship_date;
        }

        public void setShip_date(String ship_date) {
            this.ship_date = ship_date;
        }

        public String getShip_no() {
            return ship_no;
        }

        public void setShip_no(String ship_no) {
            this.ship_no = ship_no;
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

        public int getBox_no() {
            return box_no;
        }

        public void setBox_no(int box_no) {
            this.box_no = box_no;
        }
    }
}
