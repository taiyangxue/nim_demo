package com.netease.nim.demo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.VideoDedailAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class VideoDedailActivity extends UI {
    private static final String TAG = "VideoDedailActivity";
    private VideoDedailAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private boolean isSubscribe;
    private String title;
    private PullToRefreshLayout swipeRefreshLayout;
    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_dir_admin);
        title = getIntent().getStringExtra("title");
        isOpen = getIntent().getBooleanExtra("isOpen",false);
        isSubscribe = getIntent().getBooleanExtra("isSubscribe",false);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = title;
        setToolBar(R.id.toolbar, options);
        findViews();
        fetchData();
    }

    private void findViews() {
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
        // recyclerView
        bitmapUtils = new BitmapUtils(this);
        recyclerView = findView(R.id.recycler_view);
        adapter = new VideoDedailAdapter(isOpen,isSubscribe,bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<VideoDedailAdapter>() {
            @Override
            public void onItemClick(VideoDedailAdapter adapter, View view, int position) {
                final Video video = adapter.getItem(position);
//                if(!TextUtils.isEmpty(video.getSnapshotUrl())){
//                    Intent intent = new Intent(VideoDedailActivity.this, ImagePagerActivity.class);
//                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{video.getSnapshotUrl()});
//                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
//                    startActivity(intent);
//                }else {
//                    MyUtils.showToast(VideoDedailActivity.this,"该视频未上传视频封面");
//                }
            }
        });
    }
    private void startVideo(Video video) {
        Intent intent = new Intent(this, NEVideoPlayerActivity.class);
        intent.putExtra("media_type", "videoondemand");
        intent.putExtra("decode_type", "software");
        intent.putExtra("videoPath", video.getOrigUrl());
        startActivity(intent);
    }
    private void fetchData() {
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            BmobQuery<Video> query = new BmobQuery<Video>();
            if(isOpen){
                query.addWhereEqualTo("isOpen",isOpen);
            }
            if(isSubscribe){
                query.addWhereEqualTo("isSubscribe",isSubscribe);
            }
            query.order("-updatedAt");
            query.findObjects(new FindListener<Video>() {


                @Override
                public void done(List<Video> list, BmobException e) {
                    if (e == null && list != null && list.size() > 0) {
                        onFetchDataDone(true, list);
                   } else {
                        onFetchDataDone(false, null);
                        MyUtils.showToast(VideoDedailActivity.this, "该分类暂无数据");
                        LogUtil.e(TAG,isSubscribe+""+e.getErrorCode()+e.getMessage());
                    }
                }
            });
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(this, "当前用户尚未登录");
        }
    }
    private void onFetchDataDone(final boolean success, final List<Video> data) {
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
}
