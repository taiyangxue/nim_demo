package com.netease.nim.demo.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        MyUtils.showToast(context, intent.getStringExtra("type"));
        switch (intent.getIntExtra("type", 0)) {
            case 0:
                break;
            case 1:
                Intent it = new Intent(context, NEVideoPlayerActivity.class);
//                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("media_type", "videoondemand");
                it.putExtra("decode_type", "software");
                it.putExtra("videoPath", intent.getStringExtra("url"));
                context.startActivity(it);
                break;
        }
    }
}
