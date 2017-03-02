package com.netease.nim.demo.chatroom.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.neliveplayer.NEMediaPlayer;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomTabPagerAdapter;
import com.netease.nim.demo.chatroom.fragment.tab.ChatRoomTabFragment;
import com.netease.nim.demo.chatroom.helper.NEMediaController;
import com.netease.nim.demo.chatroom.helper.VideoListener;
import com.netease.nim.demo.chatroom.widget.NEVideoView;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nim.demo.common.util.Preferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatResCode;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nrtc.sdk.NRtcParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天室顶层fragment
 * Created by hzxuwen on 2015/12/14.
 * ViewPager.OnPageChangeListener, AVChatStateObserver, View.OnClickListener
 */
public class ChatRoomFragment extends ChatRoomTabFragment implements ViewPager.OnPageChangeListener, AVChatStateObserver {
    private static final String TAG = ChatRoomFragment.class.getSimpleName();
    private ChatRoomInfo roomInfo;
    private String roomId;
    private String roomName;
    private String shareUrl; // 分享地址
    private boolean isCreate = false; // 是否是主播
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private ChatRoomTabPagerAdapter adapter;
    private int scrollState;
    //    private ImageViewEx imageView;
    private ViewGroup masterVideoLayout; // 左上，主播显示区域
    private TextView statusText;
    public NEVideoView mVideoView;  //用于画面显示
    private View mLoadingView; //用于指示缓冲状态
    private NEMediaController mMediaController; //用于控制播放

    public static final int NELP_LOG_UNKNOWN = 0; //!< log输出模式：输出详细
    public static final int NELP_LOG_DEFAULT = 1; //!< log输出模式：输出详细
    public static final int NELP_LOG_VERBOSE = 2; //!< log输出模式：输出详细
    public static final int NELP_LOG_DEBUG = 3; //!< log输出模式：输出调试信息
    public static final int NELP_LOG_INFO = 4; //!< log输出模式：输出标准信息
    public static final int NELP_LOG_WARN = 5; //!< log输出模式：输出警告
    public static final int NELP_LOG_ERROR = 6; //!< log输出模式：输出错误
    public static final int NELP_LOG_FATAL = 7; //!< log输出模式：一些错误信息，如头文件找不到，非法参数使用
    public static final int NELP_LOG_SILENT = 8; //!< log输出模式：不输出

    private String mVideoPath; //文件路径
    private String mDecodeType;//解码类型，硬解或软解
    private String mMediaType; //媒体类型
    private boolean mHardware = true;
    private ImageButton mPlayBack;
    private TextView mFileName; //文件名称
    private String mTitle;
    private Uri mUri;
    private boolean pauseInBackgroud = false;

    private RelativeLayout mPlayToolbar;

    NEMediaPlayer mMediaPlayer = new NEMediaPlayer();
    private AVChatVideoRender masterRender;

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


