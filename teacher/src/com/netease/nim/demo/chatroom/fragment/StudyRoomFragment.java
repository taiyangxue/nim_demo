package com.netease.nim.demo.chatroom.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.adapter.ChatRoomTabPagerAdapter;
import com.netease.nim.demo.chatroom.fragment.tab.ChatRoomTabFragment;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 聊天室顶层fragment
 * Created by hzxuwen on 2015/12/14.
 * ViewPager.OnPageChangeListener, AVChatStateObserver, View.OnClickListener
 */
public class StudyRoomFragment extends ChatRoomTabFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final String TAG = StudyRoomFragment.class.getSimpleName();
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private GridView gridview;
    private ArrayAdapter<String> array_adapter;
    private ChatRoomTabPagerAdapter adapter;
    private int scrollState;
    private TextView tv_stop;
    private ClassRoom classRoom;
    private TextView tv_start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initFragment();
    }

    @Override
    protected void onInit() {
        //接收MainActivity传过来的参数
//        initFragment();
    }


    public void initFragment(String rtmpPullUrl) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_room_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        setupPager();
        setupTabs();
    }

    public void updateOnlineStatus(boolean isOnline) {
//        statusText.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    public void updateView() {
//        ChatRoomHelper.setCoverImage(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId(), imageView);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        classRoom.setOnline(false);
        classRoom.update(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stop:
                classRoom.setOnline(false);
                classRoom.update(new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            MyUtils.showToast(getActivity(), "下课通知已发出");
                            // 创建文本消息
                            ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                                    classRoom.getRoomId(), // 聊天室id
                                    "000000X" // 文本内容
                            );
                            NIMClient.getService(ChatRoomService.class).sendMessage(message, false);
                        } else {
                            MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                        }
                    }
                });
                break;
            case R.id.tv_start:
                classRoom.setOnline(true);
                classRoom.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            MyUtils.showToast(getActivity(), "上课通知已发出");
                            ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                                    classRoom.getRoomId(), // 聊天室id
                                    "000000Y" // 文本内容
                            );
                            NIMClient.getService(ChatRoomService.class).sendMessage(message, false);
                        } else {
                            MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                        }
                    }
                });
                break;
            case R.id.back_arrow:
                getActivity().finish();
                break;
        }
    }

    private void findViews() {
        initRoom();
        tabs = findView(R.id.chat_room_tabs);
        viewPager = findView(R.id.chat_room_viewpager);
        gridview = findView(R.id.gridView);
        tv_stop = findView(R.id.tv_stop);
        tv_stop.setOnClickListener(this);
        tv_start = findView(R.id.tv_start);
        tv_start.setOnClickListener(this);
        findView(R.id.back_arrow).setOnClickListener(this);
//        findView(share_btn).setOnClickListener(this);
        String[] adapterData = new String[80];
        for (int i = 0; i < 80; i++) {
            adapterData[i] = String.valueOf(i + 1);
        }
        array_adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_simple, adapterData);
        gridview.setAdapter(array_adapter);
    }

    private void initRoom() {
        BmobQuery<ClassRoom> query = new BmobQuery<>();
        query.addWhereEqualTo("account", DemoCache.getAccount());
        query.findObjects(new FindListener<ClassRoom>() {
            @Override
            public void done(List<ClassRoom> list, BmobException e) {
                if (e == null) {
                    classRoom = list.get(0);
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void setupPager() {
        // CACHE COUNT
        adapter = new ChatRoomTabPagerAdapter(getFragmentManager(), getActivity(), viewPager);
        viewPager.setOffscreenPageLimit(adapter.getCacheCount());
        // page swtich animation
        viewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        // ADAPTER
        viewPager.setAdapter(adapter);
        // TAKE OVER CHANGE
        viewPager.setOnPageChangeListener(this);
    }

    private void setupTabs() {
        tabs.setOnCustomTabListener(new PagerSlidingTabStrip.OnCustomTabListener() {
            @Override
            public int getTabLayoutResId(int position) {
                return R.layout.chat_room_tab_layout;
            }

            @Override
            public boolean screenAdaptation() {
                return true;
            }
        });
        tabs.setViewPager(viewPager);
        tabs.setOnTabClickListener(adapter);
        tabs.setOnTabDoubleTapListener(adapter);
    }

    /********************
     * OnPageChangeListener
     **************************/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // TO TABS
        tabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
        // TO ADAPTER
        adapter.onPageScrolled(position);
    }

    @Override
    public void onPageSelected(int position) {
        // TO TABS
        tabs.onPageSelected(position);

        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // TO TABS
        tabs.onPageScrollStateChanged(state);

        scrollState = state;

        selectPage(viewPager.getCurrentItem());
    }

    private void selectPage(int page) {
        // TO PAGE
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            adapter.onPageSelected(viewPager.getCurrentItem());
        }
    }

}
