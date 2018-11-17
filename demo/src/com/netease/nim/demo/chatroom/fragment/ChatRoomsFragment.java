package com.netease.nim.demo.chatroom.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.activity.LeanRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomsAdapter;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.fragment.TFragment;
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
    private GridLayoutManager gridLayoutManager;

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
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
////                int type=recyclerView.getAdapter().getItemViewType(position);
//                if(adapter.getData().size()-1==position){
//                    return gridLayoutManager.getSpanCount();
//                }else {
//                    return 1;
//                }
//
//            }
//        });
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<ChatRoomsAdapter>() {
            @Override
            public void onItemClick(ChatRoomsAdapter adapter, View view, int position) {
                final ClassRoom room = adapter.getItem(position);
//                Log.e(TAG,room.getRtmpPullUrl());
//                if(adapter.getData().size()-1==position){
////                    startActivity(new Intent(getActivity(),LeanRoomActivity.class));
//                    showRoomPwd(room);
//                }else {
//                }
                showRoomPwd(room);
            }
        });
        fetchData();
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
//        BmobQuery<ClassRoom> query = new BmobQuery<>();
//        query.addWhereEqualTo("isOnline", true);
//        query.order("-updatedAt");
//        query.findObjects(new FindListener<ClassRoom>() {
//            @Override
//            public void done(List<ClassRoom> list, BmobException e) {
//                if (e == null) {
//                    if (list != null && list.size() > 0) {
//                        onFetchDataDone(true, list);
//                    }
//                } else {
////                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
    }

    private void onFetchDataDone(final boolean success, final List<ClassRoom> data) {
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
//        NimUIKit.startP2PSession(getActivity(), "15235615532");
        ChatRoomActivity.start(getActivity(), name, true, rtmpPullUrl);
//        LeanChatRoomActivity.start(getActivity(), name, true, rtmpPullUrl);
    }
    /**
     * 输入房间密码
     * @param room
     */
    public void showRoomPwd(final ClassRoom room) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_room_pwd, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_pwd = (EditText) dView.findViewById(R.id.et_pwd);
//        final CheckBox cb_remember_pwd= (CheckBox) dView.findViewById(R.id.cb_remember_pwd);
//        cb_remember_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    SharedPreferencesUtils.set
//                }else {
//
//                }
//            }
//        });
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pwd = et_pwd.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)) {
                    if(pwd.equals(room.getRoomPwd())){
                        if(room.getRoomId().equals("8000428")){
                            //自习室
                            startActivity(new Intent(getActivity(),LeanRoomActivity.class));
                        }else {
                            //直播间
                            enterRoom(room.getRoomId(), room.getRtmpPullUrl());
                        }
                        dialog.dismiss();
                    }else {
                        MyUtils.showToast(getActivity(), "密码输入错误");
                    }
                } else {
                    MyUtils.showToast(getActivity(), "房间密码不能为空");
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
}
