package com.netease.nim.demo.home.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Banner;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.activity.VideoDirActivity;
import com.netease.nim.demo.home.adapter.MyHomesAdapter;
import com.netease.nim.demo.home.adapter.MyHomesAdapter2;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;



/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyHomesFragment extends TFragment {
    private static final String TAG = MyHomesFragment.class.getSimpleName();
    private MyHomesAdapter adapter;
    //    private PullToRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyHomesAdapter2 adapter2;
    //    private MyHomesAdapter adapter3;
    //    private PullToRefreshLayout swipeRefreshLayout2;
    private RecyclerView recyclerView2;
    //    private RecyclerView recyclerView3;
    private SliderLayout sliderShow;
    private BitmapUtils bitmapUtils;
    private MyUser userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_homes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        userInfo = BmobUser.getCurrentUser(MyUser.class);
        findViews();
//        init();
    }

    public void onCurrent() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        // swipeRefreshLayout
        sliderShow = findView(R.id.slider);
        findView(R.id.tv_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.getUserType() < 3) {
                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                    intent.putExtra("isSelect", false);
                    intent.putExtra("isFirst", true);
                    intent.putExtra("title", "推送课");
                    startActivity(intent);
//            findView(R.id.recycler_view3).setVisibility(View.VISIBLE);
                } else {
                    showNormalDialog("推送");
                }
            }
        });
        findView(R.id.tv_subscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo.getUserType() < 4) {
                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                    intent.putExtra("isDingyue", true);
                    intent.putExtra("isSelect", false);
                    intent.putExtra("isFirst", true);
                    intent.putExtra("title", "订阅课");
                    startActivity(intent);
//            findView(R.id.recycler_view3).setVisibility(View.VISIBLE);
                } else {
                    showNormalDialog("订阅");
//                    MyUtils.showToast(getActivity(),"缴费后才能查看该内容");
//            findView(R.id.recycler_view3).setVisibility(View.GONE);
                }

            }
        });
        findView(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                intent.putExtra("isOpen", true);
                startActivity(intent);
            }
        });
        bitmapUtils = new BitmapUtils(getActivity());
        recyclerView = findView(R.id.recycler_view);
        adapter = new MyHomesAdapter(bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
//        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyHomesAdapter>() {
//            @Override
//            public void onItemClick(MyHomesAdapter adapter, View view, int position) {
//                final Video video = adapter.getItem(position);
//                startVideo(video);
//            }
//        });
        recyclerView2 = findView(R.id.recycler_view2);
        adapter2 = new MyHomesAdapter2(recyclerView2);
        adapter2.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView2.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
//        parent = (VideoDir) getIntent().getSerializableExtra("parent");
        recyclerView2.addOnItemTouchListener(new OnItemClickListener<MyHomesAdapter2>() {
            @Override
            public void onItemClick(MyHomesAdapter2 adapter, View view, int position) {
                Intent intent=new Intent(getActivity(),VideoDirActivity.class);
                intent.putExtra("parent",adapter.getItem(position));
                intent.putExtra("isDingyue",true);
                startActivity(intent);
            }
        });
        if(userInfo.getUserType()==4){
            recyclerView2.setVisibility(View.GONE);
        }
    }

    private void showNormalDialog(String name) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage("开通试用" + name + "课请咨询：18926561053吴老师");
        normalDialog.setPositiveButton("拨打电话",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        downLoad(snapshotUrl);
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + "18926561053");
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void startVideo(Video video) {
        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
        intent.putExtra("media_type", "videoondemand");
        intent.putExtra("decode_type", "software");
        intent.putExtra("videoPath", video.getOrigUrl());
        startActivity(intent);
    }

