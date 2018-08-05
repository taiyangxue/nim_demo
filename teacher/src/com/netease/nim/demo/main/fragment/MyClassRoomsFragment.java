package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyClassRoomsFragment extends MainTabFragment {
    private MyClassRoomFragment fragment;
    public MyClassRoomsFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }
    @Override
    protected void onInit() {
        fragment = (MyClassRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.chat_rooms_fragment);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
}
