package com.netease.nim.demo.chatroom.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomsAdapter;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.demo.common.entity.ChannelListResult;
import com.netease.nim.demo.common.util.MyHttpClient;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class ChatRoomsFragment extends TFragment {
    private static final String TAG = ChatRoomsFragment.class.getSimpleName();

    private ChatRoomsAdapter adapter;
    private PullToRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Gson gson;
    private String decodeType = "software";  //解码类型，默认软件解码
    private String mediaType = "livestream"; //媒体类型，默认网络直播

    /**
     * 6.0权限处理
     **/
    private boolean bPermission = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_rooms, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        gson = new Gson();
    }

    public void onCurrent() {
        fetchData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        // swipeRefreshLayout
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
        recyclerView = findView(R.id.recycler_view);
        adapter = new ChatRoomsAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<ChatRoomsAdapter>() {
            @Override
            public void onItemClick(ChatRoomsAdapter adapter, View view, int position) {
                ChannelListResult.RetBean.ListBean room = adapter.getItem(position);
                createRoom("sun");
//                enterRoom("sun");
//                ChatRoomActivity.start(getActivity(), "123456");
//                String url = "https://vcloud.163.com/app/address";
//                String param = "{\"cid\":\"" + room.getCid() + "\"}";
//                if(room.getStatus()==1||room.getStatus()==3){
//                    new MyHttpClient(url, param) {
//                        @Override
//                        protected void onPostExecute(String s) {
//                            super.onPostExecute(s);
//                            Log.e(TAG, s);
//                            try {
//                                JSONObject jsonObj = new JSONObject(s);
//                                if (200 == jsonObj.getInt("code")) {
//                                    AddressResult addressResult = gson.fromJson(s, AddressResult.class);
//                                    Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
////                                String url = "rtmp://v2220e357.live.126.net/live/e1f3a464831c45b6bb3dd18d6a762993";
//                                    //把多个参数传给NEVideoPlayerActivity
//                                    intent.putExtra("media_type", mediaType);
//                                    intent.putExtra("decode_type", decodeType);
//                                    intent.putExtra("videoPath", addressResult.getRet().getRtmpPullUrl());
//                                    startActivity(intent);
//                                } else {
//                                    if (getActivity() != null) {
//                                        Toast.makeText(getActivity(), jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                Toast.makeText(getActivity(), "JSON解析异常", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }.execute();
//                }else {
//                    MyUtils.showToast(getActivity(),"当前教室未上课");
//                }
            }
        });
    }

    private final int WRITE_PERMISSION_REQ_CODE = 100;

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(getActivity(),
                        (String[]) permissions.toArray(new String[0]),
                        WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    private void fetchData() {
        String url = "https://vcloud.163.com/app/channellist";
        new MyHttpClient(url, "{\"records\":100, \"pnum\":1, \"ofield\": \"ctime\", \"sort\": 0}") {


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    if (200 == jsonObj.getInt("code")) {
                        ChannelListResult channelListResult = gson.fromJson(s, ChannelListResult.class);
                        List<ChannelListResult.RetBean.ListBean> rooms = channelListResult.getRet().getList();
//                        for(ChannelListResult.RetBean.ListBean room:rooms){
//                            if(room.getStatus()!=1){
//                                rooms.remove(room);
//                            }
//                        }
                        onFetchDataDone(true, rooms);
                    } else {
                        onFetchDataDone(false, null);
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "JSON解析异常", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

//        ChatRoomHttpClient.getInstance().fetchChatRoomList(new ChatRoomHttpClient.ChatRoomHttpCallback<List<ChatRoomInfo>>() {
//            @Override
//            public void onSuccess(List<ChatRoomInfo> rooms) {
//                onFetchDataDone(true, rooms);
//            }
//
//            @Override
//            public void onFailed(int code, String errorMsg) {
//                onFetchDataDone(false, null);
//                if (getActivity() != null) {
//                    Toast.makeText(getActivity(), "fetch chat room list failed, code=" + code, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void onFetchDataDone(final boolean success, final List<ChannelListResult.RetBean.ListBean> data) {
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
    // 创建房间
    private void createRoom(String name) {
        EduChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), name, new EduChatRoomHttpClient.ChatRoomHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                createChannel(s);
//                ChatRoomActivity.start(getActivity(), s, true);
//                getActivity().finish();
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }
    // 创建房间
//    private void createRoom() {
//        ChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), roomEdit.getText().toString(), new ChatRoomHttpClient.ChatRoomHttpCallback<String>() {
//            @Override
//            public void onSuccess(String s) {
//                createChannel(s);
//            }
//
//            @Override
//            public void onFailed(int code, String errorMsg) {
//                DialogMaker.dismissProgressDialog();
//                Toast.makeText(EnterRoomActivity.this, "创建房间失败, code:" + code + ", errorMsg:" + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * 创建会议频道
     */
    private void createChannel(final String roomId) {
        Log.e(TAG,roomId);
        AVChatManager.getInstance().createRoom(roomId, "avchat test", new AVChatCallback<AVChatChannelInfo>() {
            @Override
            public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                DialogMaker.dismissProgressDialog();
                ChatRoomActivity.start(getActivity(), roomId, true);
                getActivity().finish();
            }

            @Override
            public void onFailed(int i) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getActivity(), "创建频道失败, code:" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    // 进入房间
    private void enterRoom(String name) {
        ChatRoomActivity.start(getActivity(), name, false);
//        getActivity().finish();
    }
//    private void createOrEnterRoom() {
//        DialogMaker.showProgressDialog(this, "", false);
//        if (isCreate) {
//            createRoom();
//        } else {
//            enterRoom();
//        }
//    }
}