//    /**
//     * 是否加载的是公开课
//     */
//    private void fetchData() {
//        if (userInfo != null) {
//            // 查询喜欢这个帖子的所有用户，因此查询的是用户表
//            BmobQuery<Video> query = new BmobQuery<Video>();
//            BmobQuery<MyUser> inQuery = new BmobQuery<MyUser>();
//            Log.e(TAG, userInfo.getUsername());
//            inQuery.addWhereEqualTo("username", userInfo.getUsername());
//            query.addWhereMatchesQuery("likes", "_User", inQuery);
//            query.order("-updatedAt");
//            query.findObjects(new FindListener<Video>() {
//
//                @Override
//                public void done(List<Video> list, BmobException e) {
//                    if (e == null) {
//                        if(list.size()>4){
//                            onFetchDataDone(true, list.subList(0,4));
//                        }else {
//                            onFetchDataDone(true, list);
//                        }
//                    } else {
//                        onFetchDataDone(false, null);
//                        Log.e(TAG, e.getErrorCode() + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            });
//            BmobQuery<VideoDir> videoDirBmobQuery = new BmobQuery<>();
//            videoDirBmobQuery.addWhereMatchesQuery("dingYueUser", "_User", inQuery);
//            videoDirBmobQuery.order("-updatedAt");
//            videoDirBmobQuery.findObjects(new FindListener<VideoDir>() {
//                @Override
//                public void done(List<VideoDir> list, BmobException e) {
//                    if (e == null) {
//                        if(list.size()>3){
//                            onFetchDataDoneDir(true, list.subList(0,3));
//                        }else {
//                            onFetchDataDoneDir(true, list);
//                        }
//                    } else {
//                        onFetchDataDoneDir(false, null);
//                        Log.e(TAG, e.getErrorCode() + e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
//            //缓存用户对象为空时， 可打开用户注册界面…
//            MyUtils.showToast(getActivity(), "当前用户尚未登录");
//        }
//    }
    private void onFetchDataDone(final boolean success, final List<Video> data) {
        Activity context = getActivity();
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                        swipeRefreshLayout2.setRefreshing(false); // 刷新结束
                    if (success) {
//                        adapter.setNewData(data); // 刷新数据源
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                adapter.closeLoadAnimation();
                            }
                        });
                    }
                }
            });
        }
    }
    private void onFetchDataDoneDir(final boolean success, final List<VideoDir> data) {
        Activity context = getActivity();
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                        swipeRefreshLayout2.setRefreshing(false); // 刷新结束
                    if (success) {
                        adapter2.setNewData(data); // 刷新数据源
                        postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                adapter2.closeLoadAnimation();
                            }
                        });
                    }
                }
            });
        }
    }

//    public void init() {
//        initSlider();
//        fetchData();
//    }
//
//    private void initSlider() {
//        BmobQuery<Banner> bannerBmobQuery = new BmobQuery<>();
//        bannerBmobQuery.findObjects(new FindListener<Banner>() {
//            @Override
//            public void done(List<Banner> list, BmobException e) {
//                if (e == null) {
//                    for (final Banner banner : list) {
//                        DefaultSliderView defaultSliderView=new DefaultSliderView(getActivity());
////                        TextSliderView textSliderView = new TextSliderView(getActivity());
//                        if (banner.getFile() != null && banner.getFile().getUrl() != null) {
//                            defaultSliderView.image(banner.getFile().getUrl()).setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                @Override
//                                public void onSliderClick(BaseSliderView slider) {
//                                    Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
//                                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
//                                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{banner.getFile().getUrl()});
//                                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
//                                    getActivity().startActivity(intent);
//                                }
//                            });
////                            textSliderView
////                                    .description(banner.getTitle())
////                                    .image(banner.getFile().getUrl())
////                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
////                                        @Override
////                                        public void onSliderClick(BaseSliderView slider) {
//////                                          startVideo(video);
//////                                            Intent intent=new Intent(getActivity(), HtmlActivity.class);
//////                                            intent.putExtra("url",banner.getFile().getUrl());
//////                                            startActivity(intent);
////                                            Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
////                                            // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
////                                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{banner.getFile().getUrl()});
////                                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
////                                            getActivity().startActivity(intent);
////                                        }
////                                    });
//                        } else {
//                            defaultSliderView
//                                    .image(R.drawable.fengmian);
//                        }
//                        sliderShow.addSlider(defaultSliderView);
//                    }
//                } else {
//                    sliderShow.removeAllSliders();
//                    findView(R.id.tv_history).setVisibility(View.GONE);
//                    DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
//                    textSliderView
//                            .image(R.drawable.fengmian);
//                    sliderShow.addSlider(textSliderView);
//                }
//            }
//        });
//        sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        sliderShow.setDuration(2000*60);
//    }
}
