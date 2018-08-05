package com.netease.nim.demo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.SectionRet;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.adapter.SectionSelectAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 章节选择activity
 */
public class SectionSelectActivity extends UI {
    private static final String TAG = "SectionSelectActivity";
    private static final int SELECT_SETION = 0;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.spinner)
    private Spinner spinner;
    //    private static final String[] course = {"语文", "数学", "英语", "物理",
//            "化学", "生物", "地理", "历史",
//            "政治"};
    private String[] course = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物"};

    private ArrayAdapter<String> spinner_adapter;
    private SectionSelectAdapter adapter;
    private int current_position;
    private int select_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_select);
        ViewUtils.inject(this);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "错题分类选择";
        setToolBar(R.id.toolbar, options);
        select_course = getIntent().getIntExtra("course_id", 0);
        initView();
    }

    private void initView() {
        initSpinner(spinner, course);
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
        // 学生recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SectionSelectAdapter(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener<SectionSelectAdapter>() {
            @Override
            public void onItemClick(SectionSelectAdapter adapter, View view, int position) {
                SectionRet.DataBean section = adapter.getItem(position);
                Intent intent = new Intent();
//                LogUtil.e(TAG, section.getCourse() + section.getName());
                intent.putExtra("section_id", section.getId());
                setResult(SELECT_SETION, intent);
                SectionSelectActivity.this.finish();
            }
        });
    }

    private void onFetchDataDone(final boolean success, final List<SectionRet.DataBean> data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束
                if (success) {
                    adapter.setNewData(data); // 刷新数据源
                    adapter.closeLoadAnimation();
                }
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        adapter.clearData();
        ApiUtils.getInstance().errorpic_getsection(select_course,
                SharedPreferencesUtils.getString(this, "grade", ""), new ApiListener<List<SectionRet.DataBean>>() {
                    @Override
                    public void onSuccess(List<SectionRet.DataBean> dataBeans) {
                        onFetchDataDone(true, dataBeans);
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        onFetchDataDone(false, null);
                        MyUtils.showToast(SectionSelectActivity.this, "该年级尚未建立章节分类");
                    }
                });
//        BmobQuery<Section> query = new BmobQuery<>();
//        query.addWhereEqualTo("grade", current_student.getGrade());
//        query.addWhereEqualTo("course", current_course);
//        LogUtil.e(TAG, current_course + current_student.getGrade());
//        query.findObjects(new FindListener<Section>() {
//            @Override
//            public void done(List<Section> list, BmobException e) {
//                if (e == null) {
//                    if (list != null && list.size() > 0) {
//                        LogUtil.e(TAG, list.size() + "");
//                        onFetchDataDone(true, list);
//                    } else {
//                        onFetchDataDone(false, null);
//                        MyUtils.showToast(SectionSelectActivity.this, "该年级尚未建立章节分类");
//                    }
//                } else {
//                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
    }


    private void initSpinner(Spinner spinner, String[] m) {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        //设置下拉列表的风格
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
