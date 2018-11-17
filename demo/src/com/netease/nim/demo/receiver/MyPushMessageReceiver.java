package com.netease.nim.demo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.netease.nim.demo.R;
import com.netease.nim.demo.main.activity.MainActivity;


import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sunjj on 2017/5/4.
 */

public class MyPushMessageReceiver extends BroadcastReceiver {
    private Context context;
    // 声明Notification(通知)的管理者  
    private NotificationManager mNotifyMgr;
    // 声明Notification（通知）对象  
    private Notification notification;
    // 消息的唯一标示id  
    public static final int mNotificationId = 001;
    private String title;
    private String content;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;
        Gson gson = new Gson();
//        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
//            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
//            com.netease.nim.demo.common.entity.bmob.Notification myNotification = gson.fromJson(intent.getStringExtra("msg"), com.netease.nim.demo.common.entity.bmob.Notification.class);
//            if(!TextUtils.isEmpty(myNotification.getTitle())){
//                title = myNotification.getTitle();
//            }else {
//               title="壹度教育";
//            }
//            content = myNotification.getAlert();
////            intent.getStringExtra("msg")
//            //根据推送消息自行处理
//            // 创建一个即将要执行的PendingIntent对象
//            Intent resultIntent = new Intent(context,
//                    MainActivity.class);
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(
//                    context, 0, resultIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//            // 建立所要创建的Notification的配置信息，并有notifyBuilder来保存。
//            Notification.Builder builder = new Notification.Builder(context);
//            builder
//                    // 触摸之后，通知立即消失
//                    .setAutoCancel(true)
//                    // 显示的时间
//                    .setWhen(System.currentTimeMillis())
//                    // 设置通知的小图标
//                    .setSmallIcon(R.drawable.app_icon)
//                    // 设置状态栏显示的文本
//                    .setTicker("状态栏提示消息")
//                    // 设置通知的标题
//                    .setContentTitle(title)
//                    // 设置通知的内容
//                    .setContentText(content)
//                    // 设置声音（系统默认的）
//                    .setDefaults(Notification.DEFAULT_SOUND)
////                    // 设置声音（自定义）
////                    .setSound(
////                            Uri.parse("android.resource://org.crazyit.ui/"
////                                    + R.raw.msg))
//                    // 设置跳转的activity
//                    .setContentIntent(resultPendingIntent);
//            notification = builder.getNotification();
//            // 创建NotificationManager对象，并发布和管理所要创建的Notification
//            mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//            mNotifyMgr.notify(mNotificationId, notification);
//        }
    }
}
