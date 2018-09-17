package com.netease.nim.demo.home.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicRet;
import com.netease.nim.demo.common.entity.Videocomment;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.contact.activity.AddFriendActivity;
import com.netease.nim.demo.main.activity.MyMainActivity;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class VideocommentAdapter extends BaseQuickAdapter<Videocomment.DataBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils bitmapUtils;
    public VideocommentAdapter(BitmapUtils bitmapUtils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_comment_item, null);
        this.bitmapUtils =bitmapUtils;
    }

    @Override
    protected void convert(BaseViewHolder holder, final Videocomment.DataBean item, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        TextView tv_username = holder.getView(R.id.tv_username);
        // 加载本地图片(路径以/开头， 绝对路径)
//        bitmapUtils.display(testImageView, "/sdcard/test.jpg");
        bitmapUtils.display(coverImage,item.getImage());
        holder.setText(R.id.tv_username,item.getUser_name());
        holder.setText(R.id.tv_createtime, "发表时间："+ MyUtils.timeStamp2Date(item.getCreatetime()+"",null));

        holder.addOnClickListener(R.id.cover_image);
        holder.addOnClickListener(R.id.tv_username);
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,AddFriendActivity.class);
                intent.putExtra("user_mobile",item.getUser_mobile());
                mContext.startActivity(intent);
            }
        });
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.getImage())) {
                    Intent intent = new Intent(mContext, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    String imgurl="";
                    String ans_imgurl="";
                    if(item.getImage().startsWith("http")){
                        imgurl=item.getImage();
                    }else {
                        imgurl= ApiUtils.STATIC_HOST+item.getImage();
                    }
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{imgurl});
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    intent.putExtra(ImagePagerActivity.EXTRA_IS_VIDEO,false);
                    mContext.startActivity(intent);
                } else {
                    MyUtils.showToast(mContext, "图片获取失败");
                }

            }
        });
    }
}
