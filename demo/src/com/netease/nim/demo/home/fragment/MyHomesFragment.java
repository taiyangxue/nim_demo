package com.netease.nim.demo.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.thridparty.EduChatRoomHttpClient;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直播间列表fragment
 * <p>
 * Created by huangjun on 2015/12/11.
 */
public class MyHomesFragment extends TFragment {
    private static final String TAG = MyHomesFragment.class.getSimpleName();

    private SliderLayout sliderShow;
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.home_cuoti, R.drawable.home_yuyue};
    private String[] iconName = {"开始直播", "房间信息"};
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_homes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        init();
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
    public void init() {
        initSlider();
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
//                        intent=new Intent(getActivity(), ErrorAdminActivity.class);
//                        getActivity().startActivity(intent);
//                        Toast.makeText(getActivity(),"错题管理",Toast.LENGTH_SHORT).show();

                        break;
                    case 1:
//                        EduChatRoomHttpClient.getInstance().fetchChatRoomPullAddress(room.getCid(), new EduChatRoomHttpClient.ChatRoomHttpCallback<AddressResult>() {
//                            @Override
//                            public void onSuccess(AddressResult addressResult) {
//
//                            }
//                            @Override
//                            public void onFailed(int code, String errorMsg) {
//                                MyUtils.showToast(getActivity(),errorMsg);
//                            }
//                        });
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
    private void initSlider() {
        TextSliderView textSliderView1 = new TextSliderView(getActivity());
        textSliderView1
//                .description("")
                .image(R.drawable.room_cover_36);
        TextSliderView textSliderView2 = new TextSliderView(getActivity());
        textSliderView2
//                .description("社区动态")
                .image(R.drawable.room_cover_37);
        TextSliderView textSliderView3 = new TextSliderView(getActivity());
        textSliderView3
//                .description("社区趣事")
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
        sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderShow.setDuration(2000);
    }
    // 创建房间
    private void createRoom(String name) {
        EduChatRoomHttpClient.getInstance().createRoom(DemoCache.getAccount(), name, new EduChatRoomHttpClient.ChatRoomHttpCallback<String>() {
            @Override
            public void onSuccess(String s) {

//                ChatRoomActivity.start(getActivity(), s, true,rtmpPullUrl);
            }
            @Override
            public void onFailed(int code, String errorMsg) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }
}
