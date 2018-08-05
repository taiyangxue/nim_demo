package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.demo.main.adapter.SectionSelectAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 章节选择activity
 */
public class SectionSelectActivity extends UI {
    private static final String TAG = "SectionSelectActivity";
    private static final int SELECT_SETION = 0;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.spinner)
    private Spinner spinner;
    private static final String[] course = {"语文", "数学", "英语", "物理",
            "化学", "生物", "地理", "历史",
            "政治"};
    private ArrayAdapter<String> spinner_adapter;
    private SectionSelectAdapter adapter;
    private MyUser current_student;
    private String current_course;
    private int current_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_select);
        ViewUtils.inject(this);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "错题分类选择";
        setToolBar(R.id.toolbar, options);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.section_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
//                MyUtils.showToast(this, "添加");
                showAddSectionDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        initSpinner(spinner, course);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                if (current_student != null) {
                    refreshData(current_student);
                }
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        // 学生recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SectionSelectAdapter(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener<SectionSelectAdapter>() {
            @Override
            public void onItemClick(SectionSelectAdapter adapter, View view, int position) {
                Section section = adapter.getItem(position);
                Intent intent = new Intent();
                LogUtil.e(TAG, section.getCourse() + section.getName());
                intent.putExtra("course", section.getCourse());
                intent.putExtra("section", section);
                setResult(SELECT_SETION, intent);
                SectionSelectActivity.this.finish();
            }

            @Override
            public void onItemLongClick(SectionSelectAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                current_position = position;
                showSelectDialog(adapter.getItem(position));
            }
        });
    }

    private void onFetchDataDone(final boolean success, final List<Section> data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束
                if (success) {
                    adapter.setNewData(data); // 刷新数据源
                    adapter.closeLoadAnimation();
                }
            }
        });
    }

    /**
     * 刷新数据
     *
     * @param current_student
     */
    private void refreshData(MyUser current_student) {
        adapter.clearData();
        BmobQuery<Section> query = new BmobQuery<>();
        query.addWhereEqualTo("grade", current_student.getGrade());
        query.addWhereEqualTo("course", current_course);
        LogUtil.e(TAG, current_course + current_student.getGrade());
        query.findObjects(new FindListener<Section>() {
            @Override
            public void done(List<Section> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        LogUtil.e(TAG, list.size() + "");
                        onFetchDataDone(true, list);
                    } else {
                        onFetchDataDone(false, null);
                        MyUtils.showToast(SectionSelectActivity.this, "该年级尚未建立章节分类," +
                                "如需要请点击右上角添加新的章节");
                    }
                } else {
                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void initData() {
        String username = getIntent().getStringExtra("username");
        /**
         * 查询用户年级
         */
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    current_student = list.get(0);
                    refreshData(current_student);
                } else {
                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void initSpinner(Spinner spinner, String[] m) {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
        //设置下拉列表的风格
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_course = course[position];
                if (current_student != null) {
                    refreshData(current_student);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 显示操作对话框
     *
     * @param section)
     */
    private void showSelectDialog(final Section section) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"修改", "删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showEditVideoDir(section);
                        break;
                    case 1:
                        showDelete(section);
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 删除章节
     *
     * @param section
     */
    public void showDelete(final Section section) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除该章节吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                section.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //删除下面的子文件
                            MyUtils.showToast(SectionSelectActivity.this, "删除成功");
                            adapter.remove(current_position);
                            dialog.dismiss();
                        } else {
                            MyUtils.showToast(SectionSelectActivity.this, "删除失败");
                            LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                        }
                    }
                });

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 修改文件夹对话框
     *
     * @param section
     */
    public void showEditVideoDir(final Section section) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_edit_section, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_name = (EditText) dView.findViewById(R.id.et_name);
        final EditText et_desc = (EditText) dView.findViewById(R.id.et_desc);
        et_name.setText(section.getName());
        et_desc.setText(section.getDesc());
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                String desc = et_desc.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(desc)) {
                        section.setName(name);
                        section.setDesc(desc);
                        section.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(SectionSelectActivity.this, "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(SectionSelectActivity.this, "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    } else {
                        MyUtils.showToast(SectionSelectActivity.this, "章节描述不可为空");
                    }
                } else {
                    MyUtils.showToast(SectionSelectActivity.this, "章节名不可为空");
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

    /**
     * 分类对话框
     */
    private void showAddSectionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_add_section, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_name = (EditText) dView.findViewById(R.id.et_name);
        final EditText et_desc = (EditText) dView.findViewById(R.id.et_desc);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Section section = new Section();
                String name = et_name.getText().toString().trim();
                String desc = et_desc.getText().toString().trim();
                if (current_student != null) {
                    if (!TextUtils.isEmpty(name)) {
                        if (!TextUtils.isEmpty(desc)) {
                            section.setName(name);
                            section.setDesc(desc);
                            section.setCourse(current_course);
                            section.setAddAccount(DemoCache.getAccount());
                            section.setGrade(current_student.getGrade());
                            section.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        MyUtils.showToast(SectionSelectActivity.this, "添加成功");
                                        refreshData(current_student);
                                    } else {
                                        MyUtils.showToast(SectionSelectActivity.this,
                                                e.getErrorCode() + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            MyUtils.showToast(SectionSelectActivity.this, "章节描述不可为空");
                        }
                    } else {
                        MyUtils.showToast(SectionSelectActivity.this, "章节名不可为空");
                    }
                }
                dialog.dismiss();
            }
        });

    }
}
