package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.Kechengbiao;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.view.AbsGridAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.List;

public class KechengActivity extends UI {
    private GridView detailCource;
    private GridView detailCource2;
    private Gson gson;
    private String[][] contents;
    private String[][] contents2;
    private List<Kechengbiao> kecheng = new ArrayList();
    private List<Kechengbiao> kecheng2 = new ArrayList();
    private AbsGridAdapter secondAdapter;
    private AbsGridAdapter secondAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kecheng);
//        select_course = courses[getIntent().getIntExtra("course", 0)-1];
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "课程表";
        gson = new Gson();
        setToolBar(R.id.toolbar, options);
        detailCource = (GridView) findViewById(R.id.courceDetail);
        detailCource2 = (GridView) findViewById(R.id.courceDetail2);
        fillStringArray();
        secondAdapter = new AbsGridAdapter(this,kecheng);
        secondAdapter.setContent(contents, kecheng, 5, 5);
        detailCource.setAdapter(secondAdapter);
        detailCource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int position2=position%5;
                //求余得到二维索引
//                int column = position % 5;
//                //求商得到二维索引
//                int row = position / 5;
//                MyUtils.showToast(KechengActivity.this, column + "????" + row);
                showSingleChoiceDialog(position,1);
            }
        });
        secondAdapter2 = new AbsGridAdapter(this,kecheng2);
        secondAdapter2.setContent(contents2, kecheng2, 3, 5);
        detailCource2.setAdapter(secondAdapter2);
        detailCource2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int position2=position%5;
                //求余得到二维索引
//                int column = position % 5;
//                //求商得到二维索引
//                int row = position / 5;
//                MyUtils.showToast(KechengActivity.this, column + "????" + row);
                showSingleChoiceDialog(position,2);
            }
        });
    }

    public void fillStringArray() {
        if (TextUtils.isEmpty(SharedPreferencesUtils.getString(this, "contents", ""))) {
            for (int i = 0; i < 25; i++) {
//                for (int j = 0; j < 5; j++) {
                    Kechengbiao kechengbiao = new Kechengbiao();
//                    kechengbiao.column = j;
//                    kechengbiao.row = i;
                    kechengbiao.name = "";
                    kecheng.add(kechengbiao);
//                }
            }
        } else {
            kecheng = gson.fromJson(SharedPreferencesUtils.getString(this, "contents", ""), new TypeToken<List<Kechengbiao>>() {
            }.getType());
        }
        if (TextUtils.isEmpty(SharedPreferencesUtils.getString(this, "contents2", ""))) {
            for (int i = 0; i < 15; i++) {
//                for (int j = 0; j < 5; j++) {
                    Kechengbiao kechengbiao = new Kechengbiao();
//                    kechengbiao.column = j;
//                    kechengbiao.row = i;
                    kechengbiao.name = "";
                    kecheng2.add(kechengbiao);
//                }
            }
        } else {
            kecheng2 = gson.fromJson(SharedPreferencesUtils.getString(this, "contents2", ""), new TypeToken<List<Kechengbiao>>() {
            }.getType());
        }
    }

    //    public void fillDataList() {
//        dataList = new ArrayList<>();
//        for(int i = 1; i < 21; i++) {
//            dataList.add("第" + i + "周");
//        }
//    }
    int yourChoice;

    private void showSingleChoiceDialog(final int position, final int type) {
        final String[] items = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物", "体育", "美术", "音乐", "自习"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("请选择课程");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
//                            contents[row][column]=items[yourChoice];
                            Kechengbiao kechengbiao = new Kechengbiao();
                            kechengbiao.name = items[yourChoice];
                            kechengbiao.pos = yourChoice+1;
                            switch (type){
                                case 1:
                                    kecheng.set(position, kechengbiao);
                                    SharedPreferencesUtils.setString(KechengActivity.this, "contents", gson.toJson(kecheng));
//                                    secondAdapter.setContent( kecheng, 5, 5);
                                    secondAdapter.notifyDataSetChanged();
                                    break;
                                case 2:
                                    kecheng2.set(position, kechengbiao);
                                    SharedPreferencesUtils.setString(KechengActivity.this, "contents2", gson.toJson(kecheng2));
//                                    secondAdapter2.setContent( kecheng, 3, 5);
                                    secondAdapter2.notifyDataSetChanged();
                                    break;
                            }

//                            detailCource.setAdapter(secondAdapter);
//                            Toast.makeText(MainActivity.this,
//                                    "你选择了" + items[yourChoice],
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        singleChoiceDialog.show();
    }
}
