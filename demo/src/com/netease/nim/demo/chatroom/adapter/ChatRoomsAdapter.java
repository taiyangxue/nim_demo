package com.netease.nim.demo.chatroom.adapter;

import android.support.v7.widget.RecyclerView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class ChatRoomsAdapter extends BaseQuickAdapter<ClassRoom, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;

    public ChatRoomsAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.chat_room_item, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, ClassRoom room, int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        if(room.getRoomId().equals("8000428")){
            coverImage.setImageResource(R.drawable.lean_room);
            holder.setText(R.id.tv_name, room.getName());
            holder.setText(R.id.tv_status, "欢迎进入自习室");
        }else {
            coverImage.setImageResource(R.drawable.on_line_room);
            holder.setText(R.id.tv_name, room.getName());
            holder.setText(R.id.tv_status, "上课直播中");
        }

    }
}

