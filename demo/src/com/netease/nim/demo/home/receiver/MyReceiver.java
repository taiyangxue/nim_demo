package com.netease.nim.demo.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.activity.HudongActivity;

public class MyReceiver extends BroadcastReceiver {
    private Intent it;
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        MyUtils.showToast(context, intent.getStringExtra("type"));
        switch (intent.getIntExtra("type", 0)) {
            case 0:
                break;
            case 1:
                it = new Intent(context, NEVideoPlayerActivity.class);
//                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("media_type", "videoondemand");
                it.putExtra("decode_type", "software");
                it.putExtra("videoPath", intent.getStringExtra("url"));
                context.startActivity(it);
                break;
            case 2:
                //收藏
                ApiUtils.getInstance().video_collect(SharedPreferencesUtils.getInt(context, "account_id", 0)+"",
                        intent.getStringExtra("video_id"),intent.getIntExtra("course",0), new ApiListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                MyUtils.showToast(context,"收藏成功");
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                MyUtils.showToast(context,errorMsg);
                            }
                        });
                break;
            case 3:
                //互动
                it=new Intent(context,HudongActivity.class);
                it.putExtra("video_id",intent.getStringExtra("video_id"));
                context.startActivity(it);
                break;
        }
    }
}
