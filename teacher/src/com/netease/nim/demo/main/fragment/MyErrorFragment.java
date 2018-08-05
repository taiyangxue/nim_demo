package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.home.activity.SectionSelectActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyErrorFragment extends MainTabFragment {
    private static final int SELECT_SETION = 0;
    private static final String TAG = "MyErrorFragment";
    private MyErrorPicAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private String[] urls;
    private Spinner spinner_course;
    private Spinner spinner_section;
    private String current_course;
    private String current_section;
    private PullToRefreshLayout swipeRefreshLayout;
    private ErrorPic select_pic;

    public MyErrorFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }

    @Override
    protected void onInit() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_error_admin, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bitmapUtils = new BitmapUtils(getActivity());
        findViews();
        initData();
//        gson = new Gson();
    }

    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        adapter = new MyErrorPicAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyErrorPicAdapter>() {
            @Override
            public void onItemClick(MyErrorPicAdapter adapter, View view, int position) {
                adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                getActivity().startActivity(intent);
            }

            @Override
            public void onItemLongClick(MyErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
//                MyUtils.showToast(getActivity(),"长按");
//                showClassifyDialog(adapter.getItem(position);
                select_pic=adapter.getItem(position);
                Intent intent=new Intent(getActivity(), SectionSelectActivity.class);
                intent.putExtra("username",select_pic.getAccount());
                startActivityForResult(intent,SELECT_SETION);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG,data.getStringExtra("course")+data.getStringExtra("section")
                +requestCode+resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_SETION:
                    LogUtil.e(TAG,data.getStringExtra("course")+data.getStringExtra("section"));
                    select_pic.setCourse(data.getStringExtra("course"));
                    select_pic.setSection(data.getStringExtra("section"));
                    select_pic.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                refreshData();
                            }else {
                                MyUtils.showToast(getActivity(),"提交分类失败，请重新提交");
                            }
                        }
                    });
                    break;
            }
        }
    }
    private void initData() {
        refreshData();
    }
    private void refreshData() {
        BmobQuery<ErrorPic> query = new BmobQuery<>();
        query.addWhereEqualTo("course", "未整理");
        query.findObjects(new FindListener<ErrorPic>() {
            @Override
            public void done(List<ErrorPic> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        onFetchDataDone(true, list);
                    }
                } else {
                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                }
            }
        });
    }
    private void onFetchDataDone(final boolean success, final List<ErrorPic> data) {
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
                        urls = new String[data.size()];
                        for (int i = 0; i < data.size(); i++) {
                            urls[i] = data.get(i).getPicUrl();
                        }
                    }
                }
            });
        }
    }
    /**
     * 分类对话框
     * @param errorPic
     */
    private void showClassifyDialog(final ErrorPic errorPic) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_classify, null);
        final android.app.AlertDialog dialog = builder.create();
        spinner_course = (Spinner) dView.findViewById(R.id.spinner_course);
        spinner_section = (Spinner) dView.findViewById(R.id.spinner_section);
        initSpinner(spinner_course, m);
        initSpinner(spinner_section, m2);
        spinner_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_course=m[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_section=m2[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<ErrorPic> query=new BmobQuery<ErrorPic>();
                query.addWhereEqualTo("picUrl",errorPic.getPicUrl());
                query.findObjects(new FindListener<ErrorPic>() {
                    @Override
                    public void done(List<ErrorPic> list, BmobException e) {
                        if(e==null&&list!=null&&list.size()>0){
                            ErrorPic pic=list.get(0);
                            pic.setCourse(current_course);
                            pic.setSection(current_section);
                            pic.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        refreshData();
                                    }else {
                                        MyUtils.showToast(getActivity(),"提交分类失败，请重新提交");
                                    }
                                }
                            });
                        }
                    }
                });
                dialog.dismiss();
            }
        });

    }

    private static final String[] m = {"语文", "数学", "英语", "物理",
            "化学", "生物", "地理", "历史", "政治"};
    private static final String[] m2 = {"第一章", "第一章", "第一章", "第一章",
            "第一章", "第一章", "第一章"};
    private ArrayAdapter<String> spinner_adapter;

    private void initSpinner(Spinner spinner, String[] m) {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, m);

        //设置下拉列表的风格
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }
}