    public void initFragment() {
        mMediaType   = "livestream";
        mDecodeType = "software";
        mVideoPath = "rtmp://v2220e357.live.126.net/live/e1f3a464831c45b6bb3dd18d6a762993";
//        mVideoPath  = getIntent().getStringExtra("videoPath");
//        private String decodeType = "software";  //解码类型，默认软件解码
//        private String mediaType = "livestream"; //媒体类型，默认网络直播
//        Intent intent = getIntent();
//        String intentAction = intent.getAction();
//        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
//            mVideoPath = intent.getDataString();
//        }

        if (mDecodeType.equals("hardware")) {
            mHardware = true;
        }
        else if (mDecodeType.equals("software")) {
            mHardware = false;
        }

        mUri = Uri.parse(mVideoPath);
        if (mUri != null) { //获取文件名，不包括地址
            List<String> paths = mUri.getPathSegments();
            String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
            setFileName(name);
        }

        if (mMediaType.equals("livestream")) {
            mVideoView.setBufferStrategy(0); //直播低延时
        }
        else {
            mVideoView.setBufferStrategy(2); //点播抗抖动
        }
        mVideoView.setMediaController(mMediaController);
        mVideoView.setBufferingIndicator(mLoadingView);
        mVideoView.setMediaType(mMediaType);
        mVideoView.setHardwareDecoder(mHardware);
        mVideoView.setPauseInBackground(pauseInBackgroud);
        mVideoView.setVideoPath(mVideoPath);
        mMediaPlayer.setLogLevel(NELP_LOG_SILENT); //设置log级别
        mVideoView.requestFocus();
        mVideoView.start();

        mPlayBack.setOnClickListener(mOnClickEvent); //监听退出播放的事件响应
        mMediaController.setOnShownListener(mOnShowListener); //监听mediacontroller是否显示
        mMediaController.setOnHiddenListener(mOnHiddenListener); //监听mediacontroller是否隐藏
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

    // 初始化UI
    public void initLiveVideo(ChatRoomInfo roomInfo, String channelName, boolean isCreate, String shareUrl, ModuleProxy moduleProxy) {
        this.roomInfo = roomInfo;
        this.roomId = roomInfo.getRoomId();
        this.roomName = channelName;
        this.shareUrl = shareUrl;
        this.isCreate = isCreate;
//        roomIdText.setText(String.format("房间:%s", roomId));
        AVChatOptionalConfig avChatOptionalParam = new AVChatOptionalConfig();
        int mode = Preferences.getAVChatPip();
        if (mode != -1) {
            LogUtil.i(TAG, "pip mode:" + mode);
            avChatOptionalParam.setLivePIPMode(mode);
        }
        String audioEffect = Preferences.getAudioEffectMode();
        LogUtil.i(TAG, "audio effect ns mode:" + audioEffect);
        avChatOptionalParam.setAudioEffectNSMode(audioEffect);
        if (isCreate) {
            avChatOptionalParam.enableAudienceRole(false);
            avChatOptionalParam.enableLive(true);
            avChatOptionalParam.setLiveUrl(shareUrl);
//            ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, roomInfo.getCreator());
        } else {
            avChatOptionalParam.enableAudienceRole(true);
        }
        AVChatManager.getInstance().joinRoom(roomId, AVChatType.VIDEO, avChatOptionalParam, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                LogUtil.d(TAG, "join channel success, extra:" + avChatData.getExtra());
                // 设置音量信号监听, 通过AVChatStateObserver的onReportSpeaker回调音量大小
                AVChatParameters avChatParameters = new AVChatParameters();
                avChatParameters.setBoolean(NRtcParameters.KEY_AUDIO_REPORT_SPEAKER, true);
                AVChatManager.getInstance().setParameters(avChatParameters);
            }

            @Override
            public void onFailed(int i) {
                LogUtil.d(TAG, "join channel failed, code:" + i);
                Toast.makeText(getActivity(), "join channel failed, code:" + i, Toast.LENGTH_SHORT).show();
//                onMasterJoin(DemoCache.getAccount());
//                masterRender = new AVChatVideoRender(getActivity());
//                addIntoMasterPreviewLayout(masterRender);
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
//        updateControlUI();
//        switchHandsUpLayout();
//        updateVideoAudioUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        masterVideoLayout = findView(R.id.master_video_layout);
//        statusText = findView(R.id.online_status);
//        final ImageView backImage = findView(R.id.back_arrow);
        tabs = findView(R.id.chat_room_tabs);
        viewPager = findView(R.id.chat_room_viewpager);
        mPlayToolbar = (RelativeLayout)findView(R.id.play_toolbar);
        mPlayToolbar.setVisibility(View.INVISIBLE);

        mLoadingView = findView(R.id.buffering_prompt);
        mMediaController = new NEMediaController(getActivity());

        mVideoView = (NEVideoView) findView(R.id.video_view);
        mPlayBack = (ImageButton)findView(R.id.player_exit);//退出播放
        mPlayBack.getBackground().setAlpha(0);
        mFileName = (TextView) findView(R.id.file_name);
//        backImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NIMClient.getService(ChatRoomService.class).exitChatRoom(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId());
//                ((ChatRoomActivity) getActivity()).clearChatRoom();
//            }
//        });
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

        View.OnClickListener mOnClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_exit) {
                mVideoView.release_resource();
                onDestroy();
//                finish();
            }
        }
    };

