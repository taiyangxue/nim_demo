package com.netease.nim.demo.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseActivity extends UI {
    // 图片封装为一个数组
    private int[] icon = {R.drawable.tuike, R.drawable.weike, R.drawable.biji, R.drawable.zhuanxiang,
            R.drawable.zhenti, R.drawable.shoucang};
    private String[] iconName = {"推课", "微课堂", "思维导图", "专项练习",
            "真题试卷", "收藏"};
    private String[] courses = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物"};
    private String select_course;
    private GridView gridView;
    private ArrayList<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        select_course = courses[getIntent().getIntExtra("course", 0)-1];
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = select_course;
        setToolBar(R.id.toolbar, options);
        gridView = (GridView) findViewById(R.id.gridview);
        initData();
    }

    private void initData() {
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_gridview_home, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent = new Intent(CourseActivity.this,VideoNewActivity.class);
                        intent.putExtra("type_id",100);
                        intent.putExtra("title",iconName[position]);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;
                    case 1:
                        intent = new Intent(CourseActivity.this,VideoNewActivity.class);
                        intent.putExtra("type_id",1);
                        intent.putExtra("title",iconName[position]);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;
                    case 2:
                        intent=new Intent(CourseActivity.this,NodeActivity.class);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;
                    case 3:
                        intent = new Intent(CourseActivity.this,VideoNewActivity.class);
                        intent.putExtra("type_id",2);
                        intent.putExtra("title",iconName[position]);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;
                    case 4:
                        intent = new Intent(CourseActivity.this,VideoNewActivity.class);
                        intent.putExtra("type_id",3);
                        intent.putExtra("title",iconName[position]);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;
                    case 5:
                        intent = new Intent(CourseActivity.this,VideoNewActivity.class);
                        //收藏界面
                        intent.putExtra("type_id",99);
                        intent.putExtra("title",iconName[position]);
                        intent.putExtra("course",getIntent().getIntExtra("course", 0));
                        break;

                }
                startActivity(intent);
            }
        });
    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
