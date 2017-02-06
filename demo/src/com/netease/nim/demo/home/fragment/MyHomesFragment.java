package com.netease.nim.demo.home.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.ChatRoomActivity;
import com.netease.nim.demo.chatroom.adapter.ChatRoomsAdapter;
import com.netease.nim.demo.chatroom.thridparty.ChatRoomHttpClient;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.List;

/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyHomesFragment extends TFragment {
    private static final String TAG = MyHomesFragment.class.getSimpleName();

    private SliderLayout sliderShow;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_homes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        initSlider();
    }

    public void onCurrent() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        // swipeRefreshLayout
        sliderShow = findView(R.id.slider);
        gridView=findView(R.id.gridview);
    }
    private void initSlider() {
        TextSliderView textSliderView1 = new TextSliderView(getActivity());
        textSliderView1
                .description("智慧社区")
                .image(R.drawable.room_cover_36);
        TextSliderView textSliderView2 = new TextSliderView(getActivity());
        textSliderView2
                .description("社区动态")
                .image(R.drawable.room_cover_37);
        TextSliderView textSliderView3 = new TextSliderView(getActivity());
        textSliderView3
                .description("社区趣事")
                .image(R.drawable.room_cover_50)
                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(BaseSliderView slider) {
//                        getActivity().startActivity(new Intent(getActivity(), MapActivity.class));
                    }
                });

        sliderShow.addSlider(textSliderView1);
        sliderShow.addSlider(textSliderView2);
        sliderShow.addSlider(textSliderView3);
        sliderShow.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        sliderShow.setDuration(2000);
    }
}
