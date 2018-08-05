package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.UserRealtion;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.activity.ErrorAdminActivity;
import com.netease.nim.demo.home.adapter.UserListAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/2/6.
 */

public class TeacherErrorFragment extends MainTabFragment {
    private static final String TAG = "TeacherErrorFragment";
    private UserListAdapter adapter;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private PullToRefreshLayout swipeRefreshLayout;
    private static final String[] userTypeName = {"缴费用户", "订阅用户", "普通用户"};
    private static final int[] userType = {2, 3, 4};
    private ArrayAdapter<String> spinner_adapter;
    private int current_user_type = 2;
    public TeacherErrorFragment() {
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
        findViews();
        refreshData(current_user_type);
    }

    private void findViews() {
        spinner = findView(R.id.spinner);
        initSpinner(spinner,userTypeName);
        swipeRefreshLayout = findView(R.id.swipe_refresh);
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
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        adapter = new UserListAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<UserListAdapter>() {
            @Override
            public void onItemClick(UserListAdapter adapter, View view, int position) {
                MyUser user=adapter.getItem(position);
                Intent intent=new Intent(getActivity(), ErrorAdminActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }
    private void initSpinner(Spinner spinner, String[] m) {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, m);
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

//    private void initData() {
//        refreshData(current_user_type);
//    }

    private void refreshData(final int user_type) {
        adapter.clearData();
        final MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            final BmobQuery<MyUser> query = new BmobQuery<>();
            if(userInfo.getUserType()==1){
                BmobQuery<UserRealtion> userRelationBmobQuery = new BmobQuery<>();
                userRelationBmobQuery.addWhereEqualTo("account", userInfo.getUsername());
                userRelationBmobQuery.findObjects(new FindListener<UserRealtion>() {

                    private UserRealtion userRelation;

                    @Override
                    public void done(List<UserRealtion> list, BmobException e) {
                        if (e == null) {
                            userRelation = list.get(0);
                            query.addWhereRelatedTo("student", new BmobPointer(userRelation));
                            query.addWhereEqualTo("userType", user_type);
                            query.findObjects(new FindListener<MyUser>() {
                                @Override
                                public void done(List<MyUser> list, BmobException e) {
                                    if (e == null) {
                                        if (list != null && list.size() > 0) {
                                            onFetchDataDone(true, list);
                                        } else {
                                            onFetchDataDone(false, null);
                                            MyUtils.showToast(getActivity(), "无对应用户数据");
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
            }else {
                query.addWhereEqualTo("userType", user_type);
                query.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null) {
                            if (list != null && list.size() > 0) {
                                onFetchDataDone(true, list);
                            } else {
                                onFetchDataDone(false, null);
                                MyUtils.showToast(getActivity(), "无对应用户数据");
                            }
                        } else {
                            LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                        }
                    }
                });
            }
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
//            MyUtils.showToast(getActivity(), "当前用户尚未登录");
            Log.e(TAG,"当前用户未登陆");
        }

    }

    private void onFetchDataDone(final boolean success, final List<MyUser> data) {
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
                    }
                }
            });
        }
    }
}
