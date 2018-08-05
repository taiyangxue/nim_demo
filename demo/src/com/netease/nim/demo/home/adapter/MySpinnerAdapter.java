package com.netease.nim.demo.home.adapter;

import android.content.Context;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.SectionRet;
import com.netease.nim.demo.common.entity.bmob.Section;

import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 */

public class MySpinnerAdapter extends CommentAdpter<SectionRet.DataBean>{
    public MySpinnerAdapter(Context context, List<SectionRet.DataBean> data) {
        super(context, data, R.layout.item_spinner_section);
    }

    @Override
    protected void convert(ViewHolder viewHolder, SectionRet.DataBean item) {
        viewHolder.setText(R.id.tv_name,item.getName());
        viewHolder.setText(R.id.tv_desc,item.getDescription());
    }
}
