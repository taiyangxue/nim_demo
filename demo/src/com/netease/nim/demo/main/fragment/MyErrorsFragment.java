package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyErrorsFragment extends MainTabFragment {
    private MyErrorFragment fragment;
    public MyErrorsFragment() {
        setContainerId(MainTab.ERROR_ADMIN.fragmentId);
    }
    @Override
    protected void onInit() {
        fragment = (MyErrorFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_error_fragment);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
}
