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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.AVChatSoundPlayer;
import com.netease.nim.demo.chatroom.adapter.LeanRoomOnlinePeopleAdapter;
import com.netease.nim.demo.chatroom.adapter.LeanRoomOnlineTeacherAdapter;
import com.netease.nim.demo.chatroom.helper.ChatRoomMemberCache;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
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
import com.netease.nimlib.sdk.chatroom.model.MemberOption;

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
import cn.bmob.v3.listener.UpdateListener;


public class LeanRoomActivity extends UI {
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.recycler_view_teacher)
    private RecyclerView recyclerView_teacher;
    @ViewInject(R.id.tb_isfree)
    private ToggleButton tb_isfree;

    @OnClick({R.id.tb_isfree})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tb_isfree:
                if (tb_isfree.isChecked()) {
                    current_teacher.setFree(true);
                    //空闲
                    sendMessage(DemoCache.getAccount()+",003");
                } else {
                    current_teacher.setFree(false);
                    //正忙
                    sendMessage(DemoCache.getAccount()+",004");
                }
                current_teacher.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtil.e(TAG, "bmob  更新状态成功");
                        } else {
                            LogUtil.e(TAG, "bmob" + e.getErrorCode() + e.getMessage());
                        }
                    }
                });
                break;
        }
    }

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
    private String roomId = "8000428";
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
//        String[] adapterData = new String[80];
//        for (int i = 0; i < 80; i++) {
//            adapterData[i] = String.valueOf(i + 1);
//        }
//        array_adapter = new ArrayAdapter<String>(this,
//                R.layout.item_simple, adapterData);
//        gridView.setAdapter(array_adapter);
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

    // 设置/取消管理员
    private void setAdmin(final ChatRoomMember member, boolean isAdmin) {
        NIMClient.getService(ChatRoomService.class)
                .markChatRoomManager(true, new MemberOption(roomId, "15235615533"))
                .setCallback(new RequestCallback<ChatRoomMember>() {
                    @Override
                    public void onSuccess(ChatRoomMember param) {
//                        Toast.makeText(getActivity(), R.string.set_success, Toast.LENGTH_SHORT).show();
//                        refreshList(param, member);
                        Toast.makeText(LeanRoomActivity.this, R.string.set_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e(TAG, "set admin failed:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.e(TAG, "set admin failed:" + exception);
                    }
                });
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
                setAdmin(member, false);
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

    private void initBmob() {
        //获取教师列表数据
        BmobQuery<MyUser> query = new BmobQuery<>();
//        query.addWhereEqualTo("userType", 1);
        query.addWhereLessThanOrEqualTo("userType", 1);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    //设置适配器
                    Log.e(TAG, list.size() + "");
                    for (MyUser user : list) {
                        if (user.getUsername().equals(DemoCache.getAccount())) {
                            current_teacher = user;
                            current_teacher.setOnline(true);
                            current_teacher.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        LogUtil.e(TAG, "bmob  进入房间");
                                    } else {
                                        LogUtil.e(TAG, "bmob" + e.getErrorCode() + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                    tb_isfree.setChecked(current_teacher.isFree());
                    list.remove(current_teacher);
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
        current_teacher.setOnline(false);
        current_teacher.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtil.e(TAG, "bmob  退出房间");
                } else {
                    LogUtil.e(TAG, "bmob" + e.getErrorCode() + e.getMessage());
                }
            }
        });
        finish();
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
//                Log.e(TAG,"message"+messages.size()+"");

                if (!TextUtils.isEmpty(message.getContent())&&message.getContent().contains(",")) {
                    Log.e(TAG, message.getContent());
                    final String[] messageString=message.getContent().split(",");
                    if(messageString[0].equals(DemoCache.getAccount())){
                        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);
                        new AlertDialog.Builder(LeanRoomActivity.this).setTitle("提醒")//设置对话框标题

                                .setMessage(messageString[2] + "发出单独辅导请求")//设置显示的内容
                                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AVChatSoundPlayer.instance().stop();
                                        sendMessage(messageString[1]+",001");
                                    }
                                })
                                .setPositiveButton("同意", new DialogInterface.OnClickListener() {//添加确定按钮

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                        AVChatSoundPlayer.instance().stop();
                                        sendMessage(messageString[1]+",002");
                                        tb_isfree.setChecked(false);
                                        current_teacher.setFree(false);
                                        current_teacher.update(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    LogUtil.e(TAG, "bmob  更新状态成功");
                                                } else {
                                                    LogUtil.e(TAG, "bmob" + e.getErrorCode() + e.getMessage());
                                                }
                                            }
                                        });
                                        //进入直播室
                                        MyP2PMessageActivity.start(LeanRoomActivity.this,messageString[1],null,null,"");
                                    }

                                }).show();//在按键响应事件中显示此对话框
                    }
                }
            }
        }
    };

    public void sendMessage(String content) {
        // 创建文本消息
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                DemoCache.LEAN_ROOM_ID, // 聊天室id
                content // 文本内容
        );
// 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false);
    }

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
