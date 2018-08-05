package com.netease.nim.demo.main.adapter;

import android.support.v7.widget.RecyclerView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class SectionSelectAdapter extends BaseQuickAdapter<Section, BaseViewHolder> {

    public SectionSelectAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_section_adapter, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, Section section, int position, boolean isScrolling) {
        holder.setText(R.id.tv_name, section.getName());
        holder.setText(R.id.tv_desc, section.getDesc());
    }
}

