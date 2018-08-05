package com.netease.nim.demo.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.demo.R;
import com.netease.nim.demo.home.activity.CourseActivity;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 课程fragment
 */
public class MyHomesNewFragment extends TFragment {
    private static final String TAG = MyHomesNewFragment.class.getSimpleName();
    private MyHomesNewAdapter adapter;
    private RecyclerView recyclerView;
    private List<MyItem> myItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_homes, container, false);
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
        recyclerView = findView(R.id.recycler_view);
        adapter = new MyHomesNewAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyHomesNewAdapter>() {
            @Override
            public void onItemClick(MyHomesNewAdapter adapter, View view, int position) {
//                switch (position){
//                    case 0:
//                        break;
//                }
                Intent intent=new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("course",position+1);
                startActivity(intent);
            }
        });
    }
    public void init() {
        myItems=new ArrayList<>();
        String[] names={"英语","语文","历史","数学","物理","化学","地理","政治","生物"};
        int[] images={R.drawable.yingyu,R.drawable.yuwen,R.drawable.lishi,R.drawable.shuxue
                ,R.drawable.wuli,R.drawable.huaxue,R.drawable.dili,R.drawable.zhengzhi,R.drawable.shengwu};
        for (int i=0;i<names.length;i++){
            MyItem item=new MyItem();
            item.name=names[i];
            item.image=images[i];
            myItems.add(item);
        }
        adapter.setNewData(myItems); // 刷新数据源
    }
    class MyItem{
        String name;
        int image;
    }
    class MyHomesNewAdapter extends BaseQuickAdapter<MyItem, BaseViewHolder> {
        public MyHomesNewAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_home_main, null);
        }
        @Override
        protected void convert(BaseViewHolder holder, MyItem item, int position, boolean isScrolling) {
            holder.setImageResource(R.id.cover_image,item.image);
            holder.setText(R.id.tv_name,item.name);
        }
    }
}
