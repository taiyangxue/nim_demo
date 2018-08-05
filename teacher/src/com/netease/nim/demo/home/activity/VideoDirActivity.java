package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.chatroom.thridparty.ChatRoomHttpClient;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.home.adapter.VideoDirAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.constant.Extras;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.netease.nim.demo.home.adapter.VideoDirAdapter.JPG;
import static com.netease.nim.demo.home.adapter.VideoDirAdapter.MIME_JPEG;
import static com.netease.nim.uikit.session.constant.RequestCode.PICK_IMAGE;
import static com.netease.nim.uikit.session.constant.RequestCode.PREVIEW_IMAGE_FROM_CAMERA;

public class VideoDirActivity extends UI {
    private static final String TAG = "VideoDirActivity";
    private static final int SELECT_VIDEO_DIR = 0;
    private static final int SELECT_VIDEO_DIR2 = 1;
    private static final int SELECT_USER = 2;
    private static final int SELECT_USER_DIR_DINGYUE = 3;
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
    private MyUser userInfo;

    public Object getCurrent_videoDir() {
        return current_videoDir;
    }

    public void setCurrent_videoDir(Object current_videoDir) {
        this.current_videoDir = current_videoDir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_dir_admin);
//        isOpen = getIntent().getBooleanExtra("isOpen", false);
        parent = (VideoDir) getIntent().getSerializableExtra("parent");
        isSelect = getIntent().getBooleanExtra("isSelect", false);
        isFirst = getIntent().getBooleanExtra("isFirst", false);
        isDingyue = getIntent().getBooleanExtra("isDingyue", false);
        isOpen = getIntent().getBooleanExtra("isOpen", false);
        videoDirId = getIntent().getStringExtra("id");
//        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        if (parent != null) {
            options.titleString = parent.getName();
        } else if (isDingyue) {
            options.titleString = "订阅课";
        } else if (isOpen) {
            options.titleString = "公开课";
        }
        setToolBar(R.id.toolbar, options);
        findViews();
        fetchData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.section_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
//                MyUtils.showToast(this, "添加");
//                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//                if(userInfo.getUserType()==0){
                if (!isOpen && parent.getCreatAccount().equals(DemoCache.getAccount())) {
                    showAddVideoDir();
                } else {
                    MyUtils.showToast(VideoDirActivity.this, "您无权操作该文件夹");
                }
//                }else {
//                    if (!isDingyue && !isOpen) {
//                        showAddVideoDir();
//                    } else {
//                        MyUtils.showToast(this, "您没有权限修改文件夹");
//                    }
//                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, requestCode + ">>>" + resultCode);
        switch (requestCode) {
            case PICK_IMAGE:
                onPickImageActivityResult(requestCode, data);
                break;
            case PREVIEW_IMAGE_FROM_CAMERA:
                onPreviewImageActivityResult(requestCode, data);
                break;
        }
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_VIDEO_DIR:
                    if (data != null) {
                        Intent intent = new Intent();
                        intent.putExtra("videoDir", (VideoDir) data.getSerializableExtra("videoDir"));
                        Log.e(TAG, requestCode + ">>>" + resultCode);
                        setResult(SELECT_VIDEO_DIR, intent);
                        VideoDirActivity.this.finish();
                    }
                    break;
                case SELECT_VIDEO_DIR2:
                    if (data != null) {
                        VideoDir videoDir = (VideoDir) data.getSerializableExtra("videoDir");
                        if (videoDir != null) {
                            if (current_videoDir instanceof VideoDir) {
                                ((VideoDir) current_videoDir).setParent(videoDir);
                                ((VideoDir) current_videoDir).update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        MyUtils.showToast(VideoDirActivity.this, "移动成功");
                                        adapter.remove(current_position);
                                    }
                                });
                            } else if (current_videoDir instanceof Video) {
                                MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
                                if (userInfo.getUserType() == 0) {
                                    ((Video) current_videoDir).setVideoDir(videoDir);
                                    ((Video) current_videoDir).update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            adapter.remove(current_position);
                                            MyUtils.showToast(VideoDirActivity.this, "移动成功");
                                        }
                                    });
                                } else {
                                    ((Video) current_videoDir).setVideoDirTea(videoDir);
                                    ((Video) current_videoDir).update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            adapter.remove(current_position);
                                            MyUtils.showToast(VideoDirActivity.this, "移动成功");
                                        }
                                    });
