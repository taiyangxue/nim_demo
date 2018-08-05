package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.activity.LeanRoomActivity;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.chatroom.thridparty.ChatRoomHttpClient;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.demo.common.entity.ChannelCreat;
import com.netease.nim.demo.common.entity.VideoListResult;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.activity.RoomInfoActivity;
import com.netease.nim.demo.home.activity.UserAdminActivity;
import com.netease.nim.demo.home.activity.UserSelectActivity;
import com.netease.nim.demo.home.activity.VideoDirActivity;
import com.netease.nim.demo.home.adapter.VideoDirAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyClassRoomFragment extends MainTabFragment implements View.OnClickListener {
    private static final String TAG = MyClassRoomFragment.class.getSimpleName();
    private static final int SELECT_VIDEO_DIR = 0;
    private static final int SELECT_USER = 2;
    private Gson gson;
    /**
     * 6.0权限处理
     **/
    private boolean bPermission = false;
    private Button btn_channel_creat;
    private LinearLayout ll_main;
    private LinearLayout ll_user;
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.play, R.drawable.room, R.drawable.room_info};
    private String[] iconName = {"开始直播", "自习室", "房间信息"};
    private ClassRoom classRoom;
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

    public MyClassRoomFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }

    @Override
    protected void onInit() {
//        fragment = (MyClassRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_homes_fragment);
//        initData();
    }

    private void initData() {
        BmobQuery<ClassRoom> query = new BmobQuery<>();
        query.addWhereEqualTo("account", DemoCache.getAccount());
        query.findObjects(new FindListener<ClassRoom>() {
            @Override
            public void done(List<ClassRoom> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0 && list.get(0) != null) {
                        btn_channel_creat.setVisibility(View.GONE);
                        ll_main.setVisibility(View.VISIBLE);
                        classRoom = list.get(0);
                        Log.e(TAG, classRoom.toString());
//                        fetchData(classRoom);
                        fetchData();
//                        initVideo(classRoom);
                    } else {
                        ll_main.setVisibility(View.GONE);
                        btn_channel_creat.setVisibility(View.VISIBLE);
                        MyUtils.showToast(getActivity(), "您尚未创建自己的教室");
                    }
                } else {
                    if (e.getErrorCode() == 101) {
                        ll_main.setVisibility(View.GONE);
                        btn_channel_creat.setVisibility(View.VISIBLE);
                        MyUtils.showToast(getActivity(), "您尚未创建自己的教室");
                    } else {
                        MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                    }
                }
            }
        });
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(getActivity(), data_list, R.layout.item_gridview_home, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        if (classRoom != null) {
                            classRoom.setOnline(true);
                            classRoom.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        if (classRoom.getRoomId() == null) {
                                            createRoom(classRoom.getName(), classRoom.getRtmpPullUrl());
                                        } else {
                                            enterRoom(classRoom.getRoomId(), classRoom.getRtmpPullUrl());
                                        }
                                    } else {
                                        if (e.getErrorCode() == 100) {
                                            if (classRoom.getRoomId() == null) {
                                                createRoom(classRoom.getName(), classRoom.getRtmpPullUrl());
                                            } else {
                                                enterRoom(classRoom.getRoomId(), classRoom.getRtmpPullUrl());
                                            }
                                        }
                                        MyUtils.showToast(getActivity(), "进入房间失败，请重试！");
                                    }
                                }
                            });
                        }
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), LeanRoomActivity.class));
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), RoomInfoActivity.class);
                        intent.putExtra("room", classRoom);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    private void startVideo(String origUrl) {
        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
        intent.putExtra("media_type", "videoondemand");
        intent.putExtra("decode_type", "software");
        intent.putExtra("videoPath", origUrl);
        startActivity(intent);
    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_class_room, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        initData();
        gson = new Gson();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, requestCode + ">>>" + resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_VIDEO_DIR:
                    if (data != null) {
                        VideoDir videoDir = (VideoDir) data.getSerializableExtra("videoDir");
                        if (videoDir != null) {
                            if (current_videoDir instanceof VideoDir) {
                                ((VideoDir) current_videoDir).setParent(videoDir);
                                ((VideoDir) current_videoDir).update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        MyUtils.showToast(getActivity(), "移动成功");
                                        adapter.remove(current_position);
                                    }
                                });
                            } else if (current_videoDir instanceof Video) {
                                ((Video) current_videoDir).setVideoDir(videoDir);
                                ((Video) current_videoDir).update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        adapter.remove(current_position);
                                        MyUtils.showToast(getActivity(), "移动成功");
                                    }
                                });
                            }
                        }
                    }
