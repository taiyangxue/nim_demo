package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyErrorPicAdapter extends BaseQuickAdapter<ErrorPic, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    public MyErrorPicAdapter( RecyclerView recyclerView) {
        super(recyclerView, R.layout.error_pic_item, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, ErrorPic pic, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        holder.setText(R.id.tv_name,pic.getCreatedAt());
//        final TextView textView=holder.getView(R.id.tv_name);
//        BmobQuery<MyUser> query=new BmobQuery<>();
//
//        query.addWhereEqualTo("username",pic.getAccount());
//        query.findObjects(new FindListener<MyUser>() {
//            @Override
//            public void done(List<MyUser> list, BmobException e) {
//                if(e==null&&list!=null&&list.size()>0){
//                    MyUser myUser=list.get(0);
//                    textView.setText(myUser.getGrade()+"  "+myUser.getNick());
//                }
//            }
//        });
        // 加载本地图片(路径以/开头， 绝对路径)
//        bitmapUtils.display(testImageView, "/sdcard/test.jpg");
        Log.e(TAG,pic.getPicUrl());
        coverImage.load(pic.getPicUrl());
    }
}