//                                    if(((Video) current_videoDir).getVideoDir().getCreatAccount().equals(DemoCache.getAccount())){
//
//                                    }else {
//
//                                    }
                                }
                            }
                        }
                    }
                    break;
                case SELECT_USER:
                    if (data != null) {
                        List<MyUser> userList = (List<MyUser>) data.getSerializableExtra("userList");
                        if (userList != null && userList.size() > 0) {
                            for (MyUser user : userList) {
                                Log.e(TAG, "push");
                                push(user);
                            }
                        }
                        MyUser user = (MyUser) data.getSerializableExtra("user");
                        if (user != null) {
                            push(user);
                        }
                    }
                    break;
                case SELECT_USER_DIR_DINGYUE:
                    if (data != null) {
                        List<MyUser> userList = (List<MyUser>) data.getSerializableExtra("userList");
                        if (userList != null && userList.size() > 0) {
                            for (MyUser user : userList) {
                                Log.e(TAG, "dingyue");
                                dingyue(user);
                            }
                        }
                        MyUser user = (MyUser) data.getSerializableExtra("user");
                        if (user != null) {
                            dingyue(user);
                        }
                    }
                    break;
            }
        }
    }

    private void push(MyUser user) {
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        BmobRelation relation2 = new BmobRelation();
        VideoDir videoDir = new VideoDir();
        videoDir.setObjectId("nocxZAAZ");
        relation2.add(videoDir);
        ((Video) current_videoDir).setLikes(relation);
        ((Video) current_videoDir).setVideoDirStu(relation2);
        ((Video) current_videoDir).update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUtils.showToast(VideoDirActivity.this, "推送成功");
                } else {
                    MyUtils.showToast(VideoDirActivity.this, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void dingyue(MyUser user) {
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        ((VideoDir) current_videoDir).setDingYueUser(relation);
        ((VideoDir) current_videoDir).update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUtils.showToast(VideoDirActivity.this, "分享成功");
                } else {
                    MyUtils.showToast(VideoDirActivity.this, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        ll_select_video_dir = findView(R.id.ll_select_video_dir);
        if (isSelect) {
            ll_select_video_dir.setVisibility(View.VISIBLE);
        }
        findView(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDirActivity.this.finish();
            }
        });
        findView(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                VideoDirActivity.this.finish();
                Intent intent = new Intent();
                intent.putExtra("videoDir", parent);
                setResult(SELECT_VIDEO_DIR, intent);
                VideoDirActivity.this.finish();
            }
        });
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
        adapter = new VideoDirAdapter(bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<VideoDirAdapter>() {
            @Override
            public void onItemClick(VideoDirAdapter adapter, View vew, int position) {
                final Object videoDir = adapter.getItem(position);
                if (videoDir instanceof VideoDir) {
                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                    intent.putExtra("isSelect", isSelect);
                    intent.putExtra("isDingyue", isDingyue);
                    intent.putExtra("isOpen", isOpen);
                    intent.putExtra("parent", (VideoDir) videoDir);
                    LogUtil.e(TAG, videoDirId);
                    if (isSelect) {
                        if (!TextUtils.isEmpty(videoDirId) && videoDirId.equals(((VideoDir) videoDir).getObjectId())) {
                            MyUtils.showToast(VideoDirActivity.this, "文件不能移动的自身");
                        } else {
                            VideoDirActivity.this.startActivityForResult(intent, SELECT_VIDEO_DIR);
                        }
                    } else {
                        VideoDirActivity.this.startActivity(intent);
                    }
                } else if (videoDir instanceof Video) {
                    startVideo((Video) videoDir);
                }
            }

            @Override
            public void onItemLongClick(VideoDirAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                if (!isSelect) {
                    final Object videoDir = adapter.getItem(position);
                    current_videoDir = videoDir;
                    current_position = position;
                    //公开课的文件夹不能操作
                    if (isOpen && videoDir instanceof VideoDir) {
                        MyUtils.showToast(VideoDirActivity.this, "您不可以操作公开课的文件夹");
                        return;
                    }
//                    if (isDingyue && videoDir instanceof VideoDir) {
//                        MyUtils.showToast(VideoDirActivity.this, "您不可以操作订阅课的文件夹");
//                        return;
//                    }
                    showSelectDialog(videoDir);
                }
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
        allLists.clear();
        userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            //获取目录数据
            BmobQuery<VideoDir> query = new BmobQuery<>();
//            if (isOpen) {
//                //公开课读取公开课文件夹
//                query.addWhereEqualTo("creatAccount", "88888888");
//            } else {
//                query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
//                if (isFirst) {
//                    query.addWhereDoesNotExists("parent");
//                    if (!isSelect) {
//                        query.addWhereEqualTo("isDingYue", isDingyue);
//                    }
//                } else {
//                    query.addWhereEqualTo("parent", parent);
//                }
////                if (!isSelect) {
////                    query.addWhereEqualTo("isDingYue", isDingyue);
////                }
//            }
            if (parent != null) {
                query.addWhereEqualTo("parent", parent);
            } else {
                query.addWhereDoesNotExists("parent");
                if (isOpen) {
                    query.addWhereEqualTo("creatAccount", "88888888");
                } else {
                    if (userInfo.getUserType() == 0) {
                        query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
                        query.addWhereEqualTo("isDingYue", isDingyue);
                    } else {
                        if (isDingyue) {
                            if (isSelect) {
                                query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
                                query.addWhereEqualTo("isDingYue", isDingyue);
                            } else {
                                BmobQuery<VideoDir> query1 = new BmobQuery<>();
                                query1.addWhereEqualTo("creatAccount", DemoCache.getAccount());
                                query1.addWhereEqualTo("isDingYue", isDingyue);

                                BmobQuery<VideoDir> query2 = new BmobQuery<>();
                                query2.addWhereEqualTo("isDingYue", isDingyue);
                                BmobQuery<MyUser> inQuery = new BmobQuery<MyUser>();
                                inQuery.addWhereEqualTo("username", DemoCache.getAccount());
                                query2.addWhereMatchesQuery("dingYueUser", "_User", inQuery);

                                List<BmobQuery<VideoDir>> queries = new ArrayList<BmobQuery<VideoDir>>();
                                queries.add(query1);
                                queries.add(query2);
                                query.or(queries);
                            }
                        }
                    }
                }
            }
//            query.order("-createdAt");
            query.order("name");
//            query.order("updatedAt");
            query.findObjects(new FindListener<VideoDir>() {
                @Override
                public void done(List<VideoDir> list, BmobException e) {
                    if (e == null) {
                        if (list != null && list.size() > 0) {
                            allLists.addAll(list);
                            if (!isSelect) {
                                getVideo();
                            } else {
                                onFetchDataDone(true, allLists);
                            }
                        } else {
                            if (!isSelect) {
//                            Log.e(TAG, e.getErrorCode() + e.getMessage());
                                getVideo();
                            } else {
                                onFetchDataDone(true, allLists);
                            }
                        }
//                        onFetchDataDone(true, list);
                    } else {
                        if (!isSelect) {
//                            Log.e(TAG, e.getErrorCode() + e.getMessage());
                            getVideo();
                        } else {
                            onFetchDataDone(true, allLists);
                        }
//                        onFetchDataDone(false, null);
//                        Toast.makeText(VideoDirActivity.this, "该目录无内容", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //获取视频数据
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(this, "当前用户尚未登录");
        }
    }

    private void getVideo() {
        BmobQuery<Video> queryVideo = new BmobQuery<Video>();
//        if (isOpen) {
//            queryVideo.addWhereEqualTo("isOpen", isOpen);
//        }
        if (parent != null) {
            if (userInfo.getUserType() == 0) {
                queryVideo.addWhereEqualTo("videoDir", parent);
            } else {
                if (parent.getCreatAccount().equals(DemoCache.getAccount())) {
                    queryVideo.addWhereEqualTo("videoDirTea", parent);
                } else {
                    queryVideo.addWhereEqualTo("videoDir", parent);
                }
            }
        } else {
            queryVideo.addWhereDoesNotExists("videoDir");
            queryVideo.addWhereEqualTo("isOpen", isOpen);
        }
        queryVideo.include("videoDir");
//        queryVideo.order("updatedAt");
        queryVideo.order("videoName");
        queryVideo.findObjects(new FindListener<Video>() {

            @Override
            public void done(List<Video> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
//                        onFetchDataDone(true, list);
                    allLists.addAll(list);
                    onFetchDataDone(true, allLists);
                } else {
                    if (allLists.size() > 0) {
                        onFetchDataDone(true, allLists);
                    } else {
                        onFetchDataDone(false, null);
                        MyUtils.showToast(VideoDirActivity.this, "该分类暂无数据");
                    }
                }
            }
        });
    }

    private void onFetchDataDone(final boolean success, final List<Object> data) {
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
     * 显示操作对话框
     *
     * @param videoDir
     */
    private void showSelectDialog(final Object videoDir) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        if (videoDir instanceof Video) {
            //公开课的文件操作不可以分享，修改和删除
            if (isOpen) {
                if (userInfo.getUserType() == 0) {
                    builder.setItems(new String[]{"移动到", "设为公开课首页"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                                    intent.putExtra("isSelect", true);
                                    intent.putExtra("isFirst", true);
                                    intent.putExtra("isOpen", isOpen);
                                    if (videoDir instanceof VideoDir) {
                                        intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                                    }
                                    startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                    break;
                                case 1:
                                    //将文件夹置空释放掉
                                    ((Video) videoDir).remove("videoDir");
                                    ((Video) videoDir).setOpen(true);
                                    ((Video) videoDir).update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.e(TAG, "设置成功");
                                            } else {
                                                Log.e(TAG, "设置失败" + e.getMessage() + e.getErrorCode());
                                            }
                                        }
                                    });
                                    adapter.remove(current_position);
                                    break;
                            }
                        }
                    });
                } else {
                    MyUtils.showToast(VideoDirActivity.this, "您无权操作公开课的文件");
                }
            } else {
                if(((Video)videoDir).getTypeId().equals(userInfo.getTypeId())){
                    builder.setItems(new String[]{"移动到", "推送给", "修改", "删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                                    intent.putExtra("isSelect", true);
                                    intent.putExtra("isFirst", true);
                                    intent.putExtra("isDingyue", isDingyue);
                                    if (videoDir instanceof VideoDir) {
                                        intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                                    }
                                    startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                    break;
                                case 1:
                                    Intent intent1 = new Intent(VideoDirActivity.this, UserSelectActivity.class);
                                    startActivityForResult(intent1, SELECT_USER);
                                    break;
                                case 2:
                                    showEditVideoDir(videoDir);
                                    break;
                                case 3:
                                    showDelete(videoDir);
                                    break;
                            }
                        }
                    });
                }else {
                    builder.setItems(new String[]{"移动到", "推送给"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                                    intent.putExtra("isSelect", true);
                                    intent.putExtra("isFirst", true);
                                    intent.putExtra("isDingyue", isDingyue);
                                    if (videoDir instanceof VideoDir) {
                                        intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                                    }
                                    startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                    break;
                                case 1:
                                    Intent intent1 = new Intent(VideoDirActivity.this, UserSelectActivity.class);
                                    startActivityForResult(intent1, SELECT_USER);
                                    break;
                            }
                        }
                    });
                }
            }
        } else {
            if (isDingyue) {
                if (((VideoDir) videoDir).getCreatAccount().equals(DemoCache.getAccount())) {
                    builder.setItems(new String[]{"移动到", "分享给", "修改", "删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                                    intent.putExtra("isSelect", true);
                                    intent.putExtra("isDingYue", true);
                                    intent.putExtra("isFirst", true);
                                    if (videoDir instanceof VideoDir) {
                                        intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                                    }
                                    startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                    break;
                                case 1:
                                    if (parent != null) {
                                        MyUtils.showToast(VideoDirActivity.this, "订阅课的子文件夹不能分享");
                                        return;
                                    }
                                    Intent intent1 = new Intent(VideoDirActivity.this, UserSelectActivity.class);
                                    startActivityForResult(intent1, SELECT_USER_DIR_DINGYUE);
                                    break;
                                case 2:
                                    showEditVideoDir(videoDir);
                                    break;
                                case 3:
                                    showDelete(videoDir);
                                    break;
                            }
                        }
                    });
                } else {
                    builder.setItems(new String[]{"分享给"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    if (parent != null) {
                                        MyUtils.showToast(VideoDirActivity.this, "订阅课的子文件夹不能分享");
                                        return;
                                    }
                                    Intent intent1 = new Intent(VideoDirActivity.this, UserSelectActivity.class);
                                    startActivityForResult(intent1, SELECT_USER_DIR_DINGYUE);
                                    break;
                            }
                        }
                    });
                }
            } else {
                builder.setItems(new String[]{"移动到", "修改", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(VideoDirActivity.this, VideoDirActivity.class);
                                intent.putExtra("isSelect", true);
                                intent.putExtra("isFirst", true);
                                if (videoDir instanceof VideoDir) {
                                    intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                                }
                                startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                break;
                            case 1:
//                                showEditVideoDir(videoDir);
                                break;
                            case 2:
                                showDelete(videoDir);
                                break;
                        }
                    }
                });
            }
        }
        builder.show();
    }

    /**
     * 修改文件夹对话框
     *
     * @param videoDir
     */
    public void showDelete(final Object videoDir) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_alert);
