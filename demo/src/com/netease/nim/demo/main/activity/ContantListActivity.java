package com.netease.nim.demo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

/**
 * 添加好友页面
 * Created by huangjun on 2015/8/11.
 */
public class ContantListActivity extends UI {


    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ContantListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant_list_activity);
        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.main_tab_contact;
        setToolBar(R.id.toolbar, options);
        findViews();
    }

    private void findViews() {

    }

}
