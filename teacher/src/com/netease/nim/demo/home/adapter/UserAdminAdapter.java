package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class UserAdminAdapter extends BaseQuickAdapter<MyUser, BaseViewHolder> {
    public UserAdminAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_user_admin_adapter, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, MyUser user, final int position, boolean isScrolling) {
        holder.setText(R.id.tv_name, user.getNick());
        holder.setText(R.id.tv_user_grade, user.getGrade());
        holder.setText(R.id.tv_user_phone,user.getUsername());
    }
}

