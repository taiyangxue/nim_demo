package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.home.fragment.MyHomesFragment;
import com.netease.nim.demo.main.model.MainTab;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyHomeFragment extends MainTabFragment {
    private MyHomesFragment fragment;
    public MyHomeFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }
    @Override
    protected void onInit() {
        fragment = (MyHomesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_homes_fragment);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
}
