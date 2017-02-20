package com.netease.nim.demo.home.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.R;
import com.netease.nim.demo.home.adapter.ErrorAdminAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

public class ErrorAdminActivity extends UI {
    private static final String TAG = "ErrorAdminActivity";

    private ErrorAdminAdapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_admin);
        findViews();
    }
    @OnClick({R.id.btn_push})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_push:

                break;
        }
    }
    private void findViews() {
        // recyclerView
        ViewUtils.inject(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new ErrorAdminAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
//        recyclerView.addOnItemTouchListener(new OnItemClickListener<ErrorAdminAdapter>() {
//            @Override
//            public void onItemClick(ErrorAdminAdapter adapter, View view, int position) {
////                ChatRoomInfo room = adapter.getItem(position);
////                ChatRoomActivity.start(ErrorAdminActivity.this, room.getRoomId());
//            }
//        });
    }
}
