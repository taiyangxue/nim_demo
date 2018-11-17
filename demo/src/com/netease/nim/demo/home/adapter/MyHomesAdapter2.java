package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyHomesAdapter2 extends BaseQuickAdapter<VideoDir, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    public MyHomesAdapter2(RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item1, null);
    }

    public MyHomesAdapter2(BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item1, null);
    }
    @Override
    protected void convert(BaseViewHolder holder, final VideoDir videoDir, final int position, boolean isScrolling) {
        // bg
//        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageView iv_play = holder.getView(R.id.iv_play);
        ImageView iv_open = holder.getView(R.id.iv_open);
        holder.setText(R.id.tv_name,videoDir.getName());
//        holder.setText(R.id.tv_creat_time,videoDir.getCreatedAt());
    }
}