//设置对话框标题
        builder.setTitle("提醒");
//设置对话框内的文本
        if (videoDir instanceof VideoDir) {
            builder.setMessage("确定要删除该文件夹及其下面的子文件？");
        } else if (videoDir instanceof Video) {
            builder.setMessage("确定要删除该文件吗？");
        }
//设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                if (videoDir instanceof VideoDir) {
                    ((VideoDir) videoDir).delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //删除下面的子文件
                                MyUtils.showToast(VideoDirActivity.this, "删除成功");
                                adapter.remove(current_position);
                                dialog.dismiss();
                            } else {
                                MyUtils.showToast(VideoDirActivity.this, "删除失败");
                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                            }
                        }
                    });

                } else if (videoDir instanceof Video) {
                    if (userInfo.getUserType() == 0) {
                        ((Video) videoDir).delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    //删除网易云的视频文件
                                    EduChatRoomHttpClient.getInstance().deleteVideoList(Integer.valueOf(((Video) videoDir).getVid()), new ChatRoomHttpClient.ChatRoomHttpCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            adapter.remove(current_position);
                                            MyUtils.showToast(VideoDirActivity.this, "删除成功");
                                        }
                                        @Override
                                        public void onFailed(int code, String errorMsg) {
                                            MyUtils.showToast(VideoDirActivity.this, "删除失败");
                                            LogUtil.e(TAG, errorMsg);
                                        }
                                    });
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(VideoDirActivity.this, "删除失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    }else {
                        MyUtils.showToast(VideoDirActivity.this,"您无权删除该视频文件");
                    }
                }
            }
        });
