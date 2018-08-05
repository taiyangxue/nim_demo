package com.netease.nim.demo.main.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.AVChatProfile;
import com.netease.nim.demo.avchat.activity.AVChatActivity;
import com.netease.nim.demo.chatroom.fragment.ChatRoomsFragment;
import com.netease.nim.demo.chatroom.helper.ChatRoomHelper;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.activity.AddFriendActivity;
import com.netease.nim.demo.home.activity.CollectionActivity;
import com.netease.nim.demo.home.activity.VideoDirActivity;
import com.netease.nim.demo.home.fragment.MyHomesNewFragment;
import com.netease.nim.demo.home.fragment.MyToolsFragment;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.demo.main.adapter.MyFragmentPagerAdapter;
import com.netease.nim.demo.main.fragment.MyErrorFragment;
import com.netease.nim.demo.main.fragment.SessionListFragment;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.team.TeamCreateHelper;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class MyMainActivity extends UI implements View.OnClickListener {
    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final String TAG = MyMainActivity.class.getSimpleName();
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    /**
     * 底部四个按钮
     */
    private LinearLayout mTabBtnWeixin;
    private LinearLayout mTabBtnFrd;
    private LinearLayout mTabBtnAddress;
    private LinearLayout mTabBtnSettings;
    private LinearLayout mTabBtnMine;
    private int currentIndex;
    private MyUser userInfo;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;
    private PopupWindow popupWindow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        userInfo = BmobUser.getCurrentUser(MyUser.class);
        requestBasicPermission();

        onParseIntent();

        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {

                syncPushNoDisturb(UserPreferences.getStatusConfig());

                DialogMaker.dismissProgressDialog();
            }
        });

        LogUtil.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MyMainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        } else {
            syncPushNoDisturb(UserPreferences.getStatusConfig());
        }

        onInit();

    }

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MyMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mTabBtnWeixin = (LinearLayout) findViewById(R.id.id_tab_bottom_weixin);
        mTabBtnFrd = (LinearLayout) findViewById(R.id.id_tab_bottom_friend);
        mTabBtnAddress = (LinearLayout) findViewById(R.id.id_tab_bottom_contact);
        mTabBtnSettings = (LinearLayout) findViewById(R.id.id_tab_bottom_setting);
        mTabBtnMine = (LinearLayout) findViewById(R.id.id_tab_bottom_mine);

        mTabBtnWeixin.setOnClickListener(this);
        mTabBtnFrd.setOnClickListener(this);
        mTabBtnAddress.setOnClickListener(this);
        mTabBtnSettings.setOnClickListener(this);
        mTabBtnMine.setOnClickListener(this);

        mFragments.add(new MyHomesNewFragment());
        mFragments.add(new MyToolsFragment());
        mFragments.add(new ChatRoomsFragment());
        mFragments.add(new MyErrorFragment());
        mFragments.add(new SessionListFragment());

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mAdapter.setFragmentList(mFragments);
        mViewPager.setAdapter(mAdapter);
