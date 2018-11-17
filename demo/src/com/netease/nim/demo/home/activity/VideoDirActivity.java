package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Collection;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.VideoDirAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.List;

public class VideoDirActivity extends UI {
    private static final String TAG = "VideoDirActivity";
    private static final int SELECT_VIDEO_DIR = 0;
    private static final int SELECT_VIDEO_DIR2 = 1;
    private static final int SELECT_USER = 2;
    private VideoDirAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private boolean isOpen;
    private String title;
    private PullToRefreshLayout swipeRefreshLayout;
    private VideoDir parent;
    private List<Object> allLists = new ArrayList<>();
    private boolean isSelect;
    private LinearLayout ll_select_video_dir;
    private boolean isFirst;
    /**
     * 上级传过来的文件id
     */
    private String videoDirId;
    private EditText et_name;
    private Object current_videoDir;
    private int current_position;
    private boolean isDingyue;
    private boolean isOnlyVideo;
    private MyUser userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_dir_admin);
        isOpen = getIntent().getBooleanExtra("isOpen", false);
        parent = (VideoDir) getIntent().getSerializableExtra("parent");
        isSelect = getIntent().getBooleanExtra("isSelect", false);
        isFirst = getIntent().getBooleanExtra("isFirst", false);
        isDingyue = getIntent().getBooleanExtra("isDingyue", false);
        isOnlyVideo = getIntent().getBooleanExtra("isOnlyVideo", false);
        videoDirId = getIntent().getStringExtra("id");
//        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        if (parent != null) {
            options.titleString = parent.getName();
        } else if (isDingyue) {
            options.titleString = "订阅课";
        } else if (isOpen) {
            options.titleString = "公开课";
        } else {
            options.titleString = "推送课";
        }
        setToolBar(R.id.toolbar, options);
