package kr.co.changwoo.wms.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Toast;

import java.text.DecimalFormat;

import kr.co.changwoo.wms.BuildConfig;


public class Utils {

    public static void Log(String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.d("WMS", msg);
    }

    public static void LogLine(String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag(), msg);
    }

    private static String tag() {
        int level = 4;
        StackTraceElement trace = Thread.currentThread().getStackTrace()[level];
        String fileName = trace.getFileName();
        String classPath = trace.getClassName();
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        return "WMS# " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    public static void Toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String setComma(float number){
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        return formatter.format(number);
    }

    public static int stringToInt(String number) {
        int num = 0;
        try {
            num = Integer.parseInt(number);
        } catch (Exception e) {
            num = 0;
        }
        return num;
    }

    public static long stringToLong(String number) {
        long num = 0;
        try {
            num = Long.parseLong(number);
        } catch (Exception e) {
            num = 0;
        }
        return num;
    }

    public static float stringToFloat(String number) {
        float num = 0;
        try {
            num = Float.parseFloat(number);
        } catch (Exception e) {
            num = 0;
        }
        return num;
    }

    public static String appVersionName(Context context) {
        String version = "0";
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return version;
    }

    //버전에 따른 getColor를 return
    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static int getDpToPixel(Context context, float DP) {
        float px = 0;
        try {
            px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
        } catch (Exception e) {
            Utils.LogLine(e.getMessage());
        }
        return (int) px;
    }

    //string null 체크
    public static boolean isEmpty(String str){
        if(str == null)return true;
        if(str.isEmpty())return true;
        return false;
    }

    //string null 체크
    public static String nullString(String str,String def){
        if(str == null)return def;
        if(str.isEmpty())return def;
        return str;
    }
}
