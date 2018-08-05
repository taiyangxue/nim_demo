package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;

import com.netease.nim.demo.R;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class UserListAdapter extends BaseQuickAdapter<MyUser, BaseViewHolder> {
    public UserListAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_user_adapter, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, MyUser user, final int position, boolean isScrolling) {
        holder.setText(R.id.tv_name, user.getNick());
        holder.setText(R.id.tv_user_grade, user.getGrade());
        holder.setText(R.id.tv_user_phone,user.getUsername());
        CheckBox checkBox = holder.getView(R.id.checkBox);
        holder.addOnClickListener(R.id.checkBox);
        switch (user.getUserType()) {
            case 1:
                holder.setText(R.id.tv_user_type, "老师");
                break;
            case 2:
                holder.setText(R.id.tv_user_type, "缴费用户");
                break;
            case 3:
                holder.setText(R.id.tv_user_type, "订阅用户");
                break;
            case 4:
                holder.setText(R.id.tv_user_type, "普通用户");
                break;
        }
    }
}