//        findViews();
//        fetchData();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.section_activity_menu, menu);
//        super.onCreateOptionsMenu(menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////        switch (item.getItemId()) {
////            case R.id.add_menu:
//////                MyUtils.showToast(this, "添加"+isDingyue);
////                if (!isDingyue && !isOpen) {
////                    showAddVideoDir();
////                } else {
////                    MyUtils.showToast(this, "您没有权限修改文件夹");
////                }
////                break;
////        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, requestCode + ">>>" + resultCode);
//        if (resultCode == 0) {
//            switch (requestCode) {
//                case SELECT_VIDEO_DIR:
//                    if (data != null) {
//                        Intent intent = new Intent();
//                        intent.putExtra("videoDir", (VideoDir) data.getSerializableExtra("videoDir"));
//                        Log.e(TAG, requestCode + ">>>" + resultCode);
//                        setResult(SELECT_VIDEO_DIR, intent);
//                        VideoDirActivity.this.finish();
//                    }
//                    break;
//                case SELECT_VIDEO_DIR2:
//                    if (data != null) {
//                        final VideoDir videoDir = (VideoDir) data.getSerializableExtra("videoDir");
//                        if (videoDir != null) {
//                            if (current_videoDir instanceof VideoDir) {
//                                ((VideoDir) current_videoDir).setParent(videoDir);
//                                ((VideoDir) current_videoDir).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        MyUtils.showToast(VideoDirActivity.this, "移动成功");
//                                        adapter.remove(current_position);
//                                    }
//                                });
//                            } else if (current_videoDir instanceof Video) {
////                                VideoDir videoDir2 = new VideoDir();
////                                videoDir2.setObjectId("nocxZAAZ");
//                                //删除和该学生关联的文件
//                                //查询该视频关联的学生文件夹
//                                BmobQuery<VideoDir> videoDirBmobQuery = new BmobQuery<VideoDir>();
//                                videoDirBmobQuery.addWhereRelatedTo("videoDirStu", new BmobPointer((Video) current_videoDir));
//                                videoDirBmobQuery.findObjects(new FindListener<VideoDir>() {
//
//                                    @Override
//                                    public void done(List<VideoDir> list, BmobException e) {
//                                        if (e == null) {
//                                            Log.i("bmob", "查询个数：" + list.size());
//                                            for (VideoDir videoDir3 : list) {
//                                                if (videoDir3.getCreatAccount().equals(DemoCache.getAccount())
//                                                        && !videoDir3.getObjectId().equals(videoDir.getObjectId())) {
//                                                    BmobRelation relation = new BmobRelation();
//                                                    relation.remove(videoDir3);
//                                                    ((Video) current_videoDir).setVideoDirStu(relation);
//                                                    ((Video) current_videoDir).update(new UpdateListener() {
//                                                        @Override
//                                                        public void done(BmobException e) {
//                                                            if (e == null) {
//                                                                Log.e(TAG, "删除关联关系成功");
//                                                            } else {
//                                                                Log.e(TAG, "删除关联关系失败");
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                            }
//                                        } else {
//                                            Log.i("bmob", "失败：" + e.getMessage());
//                                        }
//                                    }
//                                });
//                                BmobRelation relation = new BmobRelation();
//                                relation.add(videoDir);
//                                ((Video) current_videoDir).setVideoDirStu(relation);
//                                ((Video) current_videoDir).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if (e == null) {
//                                            MyUtils.showToast(VideoDirActivity.this, "移动成功");
//                                            adapter.remove(current_position);
//                                            BmobRelation relation2 = new BmobRelation();
//                                            VideoDir videoDir2 = new VideoDir();
//                                            videoDir2.setObjectId("nocxZAAZ");
//                                            relation2.remove(videoDir2);
//                                            ((Video) current_videoDir).setVideoDirStu(relation2);
//                                            ((Video) current_videoDir).update(new UpdateListener() {
//                                                @Override
//                                                public void done(BmobException e) {
//                                                    if (e == null) {
//                                                        Log.e(TAG, "删除关联关系成功");
//                                                    } else {
//                                                        Log.e(TAG, "删除关联关系失败");
//                                                    }
//                                                }
//                                            });
//                                        } else {
//                                            MyUtils.showToast(VideoDirActivity.this, e.getErrorCode() + e.getMessage());
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    }
//                    break;
//                case SELECT_USER:
//                    if (data != null) {
//                        MyUser user = (MyUser) data.getSerializableExtra("user");
//                        BmobRelation relation = new BmobRelation();
//                        relation.add(user);
//                        ((Video) current_videoDir).setLikes(relation);
//                        ((Video) current_videoDir).update(new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if (e == null) {
//                                    MyUtils.showToast(VideoDirActivity.this, "推送成功");
//                                } else {
//                                    MyUtils.showToast(VideoDirActivity.this, e.getErrorCode() + e.getMessage());
//                                }
//                            }
//                        });
//                    }
//                    break;
//            }
//        }
//    }
//
//    private void findViews() {
//        swipeRefreshLayout = findView(R.id.swipe_refresh);
//        ll_select_video_dir = findView(R.id.ll_select_video_dir);
//        if (isSelect) {
//            ll_select_video_dir.setVisibility(View.VISIBLE);
//        }
//        findView(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VideoDirActivity.this.finish();
//            }
//        });
//        findView(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                VideoDirActivity.this.finish();
//                Intent intent = new Intent();
//                intent.putExtra("videoDir", parent);
//                setResult(SELECT_VIDEO_DIR, intent);
//                VideoDirActivity.this.finish();
//            }
//        });
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
//        bitmapUtils = new BitmapUtils(this);
//        recyclerView = findView(R.id.recycler_view);
//        adapter = new VideoDirAdapter(bitmapUtils, recyclerView);
//        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
//        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
//        recyclerView.addOnItemTouchListener(new OnItemClickListener<VideoDirAdapter>() {
//            @Override
//            public void onItemClick(VideoDirAdapter adapter, View vew, int position) {
//                final Object videoDir = adapter.getItem(position);
//                if (videoDir instanceof VideoDir) {
//                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
//                    intent.putExtra("isSelect", isSelect);
//                    intent.putExtra("isOnlyVideo", isDingyue);
//                    intent.putExtra("isDingyue", isDingyue);
//                    intent.putExtra("isOpen", isOpen);
//                    intent.putExtra("parent", (VideoDir) videoDir);
//                    LogUtil.e(TAG, videoDirId);
//                    if (isSelect) {
//                        if (!TextUtils.isEmpty(videoDirId) && videoDirId.equals(((VideoDir) videoDir).getObjectId())) {
//                            MyUtils.showToast(VideoDirActivity.this, "文件不能移动的自身");
//                        } else {
//                            VideoDirActivity.this.startActivityForResult(intent, SELECT_VIDEO_DIR);
//                        }
//                    } else {
//                        VideoDirActivity.this.startActivity(intent);
//                    }
//                } else if (videoDir instanceof Video) {
////                    startVideo((Video) videoDir);
////                    if(!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())){
////                        Intent intent = new Intent(VideoDirActivity.this, ImagePagerActivity.class);
////                        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
////                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{((Video) videoDir).getSnapshotUrl()});
////                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
////                        startActivity(intent);
////                    }else {
////                        MyUtils.showToast(VideoDirActivity.this,"该视频未上传视频封面");
////                    }
//                }
//            }
//
//            @Override
//            public void onItemLongClick(VideoDirAdapter adapter, View view, int position) {
//                super.onItemLongClick(adapter, view, position);
//                if (!isSelect) {
//                    final Object videoDir = adapter.getItem(position);
//                    current_videoDir = videoDir;
//                    current_position = position;
//                    if (!isDingyue && !isOpen) {
//                        showSelectDialog(videoDir);
//                    } else {
//                        Object video = adapter.getItem(position);
//                        if (video instanceof Video) {
//                            showCollectionDialog(video);
//                        } else {
//                            MyUtils.showToast(VideoDirActivity.this, "您无权操作该文件夹下的内容");
//
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    private void startVideo(Video video) {
//        Intent intent = new Intent(this, NEVideoPlayerActivity.class);
//        intent.putExtra("media_type", "videoondemand");
//        intent.putExtra("decode_type", "software");
//        intent.putExtra("videoPath", video.getOrigUrl());
//        startActivity(intent);
//    }
//
//    private void fetchData() {
//        allLists.clear();
//        userInfo = BmobUser.getCurrentUser(MyUser.class);
//        if (userInfo != null) {
//            //获取目录数据
//            BmobQuery<VideoDir> query = new BmobQuery<>();
//            if (parent != null) {
//                query.addWhereEqualTo("parent", parent);
//            } else {
//                query.addWhereDoesNotExists("parent");
//                if (isOpen) {
//                    query.addWhereEqualTo("creatAccount", "88888888");
//                } else if (isDingyue) {
//                    query.addWhereEqualTo("isDingYue", isDingyue);
//                    Log.e(TAG, "isDingyue" + isDingyue);
//                    BmobQuery<MyUser> inQuery = new BmobQuery<MyUser>();
//                    inQuery.addWhereEqualTo("username", DemoCache.getAccount());
//                    query.addWhereMatchesQuery("dingYueUser", "_User", inQuery);
//                } else {
//                    query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
//                }
//            }
//            query.order("name");
//            query.findObjects(new FindListener<VideoDir>() {
//                @Override
//                public void done(List<VideoDir> list, BmobException e) {
//                    if (e == null) {
//                        if (list != null && list.size() > 0) {
//                            allLists.addAll(list);
//                            if (!isSelect) {
//                                getVideo();
//                            } else {
//                                onFetchDataDone(true, allLists);
//                            }
//                        } else {
//                            if (!isSelect) {
////                            Log.e(TAG, e.getErrorCode() + e.getMessage());
//                                getVideo();
//                            } else {
//                                onFetchDataDone(true, allLists);
//                            }
//                        }
////                        onFetchDataDone(true, list);
//                    } else {
//                        if (!isSelect) {
////                            Log.e(TAG, e.getErrorCode() + e.getMessage());
//                            getVideo();
//                        } else {
//                            onFetchDataDone(true, allLists);
//                        }
//                    }
//                }
//            });
//            //获取视频数据
//        } else {
//            //缓存用户对象为空时， 可打开用户注册界面…
//            MyUtils.showToast(this, "当前用户尚未登录");
//        }
//
//    }
//
//    private void getVideo() {
//        Log.e(TAG, isOpen + "isOpen");
//        BmobQuery<Video> queryVideo = new BmobQuery<Video>();
//        if (isOpen || isDingyue) {
//            if (parent != null) {
//                BmobQuery<Video> query1 = new BmobQuery<>();
//                query1.addWhereEqualTo("videoDirTea", parent);
//
//                BmobQuery<Video> query2 = new BmobQuery<>();
//                query2.addWhereEqualTo("videoDir", parent);
//
//                List<BmobQuery<Video>> queries = new ArrayList<BmobQuery<Video>>();
//                queries.add(query1);
//                queries.add(query2);
//                queryVideo.or(queries);
//            } else {
//                queryVideo.addWhereDoesNotExists("videoDir");
//                queryVideo.addWhereEqualTo("isOpen", isOpen);
//            }
//        } else {
//            BmobQuery<MyUser> inQuery = new BmobQuery<MyUser>();
//            inQuery.addWhereEqualTo("username", DemoCache.getAccount());
//            BmobQuery<VideoDir> inQuery2 = new BmobQuery<VideoDir>();
//            queryVideo.addWhereMatchesQuery("likes", "_User", inQuery);
//            if (parent != null) {
//                inQuery2.addWhereEqualTo("objectId", parent.getObjectId());
//                queryVideo.addWhereMatchesQuery("videoDirStu", "VideoDir", inQuery2);
////            queryVideo.addWhereEqualTo("videoDirStu", parent);
//            } else {
//                inQuery2.addWhereEqualTo("objectId", "nocxZAAZ");
//                queryVideo.addWhereMatchesQuery("videoDirStu", "VideoDir", inQuery2);
////            queryVideo.addWhereDoesNotMatchQuery("videoDirStu", "VideoDir", inQuery2);
////            queryVideo.addWhereDoesNotExists("videoDirStu");
//            }
//        }
////        queryVideo.order("updatedAt");
//        queryVideo.order("videoName");
//
//        queryVideo.findObjects(new FindListener<Video>() {
//
//            @Override
//            public void done(List<Video> list, BmobException e) {
//                if (e == null && list != null && list.size() > 0) {
////                        onFetchDataDone(true, list);
////                    if(parent!=null&&!isOpen){
//                    if (parent == null && isOpen) {
//                        onFetchDataDone(true, allLists);
//                        return;
//                    }
//                    allLists.addAll(list);
////                    }
//                    onFetchDataDone(true, allLists);
//                } else {
//                    if (allLists.size() > 0) {
//                        onFetchDataDone(true, allLists);
//                    } else {
//                        onFetchDataDone(false, null);
//                        MyUtils.showToast(VideoDirActivity.this, "该分类暂无数据");
//                    }
//                }
//            }
//        });
//    }
//
//    private void onFetchDataDone(final boolean success, final List<Object> data) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(false); // 刷新结束
//
//                if (success) {
//                    adapter.setNewData(data); // 刷新数据源
//                    adapter.closeLoadAnimation();
//                }
//            }
//        });
//    }
//
//    /**
//     * 显示操作对话框
//     *
//     * @param videoDir
//     */
//    private void showSelectDialog(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择操作");
//        //    指定下拉列表的显示数据
//        //    设置一个下拉的列表选择项
//        if (videoDir instanceof Video) {
//            builder.setItems(new String[]{"收藏", "移动到","反馈"}, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case 0:
//                            //添加到收藏列表
//                            addToColletion((Video) videoDir);
//                            break;
//                        case 1:
//                            Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
//                            intent.putExtra("isSelect", true);
//                            intent.putExtra("isFirst", true);
//                            if (videoDir instanceof VideoDir) {
//                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
//                            }
//                            startActivityForResult(intent, SELECT_VIDEO_DIR2);
//                            break;
//                        case 2:
//                            Intent intent1=new Intent(VideoDirActivity.this,FankuiActivity.class);
//                            intent1.putExtra("video",(Video) videoDir);
//                            startActivity(intent1);
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
//                            Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
//                            intent.putExtra("isSelect", true);
//                            intent.putExtra("isFirst", true);
//                            if (videoDir instanceof VideoDir) {
//                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
//                            }
//                            startActivityForResult(intent, SELECT_VIDEO_DIR2);
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
//
//    /**
//     * 显示收藏对话框
//     *
//     * @param videoDir
//     */
//    private void showCollectionDialog(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择操作");
//        //    指定下拉列表的显示数据
//        //    设置一个下拉的列表选择项
//        builder.setItems(new String[]{"收藏","反馈"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        //添加到收藏列表
//                        addToColletion((Video) videoDir);
//                        break;
//                    case 1:
////                        addFankui((Video) videoDir);
//                        Intent intent=new Intent(VideoDirActivity.this,FankuiActivity.class);
//                        intent.putExtra("video",(Video) videoDir);
//                        startActivity(intent);
//                        break;
//                }
//            }
//        });
//        builder.show();
//    }
//
//    private void addToColletion(final Video videoDir) {
//        BmobQuery<Collection> collectionBmobQuery=new BmobQuery<Collection>();
//        collectionBmobQuery.addWhereEqualTo("origUrl",videoDir.getOrigUrl());
//        collectionBmobQuery.addWhereEqualTo("user",userInfo.getUsername());
//        collectionBmobQuery.findObjects(new FindListener<Collection>() {
//            @Override
//            public void done(List<Collection> list, BmobException e) {
//                if(e==null&&list.size()>0){
//                    MyUtils.showToast(VideoDirActivity.this,"您已经添加到收藏了");
//                }else {
//                    Collection collection = new Collection();
//                    collection.setVideoName(videoDir.getVideoName());
//                    collection.setOrigUrl(videoDir.getOrigUrl());
//                    collection.setDownloadOrigUrl(videoDir.getDownloadOrigUrl());
//                    collection.setSnapshotUrl(videoDir.getSnapshotUrl());
//                    collection.setUser(userInfo.getUsername());
//                    if (videoDir.getAnswer() != null) {
//                        collection.setAnswerUrl(videoDir.getAnswer().getUrl());
//                    } else {
//                        collection.setAnswerUrl("");
//                    }
//                    if (videoDir.getDescription() != null) {
//                        collection.setDescription(videoDir.getDescription());
//                    } else {
//                        collection.setDescription("");
//                    }
//                    collection.save(new SaveListener<String>() {
//
//                        @Override
//                        public void done(String objectId, BmobException e) {
//                            if (e == null) {
//                                MyUtils.showToast(VideoDirActivity.this, "收藏成功");
//                            } else {
//                                MyUtils.showToast(VideoDirActivity.this, "收藏失败，请重试");
//                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//    }
//
//    /**
//     * 修改文件夹对话框
//     *
//     * @param videoDir
//     */
//    public void showDelete(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
//                                MyUtils.showToast(VideoDirActivity.this, "删除成功");
//                                adapter.remove(current_position);
//                                dialog.dismiss();
//                            } else {
//                                MyUtils.showToast(VideoDirActivity.this, "删除失败");
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
//                                MyUtils.showToast(VideoDirActivity.this, "删除成功");
//                                dialog.dismiss();
//                            } else {
//                                MyUtils.showToast(VideoDirActivity.this, "删除失败");
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
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dView = View.inflate(this, R.layout.dialog_edit_video_dir, null);
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
//                                    MyUtils.showToast(VideoDirActivity.this, "修改成功");
//                                    dialog.dismiss();
//                                } else {
//                                    MyUtils.showToast(VideoDirActivity.this, "修改失败");
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
//                                    MyUtils.showToast(VideoDirActivity.this, "修改成功");
//                                    dialog.dismiss();
//                                } else {
//                                    MyUtils.showToast(VideoDirActivity.this, "修改失败");
//                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    MyUtils.showToast(VideoDirActivity.this, "名称不能为空");
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
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dView = View.inflate(this, R.layout.dialog_add_video_dir, null);
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
//                    if (parent != null) {
//                        videoDir.setParent(parent);
//                    }
//                    videoDir.save(new SaveListener<String>() {
//                        @Override
//                        public void done(String s, BmobException e) {
//                            if (e == null) {
//                                MyUtils.showToast(VideoDirActivity.this, "添加成功");
//                                adapter.addData(0, videoDir);
//                                dialog.dismiss();
//                            }
//                        }
//                    });
//                } else {
//                    MyUtils.showToast(VideoDirActivity.this, "名称不能为空");
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
}