//        mViewPager.setOffscreenPageLimit(5);
        ((ImageButton) mTabBtnWeixin
                .findViewById(R.id.btn_tab_bottom_weixin))
                .setImageResource(R.drawable.yidu_home);
        ((TextView) mTabBtnWeixin
                .findViewById(R.id.tv_xiaoxi))
                .setSelected(true);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                resetBtn();
                switch (position) {
                    case 0:
                        ((ImageButton) mTabBtnWeixin
                                .findViewById(R.id.btn_tab_bottom_weixin))
                                .setImageResource(R.drawable.yidu_home);
                        ((TextView) mTabBtnWeixin
                                .findViewById(R.id.tv_xiaoxi))
                                .setSelected(true);
                        break;
                    case 1:
                        ((ImageButton) mTabBtnAddress
                                .findViewById(R.id.faxian))
                                .setImageResource(R.drawable.yidu_kecheng);
                        ((TextView) mTabBtnAddress
                                .findViewById(R.id.tv_contact))
                                .setSelected(true);
                        break;
                    case 2:
                        ((ImageButton) mTabBtnSettings
                                .findViewById(R.id.btn_tab_bottom_setting))
                                .setImageResource(R.drawable.yidu_shipin);
                        ((TextView) mTabBtnSettings
                                .findViewById(R.id.tv_wo))
                                .setSelected(true);
                        break;
                    case 3:
                        ((ImageButton) mTabBtnFrd
                                .findViewById(R.id.btn_tab_bottom_friend))
                                .setImageResource(R.drawable.yidu_cuoti);
                        ((TextView) mTabBtnFrd
                                .findViewById(R.id.tv_friend))
                                .setSelected(true);
                        break;
                    case 4:
                        ((ImageButton) mTabBtnMine
                                .findViewById(R.id.btn_tab_bottom_mine))
                                .setImageResource(R.drawable.yidu_mine);
                        ((TextView) mTabBtnMine
                                .findViewById(R.id.tv_mine))
                                .setSelected(true);
                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn() {
        ((ImageButton) mTabBtnWeixin.findViewById(R.id.btn_tab_bottom_weixin))
                .setImageResource(R.drawable.yidu_home_n);
        ((TextView) mTabBtnWeixin
                .findViewById(R.id.tv_xiaoxi))
                .setSelected(false);
        ((ImageButton) mTabBtnAddress.findViewById(R.id.faxian))
                .setImageResource(R.drawable.yidu_kecheng_n);
        ((TextView) mTabBtnAddress
                .findViewById(R.id.tv_contact))
                .setSelected(false);
        ((ImageButton) mTabBtnSettings
                .findViewById(R.id.btn_tab_bottom_setting))
                .setImageResource(R.drawable.yidu_shipin_n);
        ((TextView) mTabBtnSettings
                .findViewById(R.id.tv_wo))
                .setSelected(false);
        ((ImageButton) mTabBtnFrd.findViewById(R.id.btn_tab_bottom_friend))
                .setImageResource(R.drawable.yidu_cuoti_n);
        ((TextView) mTabBtnFrd
                .findViewById(R.id.tv_friend))
                .setSelected(false);
        ((ImageButton) mTabBtnMine
                .findViewById(R.id.btn_tab_bottom_mine))
                .setImageResource(R.drawable.yidu_mine_n);
        ((TextView) mTabBtnMine
                .findViewById(R.id.tv_mine))
                .setSelected(false);
    }

    @Override
    public void onClick(View v) {
        // 重置按钮
        resetBtn();
        switch (v.getId()) {
            case R.id.id_tab_bottom_weixin:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageButton) mTabBtnWeixin
                        .findViewById(R.id.btn_tab_bottom_weixin))
                        .setImageResource(R.drawable.yidu_home);
                ((TextView) mTabBtnWeixin
                        .findViewById(R.id.tv_xiaoxi))
                        .setSelected(true);
                mViewPager.setCurrentItem(0);
                break;

            case R.id.id_tab_bottom_contact:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                ((ImageButton) mTabBtnAddress
                        .findViewById(R.id.faxian))
                        .setImageResource(R.drawable.yidu_kecheng);
                ((TextView) mTabBtnAddress
                        .findViewById(R.id.tv_contact))
                        .setSelected(true);
//                showPopupWindow((ImageButton) mTabBtnAddress
//                        .findViewById(R.id.faxian));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.id_tab_bottom_setting:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                ((ImageButton) mTabBtnSettings
                        .findViewById(R.id.btn_tab_bottom_setting))
                        .setImageResource(R.drawable.yidu_shipin);
                ((TextView) mTabBtnSettings
                        .findViewById(R.id.tv_wo))
                        .setSelected(true);
                mViewPager.setCurrentItem(2);
                break;
            case R.id.id_tab_bottom_friend:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageButton) mTabBtnFrd.findViewById(R.id.btn_tab_bottom_friend))
                        .setImageResource(R.drawable.yidu_cuoti);
                ((TextView) mTabBtnFrd
                        .findViewById(R.id.tv_friend))
                        .setSelected(true);
                mViewPager.setCurrentItem(3);
                break;
            case R.id.id_tab_bottom_mine:
                ((ImageButton) mTabBtnMine.findViewById(R.id.btn_tab_bottom_mine))
                        .setImageResource(R.drawable.yidu_mine);
                ((TextView) mTabBtnMine
                        .findViewById(R.id.tv_mine))
                        .setSelected(true);
                showPopupWindow2((ImageButton) mTabBtnMine.findViewById(R.id.btn_tab_bottom_mine));
                mViewPager.setCurrentItem(4);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MyMainActivity.this, selected, false, null);
                } else {
                    Toast.makeText(MyMainActivity.this, "请选择至少一个联系人！", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_ADVANCED) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(MyMainActivity.this, selected);
            } else {
                mFragments.get(currentIndex).onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    // 注销
    private void onLogout() {
        // 清理缓存&注销监听
        LogoutHelper.logout();

        // 启动登录
        LoginActivity.start(this);
        finish();
    }
    /**
     * 若增加第三方推送免打扰（V3.2.0新增功能），则：
     * 1.添加下面逻辑使得 push 免打扰与先前的设置同步。
     * 2.设置界面{@link com.netease.nim.demo.main.activity.SettingsActivity} 以及
     * 免打扰设置界面{@link com.netease.nim.demo.main.activity.NoDisturbActivity} 也应添加 push 免打扰的逻辑
     * <p>
     * 注意：isPushDndValid 返回 false， 表示未设置过push 免打扰。
     */
    private void syncPushNoDisturb(StatusBarNotificationConfig staConfig) {

        boolean isNoDisbConfigExist = NIMClient.getService(MixPushService.class).isPushNoDisturbConfigExist();

        if (!isNoDisbConfigExist && staConfig.downTimeToggle) {
            NIMClient.getService(MixPushService.class).setPushNoDisturbConfig(staConfig.downTimeToggle,
                    staConfig.downTimeBegin, staConfig.downTimeEnd);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
//        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }
    private void onInit() {
        com.netease.nim.uikit.UserPreferences.setEarPhoneModeEnable(false);
        initView();
        // 聊天室初始化
        ChatRoomHelper.init();

        LogUtil.ui("NIM SDK cache path=" + NIMClient.getSdkStorageDirPath());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_layout, null);
        TextView tv_shoucang = (TextView) contentView.findViewById(R.id.tv_shoucang);
        TextView tv_tuisong = (TextView) contentView.findViewById(R.id.tv_tuisong);
        TextView tv_dingyue = (TextView) contentView.findViewById(R.id.tv_dingyue);
        TextView tv_gongkai = (TextView) contentView.findViewById(R.id.tv_gongkai);
        tv_shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow1.dismiss();
                Intent intent = new Intent(MyMainActivity.this, CollectionActivity.class);
                intent.putExtra("title", "收藏");
                startActivity(intent);
//                Intent intent = new Intent(MyMainActivity.this, TuyaActivity.class);
//                startActivity(intent);

            }
        });
        tv_tuisong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow1.dismiss();
                if (userInfo.getUserType() < 3) {
                    Intent intent = new Intent(MyMainActivity.this, VideoDirActivity.class);
                    intent.putExtra("isSelect", false);
                    intent.putExtra("isFirst", true);
                    intent.putExtra("title", "推送课");
                    startActivity(intent);
//            findView(R.id.recycler_view3).setVisibility(View.VISIBLE);
                } else {
                    showNormalDialog("推送");
                }
            }
        });
        tv_dingyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow1.dismiss();
                if (userInfo.getUserType() < 4) {
                    Intent intent = new Intent(MyMainActivity.this, VideoDirActivity.class);
                    intent.putExtra("isDingyue", true);
                    intent.putExtra("isSelect", false);
                    intent.putExtra("isFirst", true);
                    intent.putExtra("title", "订阅课");
                    startActivity(intent);
//            findView(R.id.recycler_view3).setVisibility(View.VISIBLE);
                } else {
                    showNormalDialog("订阅");
//                    MyUtils.showToast(getActivity(),"缴费后才能查看该内容");
//            findView(R.id.recycler_view3).setVisibility(View.GONE);
                }
            }
        });
        tv_gongkai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow1.dismiss();
                Intent intent = new Intent(MyMainActivity.this, VideoDirActivity.class);
                intent.putExtra("isOpen", true);
                startActivity(intent);
            }
        });
        popupWindow1 = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow1.setWidth(300);
        popupWindow1.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow1.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.usermainpage_bg_center1));
        // 设置好参数之后再show
