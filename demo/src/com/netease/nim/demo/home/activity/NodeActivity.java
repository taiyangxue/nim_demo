package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.infra.DataChangeListener;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.fragment.MyNodeFragment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class NodeActivity extends UI {
    private ArrayList<Fragment> mFragments;

    private String[] mTitles_3 = {"自建思维导图", "云思维导图"};
    private SegmentTabLayout mTabLayout_3;
    private ViewPager vp_3;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String time;
    private String search;
    private EditText et_name;
    private Intent intent;
    private DataChangeListener mDataChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "思维导图";
        setToolBar(R.id.toolbar, options);
        mFragments = new ArrayList<>();
        mFragments.add(new MyNodeFragment(this,getIntent().getIntExtra("course",0),1));
        mFragments.add(new MyNodeFragment(this,getIntent().getIntExtra("course",0),0));
        mTabLayout_3 = (SegmentTabLayout) findViewById(R.id.tl_3);
        vp_3 = (ViewPager) findViewById(R.id.vp_2);
        initData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.node_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_search:
                showSearch();
                break;
//            case R.id.add_time:
//                showDataPicker();
//                break;
            case R.id.add_add:
//                showDataPicker();
                intent=new Intent(NodeActivity.this,NodeDetailActivity2.class);
                intent.putExtra("title","添加思维导图");
                intent.putExtra("course",getIntent().getIntExtra("course", 0));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initData() {
        vp_3.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mTabLayout_3.setTabData(mTitles_3);
        mTabLayout_3.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp_3.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        vp_3.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position<mTitles_3.length){
                    mTabLayout_3.setCurrentTab(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp_3.setCurrentItem(0);
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles_3[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
    /**
     * 搜索对话框
     */
    public void showSearch() {
        View view = getLayoutInflater().inflate(R.layout.dialog_search, null);
        et_name = (EditText) view.findViewById(R.id.et_name);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ali_search)//设置标题的图片
                .setTitle("搜索")//设置对话框的标题
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = et_name.getText().toString();
                        dialog.dismiss();
                        if(TextUtils.isEmpty(content)){
                            MyUtils.showToast(NodeActivity.this,"搜索的内容不能为空");
                            return;
                        }
                        mDataChangeListener.onDataSearchChange(search);
//                        search=content;
//                        fetchData(true,false);
                    }
                }).create();
        dialog.show();
    }
    private void showDataPicker(){
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                String days;
                if (mMonth + 1 < 10) {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("-").append("0").
                                append(mMonth + 1).append("-").append("0").append(mDay).toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("-").append("0").
                                append(mMonth + 1).append("-").append(mDay).toString();
                    }

                } else {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("-").
                                append(mMonth + 1).append("-").append("0").append(mDay).toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("-").
                                append(mMonth + 1).append("-").append(mDay).toString();
                    }

                }
                time = days;
                mDataChangeListener.onDataTimeChange(time);
//                fetchData(true,false);
            }
        }, mYear, mMonth, mDay).show();
    }
    public void setDataChange(DataChangeListener dataChangeListener){
        mDataChangeListener=dataChangeListener;
    }
}
