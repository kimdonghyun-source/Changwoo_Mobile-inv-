package kr.co.changwooinv.wms.model;

import java.util.List;

public class WareHouseModel extends ResultModel {

    List<WareHouseModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //코드
        String Code;
        //명칭
        String Name;

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

    }//Close model

}//Close Model
