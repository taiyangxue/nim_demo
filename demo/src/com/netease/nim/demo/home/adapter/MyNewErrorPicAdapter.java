package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicResult;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyNewErrorPicAdapter extends BaseQuickAdapter<ErrorPicResult.DataBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils bitmapUtils;
    public MyNewErrorPicAdapter(BitmapUtils bitmapUtils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.error_pic_item, null);
        this.bitmapUtils =bitmapUtils;
    }

    @Override
    protected void convert(BaseViewHolder holder, ErrorPicResult.DataBean pic, int position, boolean isScrolling) {
        // bg
//        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        if(pic.getPic_image()!=null){
            bitmapUtils.display(coverImage,pic.getPic_image());
        }else {
            coverImage.setVisibility(View.GONE);
            holder.getView(R.id.ll_category).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_title,pic.getName());
        }
    }
}

