package com.netease.nim.demo.home.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.Kechengbiao;

import java.util.List;

/**
 * Created by wan on 2016/10/16.
 * GridView的适配器
 */
public class AbsGridAdapter extends BaseAdapter {

    private Context mContext;

    private String[][] contents;
    private String[] items = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物", "体育", "美术", "音乐", "自习"};
    private int[] icons = {R.drawable.t_ying, R.drawable.t_yu, R.drawable.t_li, R.drawable.t_shu,
            R.drawable.t_wu, R.drawable.t_hua, R.drawable.t_di, R.drawable.t_zheng,
            R.drawable.t_sheng,R.drawable.t_ti,R.drawable.t_mei,R.drawable.t_yin,R.drawable.t_zixi};
    private int rowTotal;

    private int columnTotal;

    private int positionTotal;
    private List<Kechengbiao> kecheng;
    public AbsGridAdapter(Context context, List<Kechengbiao> kecheng) {
        this.mContext = context;
        this.kecheng=kecheng;
    }

    public int getCount() {
        return kecheng.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public Kechengbiao getItem(int position) {
        //求余得到二维索引
//        int column = position % columnTotal;
//        //求商得到二维索引
//        int row = position / columnTotal;
        return kecheng.get(position);
//        return contents[row][column];
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if( convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grib_item, null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.text);
        ImageView icon= (ImageView) convertView.findViewById(R.id.icon);
        if(getItem(position).pos>0){
            textView.setText(items[getItem(position).pos-1]);
            icon.setImageResource(icons[getItem(position).pos-1]);
        }
        //如果有课,那么添加数据
//        if( !getItem(position).equals("")) {
//            textView.setText((String)getItem(position));
//            textView.setTextColor(Color.WHITE);
//            //变换颜色
//            int rand = position % columnTotal;
//            switch( rand ) {
//                case 0:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.grid_item_bg));
//                    break;
//                case 1:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_12));
//                    break;
//                case 2:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_13));
//                    break;
//                case 3:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_14));
//                    break;
//                case 4:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_15));
//                    break;
//                case 5:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_16));
//                    break;
//                case 6:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_17));
//                    break;
//                case 7:
//                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.bg_18));
//                    break;
//            }
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int row = position / columnTotal;
//                    int column = position % columnTotal;
//                    String con = "当前选中的是" + contents[row][column] + "课";
//                    Toast.makeText(mContext, con, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
        return convertView;
    }

    /**
     * 设置内容、行数、列数
     */
    public void setContent(String[][] contents, List<Kechengbiao> kecheng, int row, int column) {
        this.contents = contents;
        this.rowTotal = row;
        this.columnTotal = column;
        this.kecheng=kecheng;
        positionTotal = rowTotal * columnTotal;
    }


}
