package com.netease.nim.demo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.UserRealtion;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.UserSelectAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 推送用户选择activity
 */
public class UserSelectActivity extends UI {
    private static final String TAG = "UserSelectActivity";
    private static final int SELECT_USER = 0;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.spinner)
    private Spinner spinner;
    private String[] userTypeName;
    private int[] userType;
    private ArrayAdapter<String> spinner_adapter;
    private int current_user_type;
    private UserSelectAdapter adapter;
    private List<MyUser> userLists;
    private boolean isUserRelation;

    @OnClick({R.id.bt_all, R.id.bt_more, R.id.bt_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_all:
                adapter.setMulChoice(true);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < adapter.getIsSelected().size(); i++) {
                    adapter.getIsSelected().put(i, true);
                }
                break;
            case R.id.bt_more:
                adapter.notifyDataSetChanged();
                adapter.setMulChoice(true);
                break;
            case R.id.bt_ok:
                List<MyUser> users = new ArrayList<>();
                for (int i = 0; i < adapter.getIsSelected().size(); i++) {
                    if (adapter.getIsSelected().get(i)) {
                        users.add(userLists.get(i));
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("userList", (Serializable) users);
                setResult(SELECT_USER, intent);
                UserSelectActivity.this.finish();
                adapter.setMulChoice(false);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < userLists.size(); i++) {
                    adapter.getIsSelected().put(i, false);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_select);
        ViewUtils.inject(this);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "用户选择";
        setToolBar(R.id.toolbar, options);
        isUserRelation = getIntent().getBooleanExtra("isUserRelation", false);
        final MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            if (userInfo.getUserType() == 0&&!isUserRelation) {
                current_user_type=1;
                userTypeName = new String[]{"老师", "缴费用户", "订阅用户", "普通用户"};
                userType = new int[]{1, 2, 3, 4};
            } else {
                current_user_type=2;
                userTypeName = new String[]{"缴费用户", "订阅用户", "普通用户"};
                userType = new int[]{2, 3, 4};
            }
        } else {
            MyUtils.showToast(this, "当前用户未登陆");
        }
        initView();
        refreshData(current_user_type);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.section_activity_menu, menu);
//        super.onCreateOptionsMenu(menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_menu:
////                MyUtils.showToast(this, "添加");
//                showAddSectionDialog();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void initView() {
        initSpinner(spinner, userTypeName);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                refreshData(current_user_type);
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        // 学生recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserSelectAdapter(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener<UserSelectAdapter>() {
            @Override
            public void onItemClick(UserSelectAdapter adapter, View view, int position) {
                MyUser user = adapter.getItem(position);
                if (!adapter.isMulChoice()) {
                    Intent intent = new Intent();
                    intent.putExtra("user", user);
                    setResult(SELECT_USER, intent);
                    UserSelectActivity.this.finish();
                }
            }

        });
    }

    private void onFetchDataDone(final boolean success, final List<MyUser> data) {
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
    private void refreshData(final int userType) {
        adapter.clearData();
        final MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            final BmobQuery<MyUser> query = new BmobQuery<>();
            if (userInfo.getUserType() == 1) {
                BmobQuery<UserRealtion> userRelationBmobQuery = new BmobQuery<>();
                userRelationBmobQuery.addWhereEqualTo("account", userInfo.getUsername());
                userRelationBmobQuery.findObjects(new FindListener<UserRealtion>() {

                    private UserRealtion userRelation;

                    @Override
                    public void done(List<UserRealtion> list, BmobException e) {
                        if (e == null) {
                            userRelation = list.get(0);
                            query.addWhereRelatedTo("student", new BmobPointer(userRelation));
                            query.addWhereEqualTo("userType", userType);
                            query.findObjects(new FindListener<MyUser>() {
                                @Override
                                public void done(List<MyUser> list, BmobException e) {
                                    if (e == null) {
                                        if (list != null && list.size() > 0) {
                                            userLists = list;
                                            onFetchDataDone(true, list);
                                            for (int i = 0; i < list.size(); i++) {
                                                adapter.getIsSelected().put(i, false);
                                            }
                                        } else {
                                            onFetchDataDone(false, null);
                                            MyUtils.showToast(UserSelectActivity.this, "无对应用户数据");
                                        }
                                    } else {
                                        LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.e(TAG, e.getErrorCode() + e.getMessage());
                        }
                    }
                });
            } else {
                query.addWhereEqualTo("userType", userType);
                query.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                userLists = list;
                                onFetchDataDone(true, list);
                                for (int i = 0; i < list.size(); i++) {
                                    adapter.getIsSelected().put(i, false);
                                }
                            } else {
                                onFetchDataDone(false, null);
                                MyUtils.showToast(UserSelectActivity.this, "无对应用户数据");
                            }
                        } else {
                            LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                        }
                    }
                });
            }
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(this, "当前用户尚未登录");
        }

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
                current_user_type = userType[position];
                refreshData(current_user_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
