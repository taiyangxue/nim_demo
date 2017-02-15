package com.netease.nim.demo.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/9/27.
 */
public class MyUtils {
    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }

    public static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
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

}
