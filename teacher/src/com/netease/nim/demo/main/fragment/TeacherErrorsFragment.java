package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;

/**
 * Created by Administrator on 2017/2/6.
 */

public class TeacherErrorsFragment extends MainTabFragment {
    private TeacherErrorFragment fragment;
    public TeacherErrorsFragment() {
        setContainerId(MainTab.HOME_ROOM.fragmentId);
    }
    @Override
    protected void onInit() {
        fragment = (TeacherErrorFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_homes_fragment);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
}
