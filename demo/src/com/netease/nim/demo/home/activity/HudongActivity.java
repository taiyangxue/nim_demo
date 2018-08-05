package com.netease.nim.demo.home.activity;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

public class HudongActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hudong);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "一题多解";
        setToolBar(R.id.toolbar, options);
    }
}
