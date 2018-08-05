package com.netease.nim.demo.chatroom.adapter;

import android.support.v7.widget.RecyclerView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ChannelListResult;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class ChatRoomsAdapter extends BaseQuickAdapter<ChannelListResult.RetBean.ListBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;

    public ChatRoomsAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.chat_room_item, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, ChannelListResult.RetBean.ListBean room, int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
//        ChatRoomHelper.setCoverImage(room.getRoomId(), coverImage);
        // name
        holder.setText(R.id.tv_name, room.getName());
        // online count
//        TextView onlineCountText = holder.getView(R.id.tv_status);
        switch (room.getStatus()){
            case 0:
            case 2:
                holder.setText(R.id.tv_status,"下课休息中");
                break;
            case 1:
            case 3:
                holder.setText(R.id.tv_status,"上课直播中");
                break;

        }
//        onlineCountText.setText(String.valueOf(room.getFilename()));
    }

//    private void setOnlineCount(TextView onlineCountText, ChatRoomInfo room) {
//        if (room.getOnlineUserCount() < COUNT_LIMIT) {
//            onlineCountText.setText(String.valueOf(room.getOnlineUserCount()));
//        } else if (room.getOnlineUserCount() >= COUNT_LIMIT) {
//            onlineCountText.setText(String.format("%.1f", room.getOnlineUserCount() / (float) COUNT_LIMIT) + "万");
//        }
//    }
}
