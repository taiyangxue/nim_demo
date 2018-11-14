package com.netease.nim.demo.home.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.activity.KechengActivity;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 课程fragment
 */
public class MyToolsFragment extends TFragment {
    private static final String TAG = MyToolsFragment.class.getSimpleName();
    private MyHomesNewAdapter adapter;
    private RecyclerView recyclerView;
    private List<MyItem> myItems;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_day;
    private TextView tv_hour;
    private TextView tv_min;
    private TextView tv_sec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_tools, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        init();
    }

    public void onCurrent() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        recyclerView = findView(R.id.recycler_view);
        tv_title = findView(R.id.tv_title);
        tv_content = findView(R.id.tv_content);
        tv_day = findView(R.id.tv_day);
        tv_hour = findView(R.id.tv_hour);
        tv_min = findView(R.id.tv_min);
        tv_sec = findView(R.id.tv_sec);
        String title = SharedPreferencesUtils.getString(getActivity(), "title", "");
        String content = SharedPreferencesUtils.getString(getActivity(), "content", "");
        String time = SharedPreferencesUtils.getString(getActivity(), "time", "");
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }
        if (!TextUtils.isEmpty(time)) {
            String ret = MyUtils.getDifferenceTime(time, "yyyy-MM-dd");
            String[] times = ret.split(",");
            tv_day.setText(times[0]);
            tv_hour.setText(times[1]);
            tv_min.setText(times[2]);
            tv_sec.setText(times[3]);
            String[] dates=time.split("-");
            tv_title.setText("距离"+dates[0]+"年"+dates[1]+"月"+dates[2]+"日"+title+"还有：");
        }


        SharedPreferencesUtils.setString(getActivity(), "content", content);
        adapter = new MyHomesNewAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyHomesNewAdapter>() {
            @Override
            public void onItemClick(MyHomesNewAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), KechengActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        showAddHeiban();
                        break;
                }
//                Intent intent=new Intent(getActivity(), CourseActivity.class);
//                intent.putExtra("course",position);
//                startActivity(intent);
            }
        });
    }

    public void init() {
        myItems = new ArrayList<>();
        String[] names = {"课程表", "黑板设置"};
        int[] images = {R.drawable.kechengbiao, R.drawable.heiban_icon};
        for (int i = 0; i < names.length; i++) {
            MyItem item = new MyItem();
            item.name = names[i];
            item.image = images[i];
            myItems.add(item);
        }
        adapter.setNewData(myItems); // 刷新数据源
    }

    class MyItem {
        String name;
        int image;
    }

    class MyHomesNewAdapter extends BaseQuickAdapter<MyItem, BaseViewHolder> {
        public MyHomesNewAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_home_main, null);
        }

        @Override
        protected void convert(BaseViewHolder holder, MyItem item, int position, boolean isScrolling) {
            holder.setImageResource(R.id.cover_image, item.image);
            holder.setText(R.id.tv_name, item.name);
        }
    }

    /**
     * 创建目录对话框
     */
    public void showAddHeiban() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dView = View.inflate(getActivity(), R.layout.dialog_add_heiban, null);
        final AlertDialog dialog = builder.create();
        final EditText et_title = (EditText) dView.findViewById(R.id.et_title);
        final EditText et_time = (EditText) dView.findViewById(R.id.et_time);
        final EditText et_content = (EditText) dView.findViewById(R.id.et_content);
        et_time.setCursorVisible(false);
        et_time.setFocusable(false);
        et_time.setFocusableInTouchMode(false);
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPicker(et_time);
            }
        });
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_title.getText().toString().trim();
                final String content = et_content.getText().toString().trim();
                final String time = et_time.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(name)) {
                        if (!TextUtils.isEmpty(time)) {
                            String[] dates=time.split("-");
                            tv_title.setText("距离"+dates[0]+"年"+dates[1]+"月"+dates[2]+"日"+name+"还有：");
                            tv_content.setText(content);
                            SharedPreferencesUtils.setString(getActivity(), "title", name);
                            SharedPreferencesUtils.setString(getActivity(), "content", content);
                            SharedPreferencesUtils.setString(getActivity(), "time", time);
                            String ret = MyUtils.getDifferenceTime(time, "yyyy-MM-dd");
                            String[] times = ret.split(",");
                            tv_day.setText(times[0]);
                            tv_hour.setText(times[1]);
                            tv_min.setText(times[2]);
                            tv_sec.setText(times[3]);
                            dialog.dismiss();
                        } else {
                            MyUtils.showToast(getActivity(), "日期不能为空");
                        }
                    } else {
                        MyUtils.showToast(getActivity(), "内容不能为空");
                    }
                } else {
                    MyUtils.showToast(getActivity(), "事件不能为空");
                }
            }
        });
        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private int mYear;
    private int mMonth;
    private int mDay;
    private String time;

    private void showDataPicker(final EditText et_time) {
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                et_time.setText(days);
//                time = days;
//                mDataChangeListener.onDataTimeChange(time);
//                fetchData(true,false);
            }
        }, mYear, mMonth, mDay).show();
    }
}
