package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyHomeFragment extends MainTabFragment {
    @Override
    protected void onInit() {

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
}
