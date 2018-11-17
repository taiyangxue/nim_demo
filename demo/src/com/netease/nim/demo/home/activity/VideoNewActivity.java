package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.VideoRet;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.adapter.MyHomesAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VideoNewActivity extends UI {
    private static final String TAG = "VideoNewActivity";
    private static final int SELECT_VIDEO_DIR2 = 1;
    private MyHomesAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private PullToRefreshLayout swipeRefreshLayout;
    /**
     * 上级传过来的文件id
     */
    private int type_id;
    private int pid;
    private String grade;
    private int course;
    private List<VideoRet.DataBean> videolist;
    private int limit;
    private int offset;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String time;
    private String search;
    private EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_dir_admin);
        type_id = getIntent().getIntExtra("type_id", 0);
        pid = getIntent().getIntExtra("pid", 0);
        course = getIntent().getIntExtra("course", 0);
        grade = SharedPreferencesUtils.getString(this, "grade", "");
//        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = getIntent().getStringExtra("title");
        setToolBar(R.id.toolbar, options);
        findViews();
        limit = 20;
        fetchData(true, false);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.section_activity_menu, menu);
//        super.onCreateOptionsMenu(menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.add_search:
//                showSearch();
//                break;
//            case R.id.add_time:
//                showDataPicker();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(true);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                time="";
                search="";
                fetchData(true, false);
            }

            @Override
            public void onPullUpToRefresh() {
                fetchData(false, true);
            }
        });
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        bitmapUtils = new BitmapUtils(this);
        adapter = new MyHomesAdapter(bitmapUtils, recyclerView,course);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyHomesAdapter>() {
            @Override
            public void onItemClick(MyHomesAdapter adapter, View vew, int position) {
                VideoRet.DataBean item= adapter.getItem(position);
                if(item.getPid()!=0){
                    Intent intent=new Intent(VideoNewActivity.this,VideoNewActivity.class);
                    intent.putExtra("pid",item.getId());
                    intent.putExtra("title",item.getName());
                    intent.putExtra("course",course);
                    intent.putExtra("type_id",type_id);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(MyHomesAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
            }
        });
    }


    private void fetchData(boolean isRefresh, final boolean isLoadMore) {
        if (isRefresh) {
            offset = 0;
            adapter.clearData();
        }
        if (isLoadMore) {
            offset += limit;
        }
        if(type_id==99){
            ApiUtils.getInstance().video_getcollects(SharedPreferencesUtils.getInt(VideoNewActivity.this,"account_id",0)+"", course , offset, limit,MyUtils.date2TimeStamp(time,"yyyy-MM-dd")+"",search, new ApiListener<List<VideoRet.DataBean>>() {
                @Override
                public void onSuccess(List<VideoRet.DataBean> dataBeans) {
                    videolist = dataBeans;
                    onFetchDataDone(true, isLoadMore, videolist);
                }

                @Override
                public void onFailed(String errorMsg) {
                    onFetchDataDone(false, isLoadMore, null);
                    MyUtils.showToast(VideoNewActivity.this, errorMsg);
                }
            });
        }else if(type_id==100){
            if(pid==0){
                List<VideoRet.DataBean> dataBeans=new ArrayList<>();
                String[] names={"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
                for (int i=0;i<names.length;i++){
                    VideoRet.DataBean dataBean=new VideoRet.DataBean();
                    dataBean.setName(names[i]);
                    dataBean.setId(i+1);
                    dataBean.setPid(i+1);
                    dataBeans.add(dataBean);
                }
                onFetchDataDone(true, isLoadMore, dataBeans);
            }else {
                ApiUtils.getInstance().video_getpushvideo(
                        SharedPreferencesUtils.getInt(VideoNewActivity.this,"account_id",0)+"",
                        course + "", pid + "",  new ApiListener<List<VideoRet.DataBean>>() {
                            @Override
                            public void onSuccess(List<VideoRet.DataBean> dataBeans) {
                                videolist = dataBeans;
                                onFetchDataDone(true, isLoadMore, videolist);
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                onFetchDataDone(false, isLoadMore, null);
                                MyUtils.showToast(VideoNewActivity.this, errorMsg);
                            }
                        });
            }
        }else {
            ApiUtils.getInstance().video_getvideotype(
                    SharedPreferencesUtils.getInt(VideoNewActivity.this,"account_id",0)+"",
                    course + "", type_id + "", pid,  new ApiListener<List<VideoRet.DataBean>>() {
                @Override
                public void onSuccess(List<VideoRet.DataBean> dataBeans) {
                    videolist = dataBeans;
                    onFetchDataDone(true, isLoadMore, videolist);
                }

                @Override
                public void onFailed(String errorMsg) {
                    onFetchDataDone(false, isLoadMore, null);
                    MyUtils.showToast(VideoNewActivity.this, errorMsg);
                }
            });
        }
    }


    private void onFetchDataDone(final boolean success, final boolean isLoadMore, final List<VideoRet.DataBean> data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束
                swipeRefreshLayout.setLoadMore(false);
//                Log.e("123","isLoadMore:"+isLoadMore);
                if (success) {
                    if (isLoadMore) {
                        adapter.addData(data);
                    } else {
                        adapter.setNewData(data); // 刷新数据源
                    }
                    adapter.closeLoadAnimation();
                }
            }
        });
    }

//    /**
//     * 显示操作对话框
//     *
//     * @param videoDir
//     */
//    private void showSelectDialog(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择操作");
//        builder.setItems(new String[]{"收藏", "移动到", "反馈"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        //添加到收藏列表
//                        break;
//                    case 1:
//                        Intent intent = new Intent(VideoNewActivity.this, VideoNewActivity.class);
//                        intent.putExtra("isSelect", true);
//                        intent.putExtra("isFirst", true);
//                        if (videoDir instanceof VideoDir) {
////                            intent.putExtra("id", ((VideoDir) videoDir).getObjectId());
//                        }
//                        startActivityForResult(intent, SELECT_VIDEO_DIR2);
//                        break;
//                    case 2:
//                        Intent intent1 = new Intent(VideoNewActivity.this, FankuiActivity.class);
//                        intent1.putExtra("video", (Video) videoDir);
//                        startActivity(intent1);
//                        break;
//                }
//            }
//        });
//        builder.show();
//    }
//
//    /**
//     * 显示收藏对话框
//     *
//     * @param videoDir
//     */
//    private void showCollectionDialog(final Object videoDir) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择操作");
//        //    指定下拉列表的显示数据
//        //    设置一个下拉的列表选择项
//        builder.setItems(new String[]{"收藏", "反馈"}, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        //添加到收藏列表
//                        break;
//                    case 1:
////                        addFankui((Video) videoDir);
//                        Intent intent = new Intent(VideoNewActivity.this, FankuiActivity.class);
//                        intent.putExtra("video", (Video) videoDir);
//                        startActivity(intent);
//                        break;
//                }
//            }
//        });
//        builder.show();
//    }
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
                            MyUtils.showToast(VideoNewActivity.this,"搜索的内容不能为空");
                            return;
                        }
                        search=content;
                        fetchData(true,false);
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
                fetchData(true,false);
            }
        }, mYear, mMonth, mDay).show();
    }
}