//        popupWindow.showAsDropDown(view);
        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        //  获取测量后的宽度
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        popupWindow1.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, x - popupWidth / 2, popupHeight * 2 - 20);
//        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupWidth, popupHeight);
//        popupWindow.showAsDropDown(view);
    }

    private void showPopupWindow2(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_layout2, null);
        TextView tv_setting = (TextView) contentView.findViewById(R.id.tv_setting);
        TextView tv_add = (TextView) contentView.findViewById(R.id.tv_add);
        TextView tv_qun = (TextView) contentView.findViewById(R.id.tv_qun);
        TextView tv_tongxun = (TextView) contentView.findViewById(R.id.tv_tongxun);
        TextView tv_nianji = (TextView) contentView.findViewById(R.id.tv_nianji);
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMainActivity.this, SettingsActivity.class));
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.start(MyMainActivity.this);
            }
        });
        tv_qun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MyMainActivity.this, advancedOption, REQUEST_CODE_ADVANCED);
            }
        });
        tv_tongxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContantListActivity.start(MyMainActivity.this);
            }
        });
        tv_nianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });
        popupWindow2 = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow2.setWidth(300);
        popupWindow2.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow2.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.usermainpage_bg_center1));
        // 设置好参数之后再show
//        popupWindow.showAsDropDown(view);
        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        //  获取测量后的宽度
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        popupWindow2.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, popupHeight * 2 - 20);
//        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupWidth, popupHeight);
//        popupWindow.showAsDropDown(view);
    }

    private void showNormalDialog(String name) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage("开通试用" + name + "课请咨询：18926561053吴老师");
        normalDialog.setPositiveButton("拨打电话",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        downLoad(snapshotUrl);
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + "18926561053");
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 显示操作对话框
     */
    private void showSelectDialog() {
        userInfo = BmobUser.getCurrentUser(MyUser.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择年级");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        final String[] grades = {"四年级", "五年级", "六年级", "初一", "初二", "初三", "高一", "高二", "高三"};
        final int[] gradess = {4, 5, 6, 7, 8, 9, 10, 11, 12};
        builder.setItems(grades, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                ApiUtils.getInstance().user_changegrade(SharedPreferencesUtils.getInt(MyMainActivity.this, "account_id", 0) + "",
                        gradess[which], new ApiListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                SharedPreferencesUtils.setString(MyMainActivity.this,"grade",gradess[which]+"");
                                MyUtils.showToast(MyMainActivity.this, "年级切换成功");
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                MyUtils.showToast(MyMainActivity.this, errorMsg);
                            }
                        });
            }
        });
        builder.show();
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        } else if (intent.hasExtra(EXTRA_APP_QUIT)) {
            onLogout();
            return;
        } else if (intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
            if (AVChatProfile.getInstance().isAVChatting()) {
                Intent localIntent = new Intent();
                localIntent.setClass(this, AVChatActivity.class);
                startActivity(localIntent);
            }
        } else if (intent.hasExtra(com.netease.nim.demo.main.model.Extras.EXTRA_JUMP_P2P)) {
            Intent data = intent.getParcelableExtra(com.netease.nim.demo.main.model.Extras.EXTRA_DATA);
            String account = data.getStringExtra(com.netease.nim.demo.main.model.Extras.EXTRA_ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                SessionHelper.startP2PSession(this, account);
            }
        }
    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(MyMainActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .request();
    }
}
