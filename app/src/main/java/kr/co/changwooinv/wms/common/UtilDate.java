package kr.co.changwooinv.wms.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilDate {
    //String 형식을 date형식으로 변환
    public static Date getStringToDate(String str, String format) {
        if (str == null)
            return null;

        SimpleDateFormat transFormat = new SimpleDateFormat(format);

        Date date = null;
        try {
            date = transFormat.parse(str);
        } catch (ParseException e) {
            Utils.LogLine(e.getMessage());
            return null;
        }
        return date;
    }

    /*
     * 현재 날짜에서 일 빼주기
     */
    public static String getAgoDayDate(int day, String format){
        Calendar cal = Calendar.getInstance ();
        cal.add ( cal.DAY_OF_MONTH, -day );

        Date date = cal.getTime();

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
        String dateString = formatter.format(date);

        return dateString;
    }

    /*
     * 현재 날짜에서 개월 빼주기
     */
    public static String getAgoMonthDate(int month, String format){
        Calendar cal = Calendar.getInstance ();
        cal.add ( cal.MONTH, -month );

        Date date = cal.getTime();

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
        String dateString = formatter.format(date);

        return dateString;
    }

    /*
     * 현재 날짜에서 연 빼주기
     */
    public static String getAgoYearDate(int year, String format){
        Calendar cal = Calendar.getInstance ();
        cal.add ( cal.YEAR, -year );

        Date date = cal.getTime();

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
        String dateString = formatter.format(date);

        return dateString;
    }

    /**
     * 현재 날짜 및 시간을 구한다. 사용법 : DateUtil.date(포맷을 정해준다) <br>
     * DateUtil.date("yyyyMMddhhmmss") <br>
     * DateUtil.date("yyyy.MM.dd hh:mm:ss") <br>
     *
     * @param format 출력포맷 (API에서 SimpleDateFormat 참조)
     * @return 포맷에 맞게 현재시간을 리턴
     */
    public static String getDateToString(Date date, String format) {
        if (date == null)
            return "";

        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);
        String dateString = formatter.format(date);

        return dateString;
    }

    /*
     * 스트링 형식 날짜를 원하는 포맷으로 리턴
     * 날짜
     * 기존 포맷
     * 바꿀 포맷
     */
    public static String formattedDate(String date, String fromFormatString, String toFormatString)
    {
        SimpleDateFormat fromFormat = new SimpleDateFormat(fromFormatString);
        SimpleDateFormat toFormat = new SimpleDateFormat(toFormatString);
        Date fromDate = null;
        try
        {
            fromDate = fromFormat.parse(date);
        }
        catch(ParseException e)
        {
            Utils.LogLine(e.getMessage());
            return date;
        }
        return toFormat.format(fromDate);
    }
}