//                    Log.e(TAG,videoDir.toString());
                    break;
                case SELECT_USER:
                    if (data != null) {
                        List<MyUser> userList = (List<MyUser>) data.getSerializableExtra("userList");
                        if (userList != null && userList.size() > 0) {
                            for (MyUser user : userList) {
                                Log.e(TAG, "push" + userList.size());
                                push(user);
                            }
                        }
                        MyUser user = (MyUser) data.getSerializableExtra("user");
                        if (user != null) {
                            push(user);
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
                    MyUtils.showToast(getActivity(), "推送成功");
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void findViews() {
        ll_main = findView(R.id.ll_main);
        ll_user = findView(R.id.ll_user);
        findView(R.id.ll_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取公开课
                Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                intent.putExtra("isOpen", true);
                startActivity(intent);
            }
        });
        findView(R.id.ll_subscribe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取订阅课
//                Intent intent = new Intent(getActivity(), VideoDedailActivity.class);
//                intent.putExtra("isSubscribe", true);
//                intent.putExtra("typeId", classRoom.getTypeId());
//                intent.putExtra("title", "订阅课");
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                intent.putExtra("isDingyue", true);
                intent.putExtra("isSelect", false);
                intent.putExtra("isFirst", true);
                startActivity(intent);
            }
        });
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            if (userInfo.getUserType() == 0) {
                ll_user.setVisibility(View.VISIBLE);
                ll_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //用户关系admin
                        Intent intent = new Intent(getActivity(), UserAdminActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                ll_user.setVisibility(View.GONE);
            }
        } else {
//            MyUtils.showToast(getActivity(),"当前用户未登陆");
            Log.e(TAG, "当前用户未登陆");
        }
        btn_channel_creat = findView(R.id.btn_channel_creat);
        tv_add_video_dir = findView(R.id.tv_add_video_dir);
        gridView = findView(R.id.gridview);
        btn_channel_creat.setOnClickListener(this);
        tv_add_video_dir.setOnClickListener(this);
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        bitmapUtils = new BitmapUtils(getActivity());
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
        recyclerView = findView(R.id.recycler_view);
        adapter = new VideoDirAdapter(bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        Log.e(TAG, adapter.toString());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<VideoDirAdapter>() {
            @Override
            public void onItemClick(VideoDirAdapter adapter, View view, int position) {
                final Object videoDir = adapter.getItem(position);
                if (videoDir instanceof VideoDir) {
                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                    intent.putExtra("parent", (VideoDir) videoDir);
                    intent.putExtra("isSelect", false);
                    startActivity(intent);
                } else if (videoDir instanceof Video) {
                    startVideo((Video) videoDir);
                }
//                startVideo(video.getOrigUrl());
            }

            @Override
            public void onItemLongClick(VideoDirAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                final Object videoDir = adapter.getItem(position);
                current_videoDir = videoDir;
                current_position = position;
                showSelectDialog(videoDir);
//                if (videoDir instanceof VideoDir) {
////                    Intent intent = new Intent(getActivity(), VideoDirActivity.class);
////                    intent.putExtra("parent", (VideoDir) videoDir);
////                    getActivity().startActivity(intent);
//                } else if (videoDir instanceof Video) {
//                    startVideo((Video) videoDir);
//                }
            }
        });
    }

    /**
     * 同步网易用户视频数据到bmob
     */
    private void initVideo(ClassRoom classRoom) {
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null && userInfo.getUserType() == 0) {
            // 允许用户使用应用
            typeId = classRoom.getTypeId();
            if (typeId != null) {
                EduChatRoomHttpClient.getInstance().getVideoList(Integer.valueOf(typeId),10, new ChatRoomHttpClient.ChatRoomHttpCallback<VideoListResult.RetBean>() {
                    @Override
                    public void onSuccess(final VideoListResult.RetBean listBeen) {
                        final List<VideoListResult.RetBean.ListBean> videos = listBeen.getList();
                        Log.e(TAG, "videos" + videos.size()+listBeen.getTotalRecords());
                        final int wy_totalRecords=listBeen.getTotalRecords();
                        BmobQuery<Video> query = new BmobQuery<Video>();
                        query.addWhereEqualTo("typeId", typeId);
                        query.count(Video.class, new CountListener() {
                            @Override
                            public void done(Integer integer, BmobException e) {
                                Log.e(TAG,"list:"+integer+"videos"+wy_totalRecords);
                                if (integer != wy_totalRecords && integer < wy_totalRecords) {
                                    //更新同步数据
                                    EduChatRoomHttpClient.getInstance().getVideoList(Integer.valueOf(typeId), wy_totalRecords - integer, new ChatRoomHttpClient.ChatRoomHttpCallback<VideoListResult.RetBean>() {
                                        @Override
                                        public void onSuccess(VideoListResult.RetBean retBean) {
                                            List<BmobObject> temps = new ArrayList<BmobObject>();
                                            for (VideoListResult.RetBean.ListBean videoBean:retBean.getList()){
                                                final Video video = new Video();
                                                video.setVideoName(videoBean.getVideoName());
                                                video.setDescription(videoBean.getDescription());
                                                video.setDownloadOrigUrl(videoBean.getDownloadOrigUrl());
                                                video.setOrigUrl(videoBean.getOrigUrl());
                                                video.setSnapshotUrl(videoBean.getSnapshotUrl());
                                                video.setTypeId(typeId);
                                                video.setVid(videoBean.getVid()+"");
                                                temps.add(video);
                                            }
//                                            for (int i = 0; i < (videos.size() - integer); i++) {
//                                                final Video video = new Video();
//                                                video.setVideoName(videos.get(i).getVideoName());
//                                                video.setDescription(videos.get(i).getDescription());
//                                                video.setDownloadOrigUrl(videos.get(i).getDownloadOrigUrl());
//                                                video.setOrigUrl(videos.get(i).getOrigUrl());
//                                                video.setSnapshotUrl(videos.get(i).getSnapshotUrl());
//                                                video.setTypeId(typeId);
//                                                video.setVid(videos.get(i).getVid()+"");
//                                                temps.add(video);
//                                            }
                                            //第二种方式：v3.5.0开始提供
                                            new BmobBatch().insertBatch(temps).doBatch(new QueryListListener<BatchResult>() {

                                                @Override
                                                public void done(List<BatchResult> o, BmobException e) {
                                                    if(e==null){
                                                        for(int i=0;i<o.size();i++){
                                                            BatchResult result = o.get(i);
                                                            BmobException ex =result.getError();
                                                            if(ex==null){
                                                                Log.e(TAG,"第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                                            }else{
                                                                Log.e(TAG,"第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                                            }
                                                        }
                                                    }else{
                                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(int code, String errorMsg) {

                                        }
                                    });

//                                    new BmobBatch().updateBatch(temps).doBatch(new QueryListListener<BatchResult>() {
//
//                                        @Override
//                                        public void done(List<BatchResult> o, BmobException e) {
//                                            if(e==null){
//                                                for(int i=0;i<o.size();i++){
//                                                    BatchResult result = o.get(i);
//                                                    BmobException ex =result.getError();
//                                                    if(ex==null){
//                                                        Log.e(TAG,"第"+i+"个数据批量更新成功："+result.getUpdatedAt());
//                                                    }else{
//                                                        Log.e(TAG,"第"+i+"个数据批量更新失败："+ex.getMessage()+","+ex.getErrorCode());
//                                                    }
//                                                }
//                                            }else{
//                                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                                            }
//                                        }
//                                    });
                                }
                            }
                        });
//                        query.findObjects(new FindListener<Video>() {
//                            @Override
//                            public void done(List<Video> list, BmobException e) {
//
//                            }
//                        });
                    }

                    @Override
                    public void onFailed(int code, String errorMsg) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), code + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                MyUtils.showToast(getActivity(), "获取课程失败");
            }
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(getActivity(), "当前用户尚未登录");
        }
    }

    /**
     * 获取要删除文件夹下面的内容
     */
    private void getVideoDirData(final VideoDir videoDir) {
        allVideoDirLists.clear();
        //获取目录数据
        BmobQuery<VideoDir> query = new BmobQuery<>();
        query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
        query.addWhereEqualTo("parent", videoDir);
        query.order("-createdAt");
        query.findObjects(new FindListener<VideoDir>() {
            @Override
            public void done(List<VideoDir> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    if (list.size() < 50) {
                        List<BmobObject> deleteLists = new ArrayList<BmobObject>();
                        deleteLists.addAll(list);
                        new BmobBatch().deleteBatch(deleteLists).doBatch(new QueryListListener<BatchResult>() {

                            @Override
                            public void done(List<BatchResult> o, BmobException e) {
                                if (e == null) {

                                } else {
                                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                }
                            }
                        });
                    }
                    getDeleteVideo(videoDir);
                } else {
                    getDeleteVideo(videoDir);
                }
            }
        });

    }

    private void fetchData() {
        initVideo(classRoom);
        allLists.clear();
        //获取一级目录结构
        BmobQuery<VideoDir> query = new BmobQuery<>();
        query.addWhereEqualTo("creatAccount", DemoCache.getAccount());
        query.addWhereDoesNotExists("parent");
        query.addWhereEqualTo("isDingYue", false);
        query.order("-createdAt");
        query.findObjects(new FindListener<VideoDir>() {
            @Override
            public void done(List<VideoDir> list, BmobException e) {
                if (e == null) {
//                    onFetchDataDone(true, list);
                    if (list != null && list.size() > 0) {
                        allLists.addAll(list);
                        getVideo();
                    } else {
                        getVideo();
                    }
                } else {
                    Log.e(TAG, "getVideo2");
                    getVideo();
//                    onFetchDataDone(false, null);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "您尚未建立目录", Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
//        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//        if (userInfo != null) {
//            // 允许用户使用应用
//            String typeId = classRoom.getTypeId();
//            if (typeId != null) {
//                EduChatRoomHttpClient.getInstance().getVideoList(Integer.valueOf(typeId), new ChatRoomHttpClient.ChatRoomHttpCallback<List<VideoListResult.RetBean.ListBean>>() {
//                    @Override
//                    public void onSuccess(List<VideoListResult.RetBean.ListBean> listBeen) {
//                        List<VideoListResult.RetBean.ListBean> videos = listBeen;
//                        onFetchDataDone(true, videos);
//                    }
//
//                    @Override
//                    public void onFailed(int code, String errorMsg) {
//                        onFetchDataDone(false, null);
//                        if (getActivity() != null) {
//                            Toast.makeText(getActivity(), code + errorMsg, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            } else {
//                MyUtils.showToast(getActivity(), "获取课程失败");
//            }
//        } else {
//            //缓存用户对象为空时， 可打开用户注册界面…
//            MyUtils.showToast(getActivity(), "当前用户尚未登录");
//        }
    }

    private void getVideo() {
        BmobQuery<Video> queryVideo = new BmobQuery<Video>();
        queryVideo.addWhereDoesNotExists("videoDir");
        if (classRoom != null) {
            queryVideo.addWhereEqualTo("typeId", classRoom.getTypeId());
        }
        queryVideo.addWhereEqualTo("isOpen", false);
        queryVideo.order("-updatedAt");
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
                    }
//                        MyUtils.showToast(VideoDedailActivity.this, "该分类暂无数据");
                }
            }
        });
    }

    /**
     * 获取要删除的文件夹下的视频数据
     */
    private void getDeleteVideo(VideoDir videoDir) {
        BmobQuery<Video> queryVideo = new BmobQuery<Video>();
        queryVideo.addWhereEqualTo("videoDir", videoDir);
        queryVideo.setLimit(50);
        queryVideo.order("-updatedAt");
        queryVideo.findObjects(new FindListener<Video>() {
            @Override
            public void done(List<Video> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    if (list.size() < 50) {
                        List<BmobObject> deleteLists = new ArrayList<BmobObject>();
                        deleteLists.addAll(list);
                        new BmobBatch().deleteBatch(deleteLists).doBatch(new QueryListListener<BatchResult>() {

                            @Override
                            public void done(List<BatchResult> o, BmobException e) {
                                if (e == null) {
//                                    MyUtils.showToast(getActivity(),"删除成功");
                                } else {
                                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                }
                            }
                        });
                    }
                } else {

                }
            }
        });
    }

    private void startVideo(Video video) {
        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
        intent.putExtra("media_type", "videoondemand");
        intent.putExtra("decode_type", "software");
        intent.putExtra("videoPath", video.getOrigUrl());
        startActivity(intent);
    }

    private void onFetchDataDone(final boolean success, final List<Object> data) {
        Activity context = getActivity();
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false); // 刷新结束

                    if (success) {
                        adapter.setNewData(data); // 刷新数据源
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

    // 进入房间
    private void enterRoom(String name, String rtmpPullUrl) {
        ChatRoomActivity.start(getActivity(), name, true, rtmpPullUrl);
    }

    // 创建房间
    private void createRoom(String name, final String rtmpPullUrl) {
        EduChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), name, new EduChatRoomHttpClient.ChatRoomHttpCallback<String>() {
            @Override
            public void onSuccess(final String s) {
                Log.e(TAG, s);
                //更新bmob数据库
                classRoom.setRoomId(s);
                classRoom.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ChatRoomActivity.start(getActivity(), s, true, rtmpPullUrl);
                        } else {
                            MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_channel_creat:
                showAddDialog();
                break;
            case R.id.tv_add_video_dir:
                showAddVideoDir();
                break;
        }
    }

    /**
     * 修改文件夹对话框
     *
     * @param videoDir
     */
    public void showDelete(final Object videoDir) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
                                MyUtils.showToast(getActivity(), "删除成功");
                                adapter.remove(current_position);
                                dialog.dismiss();
                            } else {
                                MyUtils.showToast(getActivity(), "删除失败");
                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                            }
                        }
                    });

                } else if (videoDir instanceof Video) {
                    ((Video) videoDir).delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                adapter.remove(current_position);
                                MyUtils.showToast(getActivity(), "删除成功");
                                dialog.dismiss();
                            } else {
                                MyUtils.showToast(getActivity(), "删除失败");
                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                            }
                        }
                    });
                    if (BmobUser.getCurrentUser(MyUser.class).getUserType() == 0) {
                        ((Video) videoDir).delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    //删除网易云的视频文件
                                    EduChatRoomHttpClient.getInstance().deleteVideoList(Integer.valueOf(((Video) videoDir).getVid()), new ChatRoomHttpClient.ChatRoomHttpCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            adapter.remove(current_position);
                                            MyUtils.showToast(getActivity(), "删除成功");
                                        }
                                        @Override
                                        public void onFailed(int code, String errorMsg) {
                                            MyUtils.showToast(getActivity(), "删除失败");
                                            LogUtil.e(TAG, errorMsg);
                                        }
                                    });
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(getActivity(), "删除失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    }else {
                        MyUtils.showToast(getActivity(),"您无权删除该视频文件");
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_edit_video_dir, null);
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
                                    MyUtils.showToast(getActivity(), "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(getActivity(), "修改失败");
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
                                    MyUtils.showToast(getActivity(), "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(getActivity(), "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    MyUtils.showToast(getActivity(), "名称不能为空");
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_add_video_dir, null);
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
                    videoDir.setCreatAccount(DemoCache.getAccount());
                    videoDir.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                MyUtils.showToast(getActivity(), "添加成功");
                                adapter.addData(0, videoDir);
                                Log.e(TAG, adapter.toString());
                                dialog.dismiss();
                            }
                        }
                    });
                } else {
                    MyUtils.showToast(getActivity(), "名称不能为空");
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
     * 创建房间对话框
     */
    public void showAddDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_add_class_room, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_tag = (EditText) dView.findViewById(R.id.et_name);
        final EditText et_pwd = (EditText) dView.findViewById(R.id.et_pwd);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_tag.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(pwd)) {
                        EduChatRoomHttpClient.getInstance().createClassRoom(name, new EduChatRoomHttpClient.ChatRoomHttpCallback<ChannelCreat.RetBean>() {
                            @Override
                            public void onSuccess(final ChannelCreat.RetBean retBean) {
                                //创建视频分类，用于管理视频
                                EduChatRoomHttpClient.getInstance().createType(name, DemoCache.getAccount(), new ChatRoomHttpClient.ChatRoomHttpCallback<String>() {
                                    @Override
                                    public void onSuccess(String typeId) {
                                        //保存到Bmob应用数据库
                                        ClassRoom classRoom = new ClassRoom();
                                        classRoom.setName(retBean.getName());
                                        classRoom.setTypeId(typeId);
                                        classRoom.setAccount(DemoCache.getAccount());
                                        classRoom.setCid(retBean.getCid());
                                        classRoom.setHlsPullUrl(retBean.getHlsPullUrl());
                                        classRoom.setHttpPullUrl(retBean.getHttpPullUrl());
                                        classRoom.setRtmpPullUrl(retBean.getRtmpPullUrl());
                                        classRoom.setPushUrl(retBean.getPushUrl());
                                        classRoom.setOnline(false);
                                        classRoom.setRoomPwd(pwd);
                                        classRoom.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null) {
                                                    MyUtils.showToast(getActivity(), "创建成功");
                                                    initData();
                                                } else {
                                                    MyUtils.showToast(getActivity(), "创建失败Bmob" + e.getErrorCode() + e.getMessage());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(int code, String errorMsg) {

                                    }
                                });
                            }

                            @Override
                            public void onFailed(int code, String errorMsg) {
                                MyUtils.showToast(getActivity(), "创建失败Nim" + code + errorMsg);
                            }
                        });
                        dialog.dismiss();
                    } else {
                        MyUtils.showToast(getActivity(), "请输入房间密码");
                    }
                } else {
                    MyUtils.showToast(getActivity(), "名称不能为空");
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
     * 显示操作对话框
     *
     * @param videoDir
     */
    private void showSelectDialog(final Object videoDir) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        if (videoDir instanceof Video) {
            builder.setItems(new String[]{"移动到", "推送给", "修改", "删除"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                            intent.putExtra("isSelect", true);
                            intent.putExtra("isFirst", true);
                            if (videoDir instanceof VideoDir) {
                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                            }
                            startActivityForResult(intent, SELECT_VIDEO_DIR);
                            break;
                        case 1:
                            Intent intent1 = new Intent(getActivity(), UserSelectActivity.class);
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
        } else {
            builder.setItems(new String[]{"移动到", "修改", "删除"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(getActivity(), VideoDirActivity.class);
                            intent.putExtra("isSelect", true);
                            intent.putExtra("isFirst", true);
                            if (videoDir instanceof VideoDir) {
                                intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
                            }
                            startActivityForResult(intent, SELECT_VIDEO_DIR);
                            break;
                        case 1:
                            showEditVideoDir(videoDir);
                            break;
                        case 2:
                            showDelete(videoDir);
                            break;
                    }
                }
            });
        }
        builder.show();
    }

    /**
     * 分类对话框
     *
     * @param video
     */
    private void showClassifyDialog(final VideoListResult.RetBean.ListBean video) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_type_video, null);
        final android.app.AlertDialog dialog = builder.create();
        spinner = (Spinner) dView.findViewById(R.id.spinner);
        final BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("userType", 2);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    userList = list;
                    userData = new String[list.size() + 1];
                    userData[0] = "公开课";
                    for (int i = 0; i < list.size(); i++) {
                        userData[i + 1] = list.get(i).getNick();
                    }
                    initSpinner(spinner, userData);
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isOpen = true;
                } else {
                    isOpen = false;
                    current_user = userList.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyUser user = current_user;
                BmobQuery<Video> queryVideo = new BmobQuery<Video>();
                queryVideo.addWhereEqualTo("origUrl", video.getOrigUrl());
                queryVideo.findObjects(new FindListener<Video>() {
                    @Override
                    public void done(List<Video> list, BmobException e) {
                        if (e == null && list != null) {
                            if (list.size() > 0) {
                                Video video1 = list.get(0);
                                if (!video1.isOpen()) {
                                    video1.setOpen(isOpen);
                                }
                                if (!isOpen) {
                                    BmobRelation relation = new BmobRelation();
                                    relation.add(user);
                                    video1.setLikes(relation);
                                }
                                video1.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            MyUtils.showToast(getActivity(), "推送成功");
                                        } else {
                                            MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                saveVideo(video, user);
                            }
                        } else {
                            if (e.getErrorCode() == 101) {
                                saveVideo(video, user);
                            } else {
                                MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                            }
                        }
                    }
                });
                dialog.dismiss();
            }
        });

    }

    /**
     * 保存到bmob服务器
     *
     * @param video
     * @param user
     */
    private void saveVideo(VideoListResult.RetBean.ListBean video, MyUser user) {
        Video myVideo = new Video();
        myVideo.setVideoName(video.getVideoName());
        myVideo.setSnapshotUrl(video.getSnapshotUrl());
        myVideo.setOrigUrl(video.getOrigUrl());
        myVideo.setDownloadOrigUrl(video.getDownloadOrigUrl());
        myVideo.setDescription(video.getDescription());
        myVideo.setOpen(isOpen);
        if (!isOpen) {
            BmobRelation relation = new BmobRelation();
//将当前用户添加到多对多关联中
            relation.add(user);
//多对多关联指向`post`的`likes`字段
            myVideo.setLikes(relation);
        }
        myVideo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.e(TAG, "保存成功");
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private String[] userData;
    private ArrayAdapter<String> spinner_adapter;

    private void initSpinner(Spinner spinner, String[] m) {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, m);

        //设置下拉列表的风格
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }
}
