package com.netease.nim.demo.chatroom.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.neliveplayer.NEMediaPlayer;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomTabPagerAdapter;
import com.netease.nim.demo.chatroom.fragment.tab.ChatRoomTabFragment;
import com.netease.nim.demo.chatroom.helper.NEMediaController;
import com.netease.nim.demo.chatroom.widget.NEVideoView;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;

import java.util.List;

/**
 * 聊天室顶层fragment
 * Created by hzxuwen on 2015/12/14.
 */
public class ChatRoomFragment extends ChatRoomTabFragment implements ViewPager.OnPageChangeListener {
    private static final String TAG = ChatRoomFragment.class.getSimpleName();
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
    public static final int NELP_LOG_DEBUG   = 3; //!< log输出模式：输出调试信息
    public static final int NELP_LOG_INFO    = 4; //!< log输出模式：输出标准信息
    public static final int NELP_LOG_WARN    = 5; //!< log输出模式：输出警告
    public static final int NELP_LOG_ERROR   = 6; //!< log输出模式：输出错误
    public static final int NELP_LOG_FATAL   = 7; //!< log输出模式：一些错误信息，如头文件找不到，非法参数使用
    public static final int NELP_LOG_SILENT  = 8; //!< log输出模式：不输出

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInit() {
        //接收MainActivity传过来的参数
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
        statusText.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    public void updateView() {
//        ChatRoomHelper.setCoverImage(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId(), imageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        masterVideoLayout = findView(R.id.master_video_layout);
        statusText = findView(R.id.online_status);
        final ImageView backImage = findView(R.id.back_arrow);
        tabs = findView(R.id.chat_room_tabs);
        viewPager = findView(R.id.chat_room_viewpager);
        mPlayToolbar = (RelativeLayout)findView(R.id.play_toolbar);
        mPlayToolbar.setVisibility(View.INVISIBLE);

        mLoadingView = findView(R.id.buffering_prompt);
        mMediaController = new NEMediaController(getActivity());

        mVideoView = (NEVideoView) findView(R.id.video_view);
        mPlayBack = (ImageButton)findView(R.id.player_exit);//退出播放
        mPlayBack.getBackground().setAlpha(0);
        mFileName = (TextView)findView(R.id.file_name);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NIMClient.getService(ChatRoomService.class).exitChatRoom(((ChatRoomActivity) getActivity()).getRoomInfo().getRoomId());
                ((ChatRoomActivity) getActivity()).clearChatRoom();
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
}
