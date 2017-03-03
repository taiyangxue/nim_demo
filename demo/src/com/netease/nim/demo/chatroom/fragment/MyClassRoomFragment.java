package com.netease.nim.demo.chatroom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.demo.common.entity.Bmob.ClassRoom;
import com.netease.nim.demo.common.entity.ChannelCreat;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.main.fragment.MainTabFragment;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;



/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyClassRoomFragment extends MainTabFragment implements View.OnClickListener {
    private static final String TAG = MyClassRoomFragment.class.getSimpleName();
    private Gson gson;
    /**
     * 6.0权限处理
     **/
    private boolean bPermission = false;
    private TextView tv_name;
    private EditText et_push_id;
    private Button btn_channel_creat;
    private LinearLayout ll_main;
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.home_cuoti, R.drawable.home_yuyue};
    private String[] iconName = {"开始直播", "房间信息"};
    private ClassRoom classRoom;

    public MyClassRoomFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }
    @Override
    protected void onInit() {
//        fragment = (MyClassRoomFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.my_homes_fragment);
//        initData();
    }

    private void initData() {
        BmobQuery<ClassRoom> query=new BmobQuery<>();
        query.addWhereEqualTo("account", DemoCache.getAccount());
        query.findObjects(new FindListener<ClassRoom>() {
            @Override
            public void done(List<ClassRoom> list, BmobException e) {
                if(e==null){
                    if (list.size()>0&&list.get(0)!=null) {
                        btn_channel_creat.setVisibility(View.GONE);
                        ll_main.setVisibility(View.VISIBLE);
                        classRoom = list.get(0);
                        tv_name.setText(classRoom.getName());
                        et_push_id.setText(classRoom.getPushUrl());
                    }else {
                        ll_main.setVisibility(View.GONE);
                        btn_channel_creat.setVisibility(View.VISIBLE);
                        MyUtils.showToast(getActivity(),"您尚未创建自己的教室");
                    }
                }else {
                    MyUtils.showToast(getActivity(),e.getErrorCode()+e.getMessage());
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

                switch (position){
                    case 0:
                        if(classRoom!=null){
                            createRoom(classRoom.getName(),classRoom.getRtmpPullUrl());
                        }else {
                            MyUtils.showToast(getActivity(),"直播间信息正在加载");
                        }
                        break;
                    case 1:
                        break;
                }
            }
        });

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
    private void findViews() {
        ll_main = findView(R.id.ll_main);
        tv_name = findView(R.id.tv_name);
        et_push_id = findView(R.id.et_push_id);
        btn_channel_creat = findView(R.id.btn_channel_creat);
        gridView = findView(R.id.gridview);
        btn_channel_creat.setOnClickListener(this);
    }
    // 创建房间
    private void createRoom(String name, final String rtmpPullUrl) {
        EduChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), name, new EduChatRoomHttpClient.ChatRoomHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ChatRoomActivity.start(getActivity(), s, true, rtmpPullUrl);
                //更新bmob数据库
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
        }
    }
    /**
     * 搜索对话框
     */
    public void showAddDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_add_class_room, null);
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
                    EduChatRoomHttpClient.getInstance().createClassRoom(name, new EduChatRoomHttpClient.ChatRoomHttpCallback<ChannelCreat.RetBean>() {
                        @Override
                        public void onSuccess(ChannelCreat.RetBean retBean) {
                            //保存到Bmob应用数据库
                            ClassRoom classRoom=new ClassRoom();
                            classRoom.setName(retBean.getName());
                            classRoom.setAccount(DemoCache.getAccount());
                            classRoom.setCid(retBean.getCid());
                            classRoom.setHlsPullUrl(retBean.getHlsPullUrl());
                            classRoom.setHttpPullUrl(retBean.getHttpPullUrl());
                            classRoom.setRtmpPullUrl(retBean.getRtmpPullUrl());
                            classRoom.setPushUrl(retBean.getPushUrl());
                            classRoom.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e==null){
                                        MyUtils.showToast(getActivity(),"创建成功");
                                        initData();
                                    }else{
                                        MyUtils.showToast(getActivity(),"创建失败Bmob"+e.getErrorCode()+e.getMessage());
                                    }
                                }
                            });
                        }
                        @Override
                        public void onFailed(int code, String errorMsg) {
                            MyUtils.showToast(getActivity(),"创建失败Nim"+code+errorMsg);
                        }
                    });
                    dialog.dismiss();
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
