package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.netease.nim.demo.R;
/**
 * Created by huangjun on 2016/12/9.
 */
    public class ErrorAdminAdapter extends RecyclerView.Adapter<ErrorAdminAdapter.ViewHolder> {
        private int[] icon = {R.drawable.t_yu, R.drawable.t_shu,R.drawable.t_ying, R.drawable.t_wu,
                R.drawable.t_hua, R.drawable.t_sheng,R.drawable.t_di, R.drawable.t_li,
                R.drawable.t_zheng};
        private String[] iconName = {"语文", "数学","英语","物理",
                "化学", "生物","地理","历史",
                "政治"};
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.error_admin_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.cover_image.setImageResource(icon[position]);
            viewHolder.tv_name.setText(iconName[position]);
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return iconName.length;
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView cover_image;
            public TextView tv_name;
            public ViewHolder(View view){
                super(view);
                cover_image = (ImageView) view.findViewById(R.id.cover_image);
                tv_name= (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }
