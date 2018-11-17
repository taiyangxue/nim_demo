package com.netease.nim.demo.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Fankui;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;



public class FankuiActivity extends UI {
    @ViewInject(R.id.checkBox1)
    private CheckBox chechBox1;
    @ViewInject(R.id.checkBox2)
    private CheckBox chechBox2;
    @ViewInject(R.id.checkBox3)
    private CheckBox chechBox3;
    @ViewInject(R.id.checkBox4)
    private CheckBox chechBox4;
    @ViewInject(R.id.et_msg)
    private EditText et_msg;
    private String msg="";
    private Video video;

    @OnClick({R.id.btn_submit})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_submit:
                if(chechBox1.isChecked()&&!TextUtils.isEmpty(chechBox1.getText().toString())){
                    msg+=","+chechBox1.getText().toString();
                }
                if(chechBox2.isChecked()&&!TextUtils.isEmpty(chechBox2.getText().toString())){
                    msg+=","+chechBox2.getText().toString();
                }
                if(chechBox3.isChecked()&&!TextUtils.isEmpty(chechBox3.getText().toString())){
                    msg+=","+chechBox3.getText().toString();
                }
                if(chechBox4.isChecked()&&!TextUtils.isEmpty(chechBox4.getText().toString())){
                    msg+=","+chechBox4.getText().toString();
                }
                if(!TextUtils.isEmpty(et_msg.getText().toString())){
                    msg+=","+et_msg.getText().toString();
                }
                Fankui fankui=new Fankui();
                fankui.setVideoName(video.getVideoName());
                fankui.setMsg(msg);
                fankui.setVideo(video);
//                fankui.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        if(e==null){
//                            MyUtils.showToast(FankuiActivity.this,"提交成功");
//                            finish();
//                        }
//                    }
//                });
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fankui);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "反馈";
        setToolBar(R.id.toolbar, options);
        ViewUtils.inject(this);
        video = (Video) getIntent().getSerializableExtra("video");
    }

}
