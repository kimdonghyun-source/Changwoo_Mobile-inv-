package kr.co.changwoo.wms.print;

public class PrintUtil {
    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;

    private static String makeCommand(int x, int y, int alignment, String str){
        int posX = 0;
        if(x == 0){
            if(alignment == 1)
                posX = 10;
            else if(alignment == 2)
                posX = 280;
            else if(alignment == 3)
                posX = 560;
            else
                posX = 10;
        }else{
            posX = x;
        }

        String text = "TEXT "+posX+","+y+","+"\"K.BF2\",0,1,1,"+alignment+","+"\""+str+"\""+"\r\n";

        return text;
    }

}