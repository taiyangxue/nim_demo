package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.home.adapter.MySpinnerAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ErrorAdminDetailActivity extends UI {
    private static final String TAG = "ErrorAdminDetailActivity";
    private static final int SELECT_SETION = 0;
    private String[] urls;
    private MyErrorPicAdapter adapter;
    private BitmapUtils bitmapUtils;
    private String select_course;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.spinner)
    private Spinner spinner;
    private MySpinnerAdapter spinner_adapter;
    private List<Section> sections = new ArrayList<Section>();
    private Section current_section;
    private MyUser current_user;
    private ErrorPic select_pic;
    private int current_position;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_detail_admin);
        ViewUtils.inject(this);
        select_course = getIntent().getStringExtra("course");
        current_user = (MyUser) getIntent().getSerializableExtra("user");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = select_course;
        setToolBar(R.id.toolbar, options);
        findViews();
        getSectionData(select_course);
        refreshData();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        LogUtil.e(TAG,data.getStringExtra("course")+data.getStringExtra("section")
//                +requestCode+resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_SETION:
                    if(data!=null){
                        LogUtil.e(TAG,data.getStringExtra("course")+data.getStringExtra("section"));
                        select_pic.setCourse(data.getStringExtra("course"));
                        select_pic.setSectionId((Section) data.getSerializableExtra("section"));
                        select_pic.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    adapter.remove(current_position);
                                }else {
                                    MyUtils.showToast(ErrorAdminDetailActivity.this,"提交分类失败，请重新提交");
                                }
                            }
                        });
                    }
                    break;
            }
        }
    }
    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        // recyclerView
        bitmapUtils = new BitmapUtils(this);
        adapter = new MyErrorPicAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyErrorPicAdapter>() {
            @Override
            public void onItemClick(MyErrorPicAdapter adapter, View view, int position) {
                adapter.getItem(position);
                Intent intent = new Intent(ErrorAdminDetailActivity.this, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                urls=new String[adapter.getData().size()];
                for (int i=0;i<adapter.getData().size();i++) {
                    urls[i]=adapter.getData().get(i).getPicUrl();
                }
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(MyErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                select_pic=adapter.getItem(position);
                current_position=position;
//                Intent intent=new Intent(ErrorAdminActivity.this, SectionSelectActivity.class);
//                intent.putExtra("username",select_pic.getAccount());
//                startActivityForResult(intent,SELECT_SETION);
                showSelectDialog();
            }
        });
        initSpinner();
    }

    private void initSpinner() {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new MySpinnerAdapter(this, sections);
        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_section = sections.get(position);
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void refreshData() {
        adapter.clearData();
        BmobQuery<ErrorPic> query = new BmobQuery<>();
        query.addWhereEqualTo("course", select_course);
        if (current_section != null) {
            query.addWhereEqualTo("sectionId", current_section);
        }
        query.addWhereEqualTo("account", current_user.getUsername());
        query.order("-updatedAt");
        query.findObjects(new FindListener<ErrorPic>() {
            @Override
            public void done(List<ErrorPic> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    onFetchDataDone(true, list);

                } else {
                    onFetchDataDone(false, null);
                    MyUtils.showToast(ErrorAdminDetailActivity.this, "该分类暂无数据");
                }
            }
        });
    }

    private void onFetchDataDone(final boolean success, final List<ErrorPic> list) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束

                if (success) {
                    adapter.setNewData(list); // 刷新数据源
                    adapter.closeLoadAnimation();
//                    urls = new String[list.size()];
//                    for (int i = 0; i < list.size(); i++) {
//                        urls[i] = list.get(i).getPicUrl();
//                    }
                }
            }
        });
    }

    /**
     * 获取对应科目的章节
     *
     * @param current_course
     */
    private void getSectionData(String current_course) {
        BmobQuery<Section> query = new BmobQuery<>();
        query.addWhereEqualTo("grade", current_user.getGrade());
        query.addWhereEqualTo("course", current_course);
        query.order("updatedAt");
        query.findObjects(new FindListener<Section>() {
            @Override
            public void done(List<Section> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        sections.clear();
                        sections.addAll(list);
                        if(current_section==null){
                            current_section=sections.get(0);
                        }
                        spinner_adapter.notifyDataSetChanged();
                    } else {
                        MyUtils.showToast(ErrorAdminDetailActivity.this, "该科目暂无章节分类");
                    }
                } else {
                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }
    /**
     * 显示操作对话框
     *
     */
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ErrorAdminDetailActivity.this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"重新分类", "下载","删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent=new Intent(ErrorAdminDetailActivity.this, SectionSelectActivity.class);
                        intent.putExtra("username",select_pic.getAccount());
                        startActivityForResult(intent,SELECT_SETION);
                        break;
                    case 1:
                        showDownloadDialog(select_pic.getPicUrl());
                        break;
                    case 2:
                        showDelete();
                        break;
                }
            }
        });
        builder.show();
    }
    /**
     * 删除章节
     *
     */
    public void showDelete() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ErrorAdminDetailActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除该图片吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                select_pic.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //删除下面的子文件
                            MyUtils.showToast(ErrorAdminDetailActivity.this, "删除成功");
                            adapter.remove(current_position);
                            dialog.dismiss();
                        } else {
                            MyUtils.showToast(ErrorAdminDetailActivity.this, "删除失败");
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
    private void showDownloadDialog(final String snapshotUrl){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ErrorAdminDetailActivity.this);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("下载文件");
        normalDialog.setMessage("确定要下载该文件?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        downLoad(snapshotUrl);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
    private void downLoad(final String snapshotUrl) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            HttpUtils http = new HttpUtils();
            if(snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1).contains(".jpg")){
                fileName=snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1);
            }else {
                fileName=snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1)+".jpg";
            }
            http.download(snapshotUrl, "/sdcard/yidu/" + System.currentTimeMillis() + fileName, true, true,
                    new RequestCallBack<File>() {

                        @Override
                        public void onFailure(HttpException arg0,
                                              String arg1) {
//                            Log.e(TAG, snapshotUrl + ">>>>>>" + arg1);
                            Toast.makeText(ErrorAdminDetailActivity.this,
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
//                            Log.e(TAG,"下载完成");
                            Toast.makeText(ErrorAdminDetailActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            //进度条效果
                            System.out.println(isUploading);
                            //进度条效果
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.setMax((int) total);
//                            progressBar.setProgress((int) current);
//                            if(current==0){
//                                showProgressDialog(total);
//                            }
//                            showProgressDialog(total);
//                            progressDialog.setProgress((int) current);
                            if (total == current) {
//                                progressDialog.cancel();
                                Toast.makeText(ErrorAdminDetailActivity.this, "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ErrorAdminDetailActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
        }
    }
}
