package com.loveplusplus.demo.image;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import uk.co.senab.photoview.TuyaView;

public class TuyaActivity extends Activity {

    private TuyaView tuyaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuya1);
        tuyaView= (TuyaView) findViewById(R.id.tuyaview1);
        findViewById(R.id.btn_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuyaView.undo();
            }
        });
        findViewById(R.id.btn_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuyaView.redo();
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