    NEMediaController.OnShownListener mOnShowListener = new NEMediaController.OnShownListener() {

        @Override
        public void onShown() {
            mPlayToolbar.setVisibility(View.VISIBLE);
            mPlayToolbar.requestLayout();
            mVideoView.invalidate();
            mPlayToolbar.postInvalidate();
        }
    };

    NEMediaController.OnHiddenListener mOnHiddenListener = new NEMediaController.OnHiddenListener() {

        @Override
        public void onHidden() {
            mPlayToolbar.setVisibility(View.INVISIBLE);
        }
    };

    public void setFileName(String name) { //设置文件名并显示出来
        mTitle = name;
        if (mFileName != null)
            mFileName.setText(mTitle);

        mFileName.setGravity(Gravity.CENTER);
    }

    /*****************************
     * AVChatStateObserver
     *********************************/
    private VideoListener videoListener;
    private List<String> userJoinedList = new ArrayList<>(); // 已经onUserJoined的用户

    @Override
    public void onTakeSnapshotResult(String s, boolean b, String s1) {

    }

    @Override
    public void onConnectionTypeChanged(int i) {

    }

    @Override
    public void onLocalRecordEnd(String[] strings, int i) {

    }

    @Override
    public void onFirstVideoFrameAvailable(String s) {

    }

    @Override
    public void onVideoFpsReported(String s, int i) {

    }

