package com.netease.nim.demo.chatroom.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.widget.ChatRoomImageView;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/29.
 */
public class LeanRoomOnlineTeacherAdapter extends BaseQuickAdapter<MyUser, BaseViewHolder> {

    public LeanRoomOnlineTeacherAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.online_people_teacher_item, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, MyUser member, int position, boolean isScrolling) {
        // bg selector
        holder.getConvertView().setBackgroundResource(com.netease.nim.uikit.R.drawable.touch_bg);

        // head image
        ChatRoomImageView userHeadImage = holder.getView(R.id.user_head);
//        userHeadImage.loadAvatarByUrl(member.getAvatar());
        if(member.isOnline()){
            if(member.isFree()){
                userHeadImage.setImageResource(R.drawable.shape_my_green);
            }else {
                userHeadImage.setImageResource(R.drawable.shape_my_red);
            }
        }else {
            userHeadImage.setImageResource(R.drawable.shape_my_ovel_gray);
        }
        // user name
        holder.setText(R.id.user_name, TextUtils.isEmpty(member.getNick()) ? "" : member.getNick());
    }
}
