package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

import java.util.HashMap;

/**
 * Created by huangjun on 2016/12/9.
 */
public class UserSelectAdapter extends BaseQuickAdapter<MyUser, BaseViewHolder> {
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    private boolean isMulChoice;
    public UserSelectAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_user_adapter, null);
        isSelected = new HashMap<Integer, Boolean>();
    }
    public int countNum;
    @Override
    protected void convert(BaseViewHolder holder, MyUser user, final int position, boolean isScrolling) {
        holder.setText(R.id.tv_name, user.getNick());
        holder.setText(R.id.tv_user_grade, user.getGrade());
        holder.setText(R.id.tv_user_phone,user.getUsername());
        final CheckBox checkBox = holder.getView(R.id.checkBox);
        holder.addOnClickListener(R.id.checkBox);
        switch (user.getUserType()) {
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
        if (isMulChoice) {
            Log.i(TAG,"sMulChoice"+isMulChoice);
            checkBox.setVisibility(CheckBox.VISIBLE);
        } else {
            Log.i(TAG,"sMulChoice"+isMulChoice);
            checkBox.setVisibility(CheckBox.INVISIBLE);
        }
        checkBox.setChecked(getIsSelected().get(position));
//        if(countNum<10){
//            checkBox.setClickable(true);
//        }else {
//            checkBox.setClickable(false);
//        }
        checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                    countNum--;
                } else {
                    if(countNum<10){
                        isSelected.put(position, true);
                        setIsSelected(isSelected);
                        countNum++;
                    }else {
                        MyUtils.showToast(mContext,"最多只能选择10个学生");
                    }
                }
            }
        });
    }
    public boolean isMulChoice() {
        return isMulChoice;
    }

    public void setMulChoice(boolean mulChoice) {
        isMulChoice = mulChoice;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        UserSelectAdapter.isSelected = isSelected;
    }
}

