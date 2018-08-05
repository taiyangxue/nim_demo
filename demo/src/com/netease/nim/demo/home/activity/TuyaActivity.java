package com.netease.nim.demo.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.R;
import com.netease.nim.demo.home.view.TuyaView;

public class TuyaActivity extends Activity {

    @ViewInject(R.id.tuyaview)
    private TuyaView tuyaView;

    @OnClick({R.id.btn_undo,R.id.btn_redo,R.id.btn_close})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_undo:
                tuyaView.undo();
                break;
            case R.id.btn_redo:
                tuyaView.redo();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuya1);
        ViewUtils.inject(this);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        WindowManager wm = this.getWindowManager();
//
//        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
//        tuyaView = new TuyaView(this,width,height-200);
//        tuyaView.setLayoutParams(params);
//        rl_main.addView(tuyaView);
    }
}
