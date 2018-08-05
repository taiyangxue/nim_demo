package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ErrorAdminActivity extends UI {
    private static final int SELECT_SETION = 0;
    private static final String TAG = "ErrorAdminActivity";
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private MyErrorPicAdapter adapter;
    private RecyclerView recyclerView;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.t_yu, R.drawable.t_shu, R.drawable.t_ying, R.drawable.t_wu,
            R.drawable.t_hua, R.drawable.t_sheng, R.drawable.t_di, R.drawable.t_li,
            R.drawable.t_zheng};
    private String[] iconName = {"语文", "数学", "英语", "物理",
            "化学", "生物", "地理", "历史",
            "政治"};
    private BitmapUtils bitmapUtils;
    private String[] urls;
    private String grade;
    private MyUser curreent_user;
    private ErrorPic select_pic;
    private int current_position;
    private String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_admin);
        curreent_user = (MyUser) getIntent().getSerializableExtra("user");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = curreent_user.getNick();
        setToolBar(R.id.toolbar, options);
        findViews();
        initData();
    }

    private void findViews() {
        gridView = findView(R.id.gridview);
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        adapter = new MyErrorPicAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyErrorPicAdapter>() {
            @Override
            public void onItemClick(MyErrorPicAdapter adapter, View view, int position) {
                adapter.getItem(position);
                Intent intent = new Intent(ErrorAdminActivity.this, ImagePagerActivity.class);
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
    }

    private void initData() {
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_gridview_error, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ErrorAdminActivity.this, ErrorAdminDetailActivity.class);
                intent.putExtra("course", iconName[position]);
                intent.putExtra("user",curreent_user);
                startActivity(intent);
            }
        });
        refreshData();
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

    private void refreshData() {
        BmobQuery<ErrorPic> query = new BmobQuery<>();
        query.addWhereEqualTo("course", "未整理");
        query.addWhereEqualTo("account", curreent_user.getUsername());
        query.order("-updatedAt");
        query.findObjects(new FindListener<ErrorPic>() {
            @Override
            public void done(List<ErrorPic> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
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
                                    MyUtils.showToast(ErrorAdminActivity.this,"提交分类失败，请重新提交");
                                }
                            }
                        });
                    }
                    break;
            }
        }
    }
    /**
     * 显示操作对话框
     *
     */
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ErrorAdminActivity.this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"分类", "下载","删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent=new Intent(ErrorAdminActivity.this, SectionSelectActivity.class);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ErrorAdminActivity.this);
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
                            MyUtils.showToast(ErrorAdminActivity.this, "删除成功");
                            adapter.remove(current_position);
                            dialog.dismiss();
                        } else {
                            MyUtils.showToast(ErrorAdminActivity.this, "删除失败");
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
                new AlertDialog.Builder(ErrorAdminActivity.this);
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
                            Log.e(TAG, snapshotUrl + ">>>>>>" + arg1);
                            Toast.makeText(ErrorAdminActivity.this,
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
                            Log.e(TAG,"下载完成");
                            Toast.makeText(ErrorAdminActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ErrorAdminActivity.this, "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ErrorAdminActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
        }
    }
}