//设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击取消按钮的业务逻辑
                dialog.dismiss();
            }
        });
//使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
//显示对话框
        dialog.show();
    }

    /**
     * 修改文件夹对话框
     *
     * @param videoDir
     */
    public void showEditVideoDir(final Object videoDir) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_edit_video_dir, null);
        final android.app.AlertDialog dialog = builder.create();
        et_name = (EditText) dView.findViewById(R.id.et_name);
        if (videoDir instanceof VideoDir) {
            et_name.setText(((VideoDir) videoDir).getName());
        } else if (videoDir instanceof Video) {
            et_name.setText(((Video) videoDir).getVideoName());
        }
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (videoDir instanceof VideoDir) {
                        ((VideoDir) videoDir).setName(name);
                        ((VideoDir) videoDir).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(VideoDirActivity.this, "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(VideoDirActivity.this, "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });

                    } else if (videoDir instanceof Video) {
                        ((Video) videoDir).setVideoName(name);
                        ((Video) videoDir).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(VideoDirActivity.this, "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(VideoDirActivity.this, "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    MyUtils.showToast(VideoDirActivity.this, "名称不能为空");
                }
            }
        });
        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 创建目录对话框
     */
    public void showAddVideoDir() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_add_video_dir, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_tag = (EditText) dView.findViewById(R.id.et_name);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_tag.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    final VideoDir videoDir = new VideoDir();
                    videoDir.setName(name);
                    videoDir.setDingYue(isDingyue);
                    videoDir.setCreatAccount(DemoCache.getAccount());
                    if (parent != null) {
                        videoDir.setParent(parent);
                    }
                    videoDir.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                MyUtils.showToast(VideoDirActivity.this, "添加成功");
                                adapter.addData(0, videoDir);
                                dialog.dismiss();
                            }
                        }
                    });
                } else {
                    MyUtils.showToast(VideoDirActivity.this, "名称不能为空");
                }
            }
        });
        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            List<PhotoInfo> photos = PickerContract.getPhotos(data);
            if (photos == null) {
                Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
                return;
            }
            for (PhotoInfo photoInfo : photos) {
                Log.e(TAG,photoInfo.getAbsolutePath());
                compressSave(photoInfo.getAbsolutePath());
            }
