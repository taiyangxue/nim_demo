package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Collection;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.CollectionAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CollectionActivity extends UI {
    private static final String TAG = "CollectionActivity";
    private CollectionAdapter adapter;
    private RecyclerView recyclerView;
    private PullToRefreshLayout swipeRefreshLayout;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = title;
        setToolBar(R.id.toolbar, options);
        findViews();
        fetchData();
    }

    private void findViews() {
//        setTitle(getIntent().getStringExtra("title"));
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                fetchData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        recyclerView = findView(R.id.recycler_view);
        adapter = new CollectionAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<CollectionAdapter>() {
            @Override
            public void onItemClick(CollectionAdapter adapter, View vew, int position) {
//                final Object videoDir = adapter.getItem(position);
//                startVideo(adapter.getItem(position));
            }
            @Override
            public void onItemLongClick(CollectionAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                showCollectionDialog(adapter.getItem(position),position);
           }
        });
    }
    private void startVideo(Collection collection) {
        Intent intent = new Intent(this, NEVideoPlayerActivity.class);
        intent.putExtra("media_type", "videoondemand");
        intent.putExtra("decode_type", "software");
        intent.putExtra("videoPath", collection.getOrigUrl());
        startActivity(intent);
    }
    private void fetchData() {
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            //获取目录数据
            BmobQuery<Collection> query = new BmobQuery<>();
            query.addWhereEqualTo("user",userInfo.getUsername());
            query.order("-updatedAt");
            query.findObjects(new FindListener<Collection>() {
                @Override
                public void done(List<Collection> list, BmobException e) {
                    if (e == null) {
                        onFetchDataDone(true, list);
                    }else {
                        onFetchDataDone(false, null);
                    }
                }
            });
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(this, "当前用户尚未登录");
        }

    }
    private void onFetchDataDone(final boolean success, final List<Collection> data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束

                if (success) {
                    adapter.setNewData(data); // 刷新数据源
                    adapter.closeLoadAnimation();
                }
            }
        });
    }
    /**
     * 显示收藏对话框
     *
     * @param collection
     * @param position
     */
    private void showCollectionDialog(final Collection collection, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"取消收藏"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //添加到收藏列表
                        collection.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    MyUtils.showToast(CollectionActivity.this,"取消成功");
                                    adapter.remove(position);
                                }else {
                                    MyUtils.showToast(CollectionActivity.this,"取消失败，请重试");
                                }
                            }
                        });
                        break;
                }
            }
        });
        builder.show();
    }

}
