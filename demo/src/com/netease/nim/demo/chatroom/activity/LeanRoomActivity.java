package com.netease.nim.demo.chatroom.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.AVChatSoundPlayer;
import com.netease.nim.demo.chatroom.adapter.LeanRoomOnlinePeopleAdapter;
import com.netease.nim.demo.chatroom.adapter.LeanRoomOnlineTeacherAdapter;
import com.netease.nim.demo.chatroom.helper.ChatRoomMemberCache;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class LeanRoomActivity extends UI {
    @ViewInject(R.id.gridView)
    private GridView gridView;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.recycler_view_teacher)
    private RecyclerView recyclerView_teacher;
    private LeanRoomOnlinePeopleAdapter adapter;
    private LeanRoomOnlineTeacherAdapter mAdapter;
    private List<ChatRoomMember> items = new ArrayList<>();
    private List<ChatRoomMember> items_teacher = new ArrayList<>();
    private static final int LIMIT = 80;
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime
    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    private ArrayAdapter<String> array_adapter;

    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private static final String TAG = LeanRoomActivity.class.getSimpleName();
    private final static String KEY_SHARE_URL = "webUrl";
    /**
     * 聊天室基本信息
     */
//    private String roomId = "8000428";
    private String roomId = DemoCache.LEAN_ROOM_ID;
    private ChatRoomInfo roomInfo;
    private boolean hasEnterSuccess = false; // 是否已经成功登录聊天室
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    private MyUser current_teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lean_room);
        ViewUtils.inject(this);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "自习室";
        setToolBar(R.id.toolbar, options);
        registerObservers(true);
        initView();
        initData();
    }

    private void initData() {
        enterRoom();
        refreshData();
    }

    private void initView() {
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });

        // 学生recyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(3), ScreenUtil.dip2px(3), true));