    @Override
    public void onJoinedChannel(int i, String s, String s1) {
        LogUtil.d(TAG, "onJoinedChannel, res:" + i);
        if (i != AVChatResCode.JoinChannelCode.OK) {
            Toast.makeText(getActivity(), "joined channel:" + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLeaveChannel() {
        userJoinedList.remove(DemoCache.getAccount());
    }

    @Override
    public void onUserJoined(String s) {
        userJoinedList.add(s);
        onMasterJoin(s);
        LogUtil.e(TAG, "onUserJoined" + s);
    }

    @Override
    public void onUserLeave(String s, int i) {
        // 用户离开频道，如果是有权限用户，移除下画布
        videoListener.onUserLeave(s);
        userJoinedList.remove(s);
    }

    @Override
    public void onProtocolIncompatible(int i) {

    }

    @Override
    public void onDisconnectServer() {

    }

    @Override
    public void onNetworkQuality(String account, int i) {

    }

    @Override
    public void onCallEstablished() {
        userJoinedList.add(DemoCache.getAccount());
        onMasterJoin(DemoCache.getAccount());
    }

    @Override
    public void onDeviceEvent(int i, String s) {

    }

    @Override
    public void onFirstVideoFrameRendered(String s) {

    }

    @Override
    public void onVideoFrameResolutionChanged(String s, int i, int i1, int i2) {

    }

    @Override
    public int onVideoFrameFilter(AVChatVideoFrame avChatVideoFrame) {
        return 0;
    }

    @Override
    public int onAudioFrameFilter(AVChatAudioFrame avChatAudioFrame) {
        return 0;
    }

    @Override
    public void onAudioDeviceChanged(int i) {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> map, int i) {
        videoListener.onReportSpeaker(map);
    }

    @Override
    public void onStartLiveResult(int i) {
    }

    @Override
    public void onStopLiveResult(int i) {
    }

    @Override
    public void onAudioMixingEvent(int i) {

    }


    /**************************** AVChatStateObserver end ****************************/

    /***************************
     * 画布的显示和取消
     **********************************/

    // 主持人进入频道
    private void onMasterJoin(String s) {
//        if (userJoinedList != null && userJoinedList.contains(s) && s.equals(roomInfo.getCreator())) {
        if (masterRender == null) {
            masterRender = new AVChatVideoRender(getActivity());
        }
        boolean isSetup = setupMasterRender(s, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        if (isSetup && masterRender != null) {
            addIntoMasterPreviewLayout(masterRender);
//                ChatRoomMemberCache.getInstance().savePermissionMemberbyId(roomId, roomInfo.getCreator());
//                updateDeskShareUI();
        }
//        }
    }

//    private void updateDeskShareUI() {
//        Map<String, Object> ext = roomInfo.getExtension();
//        if (ext != null && ext.containsKey(MeetingConstant.FULL_SCREEN_TYPE)) {
//            int fullScreenType = (int) ext.get(MeetingConstant.FULL_SCREEN_TYPE);
//            if (fullScreenType == FullScreenType.CLOSE.getValue()) {
//                fullScreenImage.setVisibility(View.GONE);
//            } else if (fullScreenType == FullScreenType.OPEN.getValue()) {
//                fullScreenImage.setVisibility(View.VISIBLE);
//            }
//        }
//    }

    private boolean setupMasterRender(String s, int mode) {
        boolean isSetup = false;
        try {
            isSetup = AVChatManager.getInstance().setupVideoRender(s, masterRender, false, mode);
        } catch (Exception e) {
            LogUtil.e(TAG, "set up video render error:" + e.getMessage());
            e.printStackTrace();
        }
        return isSetup;
    }

    // 将主持人添加到主持人画布
    private void addIntoMasterPreviewLayout(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null)
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        masterVideoLayout.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
    }

//    ChatRoomMemberCache.RoomMemberChangedObserver roomMemberChangedObserver = new ChatRoomMemberCache.RoomMemberChangedObserver() {
//        @Override
//        public void onRoomMemberIn(ChatRoomMember member) {
//            onMasterJoin(member.getAccount());
//
//            if (DemoCache.getAccount().equals(roomInfo.getCreator())
//                    && !member.getAccount().equals(DemoCache.getAccount())) {
//                // 主持人点对点通知有权限的成员列表
//                // 主持人自己进来，不需要通知自己
//                MsgHelper.getInstance().sendP2PCustomNotification(roomId, MeetingOptCommand.ALL_STATUS.getValue(),
//                        member.getAccount(), ChatRoomMemberCache.getInstance().getPermissionMems(roomId));
//            }
//
//            if (member.getAccount().equals(roomInfo.getCreator())) {
//                // 主持人重新进来,观众要取消自己的举手状态
//                ChatRoomMemberCache.getInstance().saveMyHandsUpDown(roomId, false);
//            }
//
//            if (member.getAccount().equals(roomInfo.getCreator()) && DemoCache.getAccount().equals(roomInfo.getCreator())) {
//                // 主持人自己重新进来，清空观众的举手状态
//                ChatRoomMemberCache.getInstance().clearAllHandsUp(roomId);
//                // 重新向所有成员请求权限
//                requestPermissionMembers();
//            }
//        }
//
//        @Override
//        public void onRoomMemberExit(ChatRoomMember member) {
//            // 主持人要清空离开成员的举手
//            if (DemoCache.getAccount().equals(roomInfo.getCreator())) {
//                ChatRoomMemberCache.getInstance().removeHandsUpMem(roomId, member.getAccount());
//            }
//
//            // 用户离开频道，如果是有权限用户，移除下画布
//            if (member.getAccount().equals(roomInfo.getCreator())) {
//                masterVideoLayout.removeAllViews();
//            } else if (ChatRoomMemberCache.getInstance().hasPermission(roomId, member.getAccount())) {
//                removeMemberPermission(member.getAccount());
//            }
//        }
//    };
}
