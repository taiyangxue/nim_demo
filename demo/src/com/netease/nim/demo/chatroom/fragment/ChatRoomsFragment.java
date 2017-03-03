package com.netease.nim.demo.chatroom.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomsAdapter;
import com.netease.nim.demo.chatroom.thridparty.ChatRoomHttpClient;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.demo.common.entity.ChannelListResult;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

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
                if(room.getStatus()==1||room.getStatus()==3){

                }else {
                    MyUtils.showToast(getActivity(),"当前教室未上课");
                }
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
        EduChatRoomHttpClient.getInstance().fetchChatRoomList(new ChatRoomHttpClient.ChatRoomHttpCallback<List<ChannelListResult.RetBean.ListBean>>() {
            @Override
            public void onSuccess(List<ChannelListResult.RetBean.ListBean> listBeen) {
                List<ChannelListResult.RetBean.ListBean> rooms = listBeen;
                onFetchDataDone(true, rooms);
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                onFetchDataDone(false, null);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "fetch chat room list failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private void createRoom(String name, final String rtmpPullUrl) {
        EduChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), name, new EduChatRoomHttpClient.ChatRoomHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ChatRoomActivity.start(getActivity(), s, true,rtmpPullUrl);
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }
//    // 进入房间
//    private void enterRoom(String name) {
//        ChatRoomActivity.start(getActivity(), name, false, rtmpPullUrl);
////        getActivity().finish();
//    }
//    private void createOrEnterRoom() {
//        DialogMaker.showProgressDialog(this, "", false);
//        if (isCreate) {
//            createRoom();
//        } else {
//            enterRoom();
//        }
//    }
}