//        clerView.addOnItemTouchListener(touchListener);
        adapter = new LeanRoomOnlinePeopleAdapter(recyclerView, items);
        recyclerView.setAdapter(adapter);
        //教师
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_teacher.setLayoutManager(linearLayoutManager);
        mAdapter = new LeanRoomOnlineTeacherAdapter(recyclerView_teacher);
        recyclerView_teacher.setAdapter(mAdapter);
        recyclerView_teacher.addOnItemTouchListener(new OnItemClickListener<LeanRoomOnlineTeacherAdapter>() {
            @Override
            public void onItemClick(LeanRoomOnlineTeacherAdapter adapter, View view, int position) {
                MyUser teacher = adapter.getItem(position);
                if (teacher.isOnline()) {
                    if (teacher.isFree()) {
                        //与老师视频连线或进入该老师的直播间
                        sendMessage(teacher.getUsername(), DemoCache.getAccount(), NimUserInfoCache.getInstance().getUserName(DemoCache.getAccount()));
//                        "{name:msg}"
                        current_teacher=teacher;
                    } else {
                        MyUtils.showToast(LeanRoomActivity.this, teacher.getNick() + "老师正忙，请稍后");
                    }
                } else {
                    MyUtils.showToast(LeanRoomActivity.this, teacher.getNick() + "老师尚未进入自习室，请稍后");
                }
            }
        });
    }

    /**
     * 进入教师的自习直播间
     *
     * @param teacher
     */
    private void enterTeacherRoom(MyUser teacher) {
        BmobQuery<ClassRoom> query = new BmobQuery<>();
        query.addWhereEqualTo("account", teacher.getUsername());
        query.findObjects(new FindListener<ClassRoom>() {
            @Override
            public void done(List<ClassRoom> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    ClassRoom room = list.get(0);
                    MyP2PMessageActivity.start(LeanRoomActivity.this,room.getAccount(),null,null,room.getRtmpPullUrl());
//                    ChatRoomActivity.start(LeanRoomActivity.this, room.getRoomId(), true, room.getRtmpPullUrl());
                } else {
                    MyUtils.showToast(LeanRoomActivity.this, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoutChatRoom();
        registerObservers(true);
    }

    @Override
    public void onBackPressed() {
        logoutChatRoom();
    }

    private void resetStatus() {
        updateTime = 0;
        enterTime = 0;
        isNormalEmpty = false;
    }

    private void clearCache() {
        resetStatus();
        adapter.clearData();
        memberCache.clear();
    }

    private void updateCache(List<ChatRoomMember> members) {
        if (members == null || members.isEmpty()) {
            return;
        }

        for (ChatRoomMember member : members) {
            LogUtil.e(TAG, member.getMemberType().getValue() + "");
            if (member.getMemberType() == MemberType.ADMIN || member.getMemberType() == MemberType.CREATOR) {
                items_teacher.add(member);
            } else {
                if (member.getMemberType() == MemberType.GUEST) {
                    enterTime = member.getEnterTime();
                } else {
                    updateTime = member.getUpdateTime();
                }

                if (memberCache.containsKey(member.getAccount())) {
                    items.remove(memberCache.get(member.getAccount()));
                }
                memberCache.put(member.getAccount(), member);
                items.add(member);
            }
        }
        Collections.sort(items, comp);
    }

    private void refreshData() {
        adapter.setEnableLoadMore(false);
        getData(true, new SimpleCallback<List<ChatRoomMember>>() {
            @Override
            public void onResult(final boolean success, final List<ChatRoomMember> result) {
                final Activity context = LeanRoomActivity.this;
                if (context == null) {
                    return;
                } else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 刷新结束
                            swipeRefreshLayout.setRefreshing(false);

                            if (success) {
                                clearCache();
//                                for(ChatRoomMember member:){
//
//                                }
                                updateCache(result);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
        initBmob();
    }

    private void getData(final boolean fetching, final SimpleCallback<List<ChatRoomMember>> callback) {
        // reset status
        if (fetching) {
            resetStatus();
        }

        // query type
        final MemberQueryType memberQueryType = isNormalEmpty ? MemberQueryType.GUEST : MemberQueryType.ONLINE_NORMAL;
        final long time = isNormalEmpty ? enterTime : updateTime;
        final int expectNum = LIMIT;
        final List<ChatRoomMember> resultList = new ArrayList<>();
        ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, memberQueryType, time, expectNum, new
                SimpleCallback<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(boolean success, List<ChatRoomMember> result) {
                        if (success) {
                            // 结果集
                            resultList.addAll(result);

                            // 固定成员已经拉完，不满预期数量，开始拉游客
                            if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < expectNum) {
                                isNormalEmpty = true;
                                final int expectNum2 = expectNum - result.size();
                                ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, MemberQueryType.GUEST, enterTime, expectNum2, new
                                        SimpleCallback<List<ChatRoomMember>>() {
                                            @Override
                                            public void onResult(boolean success, List<ChatRoomMember> result) {
                                                if (success) {
                                                    // 结果集
                                                    resultList.addAll(result);
                                                    callback.onResult(true, resultList);
                                                } else {
                                                    callback.onResult(false, null);
                                                }
                                            }
                                        });
                            } else {
                                // 固定成员拉取到位或者拉取游客成功
                                callback.onResult(true, resultList);
                            }
                        } else {
                            callback.onResult(false, null);
                        }
                    }
                });
    }

    private void enterRoom() {
        DialogMaker.showProgressDialog(this, null, "", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (enterRequest != null) {
                    enterRequest.abort();
                    onLoginDone();
                    finish();
                }
            }
        }).setCanceledOnTouchOutside(false);
        hasEnterSuccess = false;
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 1);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                roomInfo = result.getRoomInfo();
                Log.e(TAG, roomInfo.getRoomId());
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomMemberCache.getInstance().saveMyMember(member);
//                if(current_teacher!=null){
//
//                }
                initBmob();
                hasEnterSuccess = true;
            }

            @Override
            public void onFailed(int code) {
                // test
                LogUtil.ui("enter chat room failed, callback code=" + code);
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(LeanRoomActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    Toast.makeText(LeanRoomActivity.this, "聊天室不存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LeanRoomActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                Toast.makeText(LeanRoomActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
//    private List<MyUser> teachers;
    private void initBmob() {
        //获取教师列表数据
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("userType", 1);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    //设置适配器
                    Log.e(TAG, list.size() + "");
                    mAdapter.setNewData(list);
                } else {
                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        clearChatRoom();
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
        finish();
    }

    public void sendMessage(String teacherAccount, String mAccount, String content) {
        // 创建文本消息
//        IMMessage message = MessageBuilder.createTextMessage(
//                account, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
//                , // 聊天类型，单聊或群组
//                content // 文本内容
//        );
        // 创建文本消息
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                DemoCache.LEAN_ROOM_ID, // 聊天室id
                teacherAccount + "," + mAccount + "," + content // 文本内容
        );
// 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false);
    }

    /**
     * 聊天室监听
     *
     * @param register
     */
    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            if (messages.size() > 0) {
                ChatRoomMessage message = messages.get(0);
//                Log.e(TAG, "message" + messages.size() + "");

                if (!TextUtils.isEmpty(message.getContent())) {
                    Log.e(TAG, message.getContent());
                    final String[] messageString = message.getContent().split(",");
                    if (messageString[0].equals(DemoCache.getAccount())) {

                        switch (messageString[1]) {
                            case "001":
                                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
                                new AlertDialog.Builder(LeanRoomActivity.this).setTitle("提醒")//设置对话框标题

                                        .setMessage("老师拒绝了您发出的请求")//设置显示的内容

                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮

                                            @Override

                                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                                AVChatSoundPlayer.instance().stop();
                                            }

                                        }).show();//在按键响应事件中显示此对话框
                                //拒绝
                                break;
                            case "002":
                                //同意
                                if(current_teacher!=null){
                                    enterTeacherRoom(current_teacher);
                                }
                                break;

                        }
                    }else {
                        Log.e(TAG, "initBmob()"+message.getContent());
                    }
                    initBmob();
                }
            }

        }
    };
    private static Map<MemberType, Integer> compMap = new HashMap<>();

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    private static Comparator<ChatRoomMember> comp = new Comparator<ChatRoomMember>() {
        @Override
        public int compare(ChatRoomMember lhs, ChatRoomMember rhs) {
            if (lhs == null) {
                return 1;
            }

            if (rhs == null) {
                return -1;
            }
            return compMap.get(lhs.getMemberType()) - compMap.get(rhs.getMemberType());
        }
    };
}
