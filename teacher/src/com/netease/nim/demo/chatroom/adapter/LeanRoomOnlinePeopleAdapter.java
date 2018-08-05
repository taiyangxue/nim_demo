package com.netease.nim.demo.chatroom.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.widget.ChatRoomImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.util.List;

/**
 * Created by huangjun on 2016/12/29.
 */
public class LeanRoomOnlinePeopleAdapter extends BaseQuickAdapter<ChatRoomMember, BaseViewHolder> {

    public LeanRoomOnlinePeopleAdapter(RecyclerView recyclerView, List<ChatRoomMember> members) {
        super(recyclerView, R.layout.online_people_student_item, members);
    }

    @Override
    protected void convert(BaseViewHolder holder, ChatRoomMember member, int position, boolean isScrolling) {
        // bg selector
        holder.getConvertView().setBackgroundResource(com.netease.nim.uikit.R.drawable.touch_bg);

        // head image
        ChatRoomImageView userHeadImage = holder.getView(R.id.user_head);
        userHeadImage.loadAvatarByUrl(member.getAvatar());

        // user name
        holder.setText(R.id.user_name, TextUtils.isEmpty(member.getNick()) ? "" : member.getNick());
    }
}
