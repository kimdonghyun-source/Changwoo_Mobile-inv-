package kr.co.changwoo.wms.model;

import java.util.List;

public class ShipScanModel extends ResultModel {

    List<ShipScanModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //거래처코드
        String cst_code;
        //거래처명
        String cst_name;
        //seq
        int sreq_seq;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //타입
        String bprt_type;
        //지시량
        int sreq_qty;
        //미지시량
        int sreq_rqty;
        //출하의뢰일
        String ship_date;
        //순번
        String ship_no;

        //스캔할때 순번
        String ship_seq;

        public String getCst_code() {
            return cst_code;
        }

        public void setCst_code(String cst_code) {
            this.cst_code = cst_code;
        }

        public String getCst_name() {
            return cst_name;
        }

        public void setCst_name(String cst_name) {
            this.cst_name = cst_name;
        }

        public int getSreq_seq() {
            return sreq_seq;
        }

        public void setSreq_seq(int sreq_seq) {
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

        public String getShip_seq() {
            return ship_seq;
        }

        public void setShip_seq(String ship_seq) {
            this.ship_seq = ship_seq;
        }
    }
}
