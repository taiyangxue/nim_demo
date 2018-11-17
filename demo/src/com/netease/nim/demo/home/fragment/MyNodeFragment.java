package com.netease.nim.demo.home.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.NodeRet;
import com.netease.nim.demo.common.infra.DataChangeListener;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.activity.NodeActivity;
import com.netease.nim.demo.home.adapter.MyNodeAdapter;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.List;


/**
 * 课程fragment
 */
@SuppressLint("ValidFragment")
public class MyNodeFragment extends TFragment {
    private static final String TAG = MyNodeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private MyNodeAdapter adapter;
    private BitmapUtils bitmapUtils;
    private PullToRefreshLayout swipeRefreshLayout;
    private String time;
    private String search;
    private int offset;
    private int limit = 20;
//    private int course;
    private String type;
    private List<NodeRet.DataBean> nodelist;
    private NodeActivity mActivity;
    private int mCourse;
    public MyNodeFragment(NodeActivity nodeActivity, int course, int mtype) {
        mActivity=nodeActivity;
        mCourse=course;
        type=mtype+"";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_homes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        fetchData(true, false);
    }

    public void onCurrent() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(true);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                time = "";
                search = "";
                fetchData(true, false);
            }

            @Override
            public void onPullUpToRefresh() {
                fetchData(false, true);
            }
        });
        mActivity.setDataChange(new DataChangeListener() {
            @Override
            public void onDataSearchChange(String msearch) {
                search=msearch;
                fetchData(true,false);
            }

            @Override
            public void onDataTimeChange(String mtime) {
                time=mtime;
                fetchData(true,false);
            }
        });
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        bitmapUtils = new BitmapUtils(getActivity());
        adapter = new MyNodeAdapter(bitmapUtils, recyclerView, mCourse);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(0), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyNodeAdapter>() {
            @Override
            public void onItemClick(MyNodeAdapter adapter, View view, int position) {
                NodeRet.DataBean node=adapter.getItem(position);
//                Intent intent = new Intent(mActivity, NodePlayActivity.class);
//                intent.putExtra("node",node);
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                String url;
//                if(node.getContent_image().startsWith("http")){
//                    url=node.getContent_image();
//                }else {
//                    url=ApiUtils.STATIC_HOST+node.getContent_image();
//                }
                url=MyUtils.formatUrl(node.getContent_image());
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{url});
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(MyNodeAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
            }
        });
    }


    private void fetchData(boolean isRefresh, final boolean isLoadMore) {
        if (isRefresh) {
            offset = 0;
            adapter.clearData();
        }
        if (isLoadMore) {
            offset += limit;
        }
        ApiUtils.getInstance().node_getnodes(SharedPreferencesUtils.getInt(getActivity(), "account_id", 0) + "",
                mCourse,type, offset, limit, MyUtils.date2TimeStamp(time, "yyyy-MM-dd") + "",
                search, new ApiListener<List<NodeRet.DataBean>>() {
            @Override
            public void onSuccess(List<NodeRet.DataBean> dataBeans) {
                nodelist = dataBeans;
                onFetchDataDone(true, isLoadMore, nodelist);
            }

            @Override
            public void onFailed(String errorMsg) {
                onFetchDataDone(false, isLoadMore, null);
                MyUtils.showToast(getActivity(), errorMsg);
            }
        });
    }


    private void onFetchDataDone(final boolean success, final boolean isLoadMore, final List<NodeRet.DataBean> data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束
                swipeRefreshLayout.setLoadMore(false);
//                Log.e("123","isLoadMore:"+isLoadMore);
                if (success) {
                    if (isLoadMore) {
                        adapter.addData(data);
                    } else {
                        adapter.setNewData(data); // 刷新数据源
                    }
                    adapter.closeLoadAnimation();
                }
            }
        });
    }
}
