package com.netease.nim.demo.main.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.home.adapter.VideoDirAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;



/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyVideoFragment extends MainTabFragment implements View.OnClickListener {
    private static final String TAG = MyVideoFragment.class.getSimpleName();
    private static final int SELECT_VIDEO_DIR = 0;
    private static final int SELECT_USER = 2;
    private Gson gson;
    /**
     * 6.0权限处理
     **/
    private boolean bPermission = false;
    // 图片封装为一个数组
    private VideoDirAdapter adapter;
    private PullToRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private Spinner spinner;
    private MyUser current_user;
    private List<MyUser> userList;
    private boolean isOpen;
    private TextView tv_add_video_dir;
    private List<Object> allLists = new ArrayList<>();
    private List<Object> allVideoDirLists = new ArrayList<>();
    private String typeId;
    private Object current_videoDir;
    private int current_position;
    private EditText et_name;
    private MyUser userInfo;

    public MyVideoFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }

    @Override
    protected void onInit() {
//        fragment = (MyClassRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_homes_fragment);
//        initData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_video_home, container, false);
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        findViews();
//        userInfo = BmobUser.getCurrentUser(MyUser.class);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, requestCode + ">>>" + resultCode);
//        if (resultCode == 0) {
//            switch (requestCode) {
//                case SELECT_VIDEO_DIR:
//                    if (data != null) {
//                        VideoDir videoDir = (VideoDir) data.getSerializableExtra("videoDir");
//                        if (videoDir != null) {
//                            if (current_videoDir instanceof VideoDir) {
//                                ((VideoDir) current_videoDir).setParent(videoDir);
//                                ((VideoDir) current_videoDir).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        MyUtils.showToast(getActivity(), "移动成功");
//                                        adapter.remove(current_position);
//                                    }
//                                });
//                            } else if (current_videoDir instanceof Video) {
//                                BmobRelation relation = new BmobRelation();
//                                relation.add(videoDir);
//                                ((Video) current_videoDir).setVideoDirStu(relation);
//                                ((Video) current_videoDir).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if (e == null) {
//                                            MyUtils.showToast(getActivity(), "移动成功");
//                                        } else {
//                                            MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
//    }
//
//    private void findViews() {
//        findView(R.id.ll_open).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //获取公开课
//                Intent intent=new Intent(getActivity(), VideoDedailActivity.class);
//                intent.putExtra("isOpen",true);
//                intent.putExtra("title","公开课");
//                startActivity(intent);
//            }
//        });
//        findView(R.id.ll_subscribe).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //获取订阅课
//                if(userInfo.getUserType()<4){
//                    Intent intent=new Intent(getActivity(), VideoDedailActivity.class);
//                    intent.putExtra("isSubscribe",true);
//                    intent.putExtra("title","订阅课");
//                    startActivity(intent);
//                }else {
//                    MyUtils.showToast(getActivity(),"您不是订阅用户，请缴费后观看");
//                }
//            }
//        });
//        tv_add_video_dir = findView(R.id.tv_add_video_dir);
//        tv_add_video_dir.setOnClickListener(this);
//        swipeRefreshLayout = findView(R.id.swipe_refresh);
//        bitmapUtils = new BitmapUtils(getActivity());
//        swipeRefreshLayout.setPullUpEnable(false);
//        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onPullDownToRefresh() {
//                fetchData();
//            }
//
//            @Override
//            public void onPullUpToRefresh() {
//
//            }
//        });
//        // recyclerView
//        recyclerView = findView(R.id.recycler_view);
//        adapter = new VideoDirAdapter(bitmapUtils, recyclerView);
//        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
//        recyclerView.addOnItemTouchListener(new OnItemClickListener<VideoDirAdapter>() {
//            @Override
//            public void onItemClick(VideoDirAdapter adapter, View view, int position) {
//                final Object videoDir = adapter.getItem(position);
//                if (videoDir instanceof VideoDir) {
//                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
//                    intent.putExtra("parent", (VideoDir) videoDir);
//                    intent.putExtra("isSelect", false);
//                    getActivity().startActivity(intent);
//                } else if (videoDir instanceof Video) {
//                    startVideo((Video) videoDir);
//                }
////                startVideo(video.getOrigUrl());
//            }
//
//            @Override
//            public void onItemLongClick(VideoDirAdapter adapter, View view, int position) {
//                super.onItemLongClick(adapter, view, position);
//                final Object videoDir = adapter.getItem(position);
//                current_videoDir = videoDir;
//                current_position = position;
//                showSelectDialog(videoDir);
////                if (videoDir instanceof VideoDir) {
//////                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
//////                    intent.putExtra("parent", (VideoDir) videoDir);
//////                    getActivity().startActivity(intent);
////                } else if (videoDir instanceof Video) {
////                    startVideo((Video) videoDir);
////                }
//            }
//        });
//        fetchData();
//    }
//
//
//    private void fetchData() {
//        allLists.clear();
//        //获取一级目录结构
//        BmobQuery<VideoDir> query = new BmobQuery<>();
//        query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
//        query.addWhereDoesNotExists("parent");
//        query.order("-createdAt");
//        query.findObjects(new FindListener<VideoDir>() {
//            @Override
//            public void done(List<VideoDir> list, BmobException e) {
//                if (e == null ) {
////                    onFetchDataDone(true, list);
//                    if(list != null && list.size() > 0){
//                        allLists.addAll(list);
//                        getVideo();
//                    }else {
//                        getVideo();
//                    }
//                } else {
//                    getVideo();
////                    onFetchDataDone(false, null);
//                    if (getActivity() != null) {
//                        Toast.makeText(getActivity(), "您尚未建立目录", Toast.LENGTH_SHORT).show();
//                    }
//                    Log.e(TAG, e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void getVideo() {
//        if (userInfo != null) {
//            BmobQuery<Video> queryVideo = new BmobQuery<Video>();
//            BmobQuery<MyUser> inQuery = new BmobQuery<MyUser>();
//            Log.e(TAG, userInfo.getUsername());
//            inQuery.addWhereEqualTo("username", userInfo.getUsername());
//            if (isOpen) {
//                queryVideo.addWhereEqualTo("isOpen", isOpen);
//            } else {
//                queryVideo.addWhereMatchesQuery("likes", "_User", inQuery);
//            }
//            queryVideo.addWhereDoesNotExists("videoDirStu");
//            queryVideo.order("-updatedAt");
//            queryVideo.findObjects(new FindListener<Video>() {
//
//                @Override
//                public void done(List<Video> list, BmobException e) {
//                    if (e == null && list != null && list.size() > 0) {
////                        onFetchDataDone(true, list);
//                        allLists.addAll(list);
//                        onFetchDataDone(true, allLists);
//                    } else {
//                        if (allLists.size() > 0) {
//                            onFetchDataDone(true, allLists);
//                        } else {
//                            onFetchDataDone(false, null);
//                        }
////                        MyUtils.showToast(VideoDedailActivity.this, "该分类暂无数据");
//                    }
//                }
//            });
//        }
//    }
//
//
//    private void startVideo(Video video) {
//        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
//        intent.putExtra("media_type", "videoondemand");
//        intent.putExtra("decode_type", "software");
//        intent.putExtra("videoPath", video.getOrigUrl());
//        startActivity(intent);
//    }
//
//    private void onFetchDataDone(final boolean success, final List<Object> data) {
//        Activity context = getActivity();
//        if (context != null) {
//            context.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(false); // 刷新结束
//
//                    if (success) {
//                        adapter.setNewData(data); // 刷新数据源
//                        postRunnable(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.closeLoadAnimation();
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_video_dir:
//                showAddVideoDir();
                break;
        }
    }
