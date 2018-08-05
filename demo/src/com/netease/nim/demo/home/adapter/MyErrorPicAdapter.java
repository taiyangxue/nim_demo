package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicRet;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyErrorPicAdapter extends BaseQuickAdapter<ErrorPicRet.DataBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils bitmapUtils;
    public MyErrorPicAdapter(BitmapUtils bitmapUtils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.error_pic_item, null);
        this.bitmapUtils =bitmapUtils;
    }

    @Override
    protected void convert(BaseViewHolder holder, ErrorPicRet.DataBean pic, int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        // 加载本地图片(路径以/开头， 绝对路径)
//        bitmapUtils.display(testImageView, "/sdcard/test.jpg");
        Log.e(TAG,pic.getPic_image());
        bitmapUtils.display(coverImage,pic.getPic_image());
    }
}