//            sendImageAfterSelfImagePicker(data);
        } else {
            // 拍照
            Intent intent = new Intent();
            if (!handleImagePath(intent, data)) {
                return;
            }
            intent.setClass(this, PreviewImageFromCameraActivity.class);
            startActivityForResult(intent, PREVIEW_IMAGE_FROM_CAMERA);
        }
    }

    /**
     * 拍摄回调
     */
    private void onPreviewImageActivityResult(int requestCode, Intent data) {
        if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_SEND, false)) {
//            sendImageAfterPreviewPhotoActivityResult(data);
            final ArrayList<String> selectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_SCALED_IMAGE_LIST);
            final ArrayList<String> origSelectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_ORIG_IMAGE_LIST);
//            Log.e("TAG","拍摄回调"+selectedImageFileList.size()+origSelectedImageFileList.size());

            boolean isOrig = data.getBooleanExtra(Extras.EXTRA_IS_ORIGINAL, false);
            for (int i = 0; i < selectedImageFileList.size(); i++) {
                String imageFilepath = selectedImageFileList.get(i);
                Log.e("TAG","拍摄回调"+imageFilepath);
                compressSave(imageFilepath);
            }
        } else if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_RETAKE, false)) {
            String filename = StringUtil.get32UUID() + JPG;
            String path = StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);

            if (requestCode == PREVIEW_IMAGE_FROM_CAMERA) {
                PickImageActivity.start(this, PICK_IMAGE, PickImageActivity.FROM_CAMERA, path);
            }
        }
    }

    /**
     * 是否可以获取图片
     */
    private boolean handleImagePath(Intent intent, Intent data) {
        String photoPath = data.getStringExtra(Extras.EXTRA_FILE_PATH);
        if (TextUtils.isEmpty(photoPath)) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        }

        File imageFile = new File(photoPath);
        intent.putExtra("OrigImageFilePath", photoPath);
        File scaledImageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, MIME_JPEG);

        boolean local = data.getExtras().getBoolean(Extras.EXTRA_FROM_LOCAL, true);
        if (!local) {
            // 删除拍照生成的临时文件
            AttachmentStore.delete(photoPath);
        }

        if (scaledImageFile == null) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ImageUtil.makeThumbnail(this, scaledImageFile);
        }
        intent.putExtra("ImageFilePath", scaledImageFile.getAbsolutePath());
        return true;
    }

    /**
     * 将获取的图片压缩后上传
     *
     * @param photoPath//图片存放路径
     */
    private void compressSave(String photoPath) {
        try {
            File f = new File(photoPath);
            Bitmap bm = PictureUtil.getSmallBitmap(photoPath);
            FileOutputStream fos = new FileOutputStream(new File(
                    PictureUtil.getAlbumDir(), "small_" + f.getName()));

            bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);

//            Toast.makeText(getActivity(), "压缩成功，已保存至" + PictureUtil.getAlbumDir(), Toast.LENGTH_SHORT).show();
            String picPath = PictureUtil.getAlbumDir() + "/small_" + f.getName();
            final BmobFile bmobFile = new BmobFile(new File(picPath));
//            ((Video) current_videoDir).setAnswer(bmobFile);
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.e(TAG,"文件上传成功");
                        ((Video) current_videoDir).setAnswer(bmobFile);
                        ((Video) current_videoDir).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                MyUtils.showToast(VideoDirActivity.this, "上传成功");
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
//                        toast("上传文件失败：" + e.getMessage());
                        MyUtils.showToast(VideoDirActivity.this, "上传失败："+e.getErrorCode() + e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    Log.e(TAG,"上传进度"+value);
                }
            });
        } catch (Exception e) {

        }
    }
}