//
//    /**
//     * 删除文件夹对话框
//     *
//     * @param videoDir
//     */
//    public void showDelete(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
////设置对话框标题
//        builder.setTitle("提醒");
////设置对话框内的文本
//        if (videoDir instanceof VideoDir) {
//            builder.setMessage("确定要删除该文件夹及其下面的子文件？");
//        } else if (videoDir instanceof Video) {
//            builder.setMessage("确定要删除该文件吗？");
//        }
////设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(final DialogInterface dialog, int which) {
//                // 执行点击确定按钮的业务逻辑
//                if (videoDir instanceof VideoDir) {
//                    ((VideoDir) videoDir).delete(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
//                                //删除下面的子文件
//                                MyUtils.showToast(getActivity(), "删除成功");
//                                adapter.remove(current_position);
//                                dialog.dismiss();
//                            } else {
//                                MyUtils.showToast(getActivity(), "删除失败");
//                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                            }
//                        }
//                    });
//
//                } else if (videoDir instanceof Video) {
//                    ((Video) videoDir).update(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
//                                adapter.remove(current_position);
//                                MyUtils.showToast(getActivity(), "删除成功");
//                                dialog.dismiss();
//                            } else {
//                                MyUtils.showToast(getActivity(), "删除失败");
//                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                            }
//                        }
//                    });
//                }
//            }
//        });
////设置取消按钮
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 执行点击取消按钮的业务逻辑
//                dialog.dismiss();
//            }
//        });
////使用builder创建出对话框对象
//        AlertDialog dialog = builder.create();
////显示对话框
//        dialog.show();
//    }
//
//    /**
//     * 修改文件夹对话框
//     *
//     * @param videoDir
//     */
//    public void showEditVideoDir(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        View dView = View.inflate(getActivity(), R.layout.dialog_edit_video_dir, null);
//        final AlertDialog dialog = builder.create();
//        et_name = (EditText) dView.findViewById(R.id.et_name);
//        if (videoDir instanceof VideoDir) {
//            et_name.setText(((VideoDir) videoDir).getName());
//        } else if (videoDir instanceof Video) {
//            et_name.setText(((Video) videoDir).getVideoName());
//        }
//        dialog.setView(dView, 0, 0, 0, 0);
//        dialog.show();
//        //确认按钮监听
//        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String name = et_name.getText().toString().trim();
//                if (!TextUtils.isEmpty(name)) {
//                    if (videoDir instanceof VideoDir) {
//                        ((VideoDir) videoDir).setName(name);
//                        ((VideoDir) videoDir).update(new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if (e == null) {
//                                    MyUtils.showToast(getActivity(), "修改成功");
//                                    dialog.dismiss();
//                                } else {
//                                    MyUtils.showToast(getActivity(), "修改失败");
//                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                                }
//                            }
//                        });
//
//                    } else if (videoDir instanceof Video) {
//                        ((Video) videoDir).setVideoName(name);
//                        ((Video) videoDir).update(new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if (e == null) {
//                                    MyUtils.showToast(getActivity(), "修改成功");
//                                    dialog.dismiss();
//                                } else {
//                                    MyUtils.showToast(getActivity(), "修改失败");
//                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    MyUtils.showToast(getActivity(), "名称不能为空");
//                }
//            }
//        });
//        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }
//
//    /**
//     * 创建目录对话框
//     */
//    public void showAddVideoDir() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        View dView = View.inflate(getActivity(), R.layout.dialog_add_video_dir, null);
//        final AlertDialog dialog = builder.create();
//        final EditText et_tag = (EditText) dView.findViewById(R.id.et_name);
//        dialog.setView(dView, 0, 0, 0, 0);
//        dialog.show();
//        //确认按钮监听
//        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String name = et_tag.getText().toString().trim();
//                if (!TextUtils.isEmpty(name)) {
//                    final VideoDir videoDir = new VideoDir();
//                    videoDir.setName(name);
//                    videoDir.setCreatAccount(DemoCache.getAccount());
//                    videoDir.save(new SaveListener<String>() {
//                        @Override
//                        public void done(String s, BmobException e) {
//                            if (e == null) {
//                                MyUtils.showToast(getActivity(), "添加成功");
//                                adapter.addData(0,videoDir);
//                                dialog.dismiss();
//                            }
//                        }
//                    });
//                } else {
//                    MyUtils.showToast(getActivity(), "名称不能为空");
//                }
//            }
//        });
//        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }
//
//
//    /**
//     * 显示操作对话框
//     *
//     * @param videoDir
//     */
//    private void showSelectDialog(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("选择操作");
//        //    指定下拉列表的显示数据
//        //    设置一个下拉的列表选择项
//        if (videoDir instanceof Video) {
//            builder.setItems(new String[]{"移动到"}, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case 0:
//                            Intent intent = new Intent(getActivity(), VideoDirActivity.class);
//                            intent.putExtra("isSelect", true);
//                            intent.putExtra("isFirst", true);
//                            if (videoDir instanceof VideoDir) {
//                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
//                            }
//                            startActivityForResult(intent, SELECT_VIDEO_DIR);
//                            break;
//                    }
//                }
//            });
//        } else {
//            builder.setItems(new String[]{"移动到", "修改", "删除"}, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case 0:
//                            Intent intent = new Intent(getActivity(), VideoDirActivity.class);
//                            intent.putExtra("isSelect", true);
//                            intent.putExtra("isFirst", true);
//                            if (videoDir instanceof VideoDir) {
//                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
//                            }
//                            startActivityForResult(intent, SELECT_VIDEO_DIR);
//                            break;
//                        case 1:
//                            showEditVideoDir(videoDir);
//                            break;
//                        case 2:
//                            showDelete(videoDir);
//                            break;
//                    }
//                }
//            });
//        }
//        builder.show();
//    }
//    /**
//     * 保存到bmob服务器
//     *
//     * @param video
//     * @param user
//     */
//    private void saveVideo(VideoListResult.RetBean.ListBean video, MyUser user) {
//        Video myVideo = new Video();
//        myVideo.setVideoName(video.getVideoName());
//        myVideo.setSnapshotUrl(video.getSnapshotUrl());
//        myVideo.setOrigUrl(video.getOrigUrl());
//        myVideo.setDownloadOrigUrl(video.getDownloadOrigUrl());
//        myVideo.setDescription(video.getDescription());
//        myVideo.setOpen(isOpen);
//        if (!isOpen) {
//            BmobRelation relation = new BmobRelation();
////将当前用户添加到多对多关联中
//            relation.add(user);
////多对多关联指向`post`的`likes`字段
//            myVideo.setLikes(relation);
//        }
//        myVideo.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    Log.e(TAG, "保存成功");
//                } else {
//                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private String[] userData;
//    private ArrayAdapter<String> spinner_adapter;
//
//    private void initSpinner(Spinner spinner, String[] m) {
//        //将可选内容与ArrayAdapter连接起来
//        spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, m);
//
//        //设置下拉列表的风格
//        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        //将adapter 添加到spinner中
//        spinner.setAdapter(spinner_adapter);
//
//        //设置默认值
//        spinner.setVisibility(View.VISIBLE);
//    }
}
