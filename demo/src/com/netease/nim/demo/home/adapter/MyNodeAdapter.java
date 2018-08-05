package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.NodeRet;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyNodeAdapter extends BaseQuickAdapter<NodeRet.DataBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;
    private int course;
    public MyNodeAdapter(BitmapUtils utils, RecyclerView recyclerView, int course) {
        super(recyclerView, R.layout.item_node_adapter, null);
        this.utils=utils;
        this.course=course;
    }

    public MyNodeAdapter(BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_node_adapter, null);
        this.utils=utils;

    }
    @Override
    protected void convert(BaseViewHolder holder, final NodeRet.DataBean video, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        holder.setText(R.id.tv_title,video.getTitle());
        holder.setText(R.id.tv_time, "创建时间："+MyUtils.timeStamp2Date(video.getCreatetime()+"",null));
    }
}

