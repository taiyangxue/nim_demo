package com.loveplusplus.demo.image;

import android.app.ActivityManager;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/9/27.
 */
public class MyUtils {
    public static String formatUrl(String url){
        String path;
        if (url.startsWith("http")) {
            path = url;
        } else if (url.startsWith("/alioss")) {
            path = Common.OOS_HOST_MY1 + url;
        } else {
            path = Common.API_HOST + url;
        }
        return path;
    }
    public static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param timeFormat 日期格式 yyyy-MM-dd HH:mm:ss    毫秒(yyyy-MM-dd HH:mm:ss.SSS)
     * @return String 返回值为：xx天xx小时xx分xx秒xx毫秒
     */
    public static String getDifferenceTime(String strTime,  String timeFormat) {
        DateFormat df = new SimpleDateFormat(timeFormat);
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long ms = 0;
        try {
            one = df.parse(strTime);
//            two = df.parse(strTime2);
            long time1 = one.getTime();
//            long time2 = two.getTime();
            long time2 = System.currentTimeMillis();
            long diff ;
            if(time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
            ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "," + hour + "," + min + "," + sec ;
    }

    /*
    判断是否是手机号
     */
    public static boolean isMobileNumber(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[5,7])|(17[0,6-8])|(15[^4])|(18[0-9])|(20[0-2]))+\\d{8}$");
//        Pattern p = Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|20[0|1|2])+\\d{8})$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }
    /**
     * 判断是否为固定电话号码
     *
     * @param number
     *            固定电话号码
     * @return
     */
    public static boolean isFixedPhone(String number) {
        Matcher match = Pattern.compile("^(010|02\\d|0[3-9]\\d{2})?-\\d{6,8}$").matcher(number);
        return match.matches();
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.baidu.trace.LBSTraceService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(80);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 6      * 时间戳转换成日期格式字符串
     * 7      * @param seconds 精确到秒的字符串
     * 8      * @param formatStr
     * 9      * @return
     * 10
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static int date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return (int) (sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return
     */
    public static int timeStamp() {
        long time = System.currentTimeMillis();
        return (int) (time/1000);
    }
}
