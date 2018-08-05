package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.UserRealtion;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.adapter.UserAdminAdapter;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserAdminActivity extends UI {
    private static final String TAG = "UserAdminActivity";
    private static final int SELECT_VIDEO_DIR = 0;
    private static final int SELECT_VIDEO_DIR2 = 1;
    private static final int SELECT_USER = 2;
    private static final int SELECT_USER_DIR_DINGYUE = 3;
    private UserAdminAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private boolean isOpen;
    private String title;
    private PullToRefreshLayout swipeRefreshLayout;
    private MyUser parent;
    private List<Object> allLists = new ArrayList<>();
    private boolean isSelect;
    private LinearLayout ll_select_video_dir;
    private boolean isFirst;
    /**
     * 上级传过来的文件id
     */
    private EditText et_name;
    private MyUser current_MyUser;
    private int current_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_admin);
//        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "用户关系";
        setToolBar(R.id.toolbar, options);
        findViews();
        fetchData();
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
//            case R.id.add_menu:
////                MyUtils.showToast(this, "添加");
//                if (!isOpen) {
//                    showAddMyUser();
//                } else {
//                    MyUtils.showToast(UserAdminActivity.this, "您无权操作公开课下的文件夹");
//                }
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, requestCode + ">>>" + resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_VIDEO_DIR:
                    if (data != null) {
                        Intent intent = new Intent();
                        intent.putExtra("MyUser", (MyUser) data.getSerializableExtra("MyUser"));
                        Log.e(TAG, requestCode + ">>>" + resultCode);
                        setResult(SELECT_VIDEO_DIR, intent);
                        UserAdminActivity.this.finish();
                    }
                    break;
                case SELECT_VIDEO_DIR2:
                    if (data != null) {
                        MyUser MyUser = (MyUser) data.getSerializableExtra("MyUser");
//                        if (MyUser != null) {
//                            if (current_MyUser instanceof MyUser) {
//                                ((MyUser) current_MyUser).setParent(MyUser);
//                                ((MyUser) current_MyUser).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        MyUtils.showToast(UserAdminActivity.this, "移动成功");
//                                        adapter.remove(current_position);
//                                    }
//                                });
//                            } else if (current_MyUser instanceof Video) {
//                                ((Video) current_MyUser).setMyUser(MyUser);
//                                ((Video) current_MyUser).update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        adapter.remove(current_position);
//                                        MyUtils.showToast(UserAdminActivity.this, "移动成功");
//                                    }
//                                });
//                            }
//                        }
                    }
                    break;
                case SELECT_USER:
                    if (data != null) {
                        //
                        final List<MyUser> userList = (List<MyUser>) data.getSerializableExtra("userList");
                        final MyUser user = (MyUser) data.getSerializableExtra("user");
                        BmobQuery<UserRealtion> relationBmobQuery = new BmobQuery<>();
                        relationBmobQuery.addWhereEqualTo("account", current_MyUser.getUsername());
                        relationBmobQuery.findObjects(new FindListener<UserRealtion>() {
                            @Override
                            public void done(List<UserRealtion> list, BmobException e) {
                                if (e == null) {
                                    if (list != null && list.size() > 0) {
                                        if (userList != null && userList.size() > 0) {
                                            for (MyUser user : userList) {
                                                Log.e(TAG, "push");
                                                push(user, list.get(0));
                                            }
                                        }
                                        if (user != null) {
                                            push(user, list.get(0));
                                        }
                                    } else {
                                        //创建用户关系
                                        final UserRealtion userRelation = new UserRealtion();
                                        userRelation.setAccount(current_MyUser.getUsername());
                                        userRelation.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null) {
                                                    if (userList != null && userList.size() > 0) {
                                                        for (MyUser user : userList) {
                                                            Log.e(TAG, "push");
                                                            push(user, userRelation);
                                                        }
                                                    }
                                                    if (user != null) {
                                                        push(user, userRelation);
                                                    }
                                                } else {
                                                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    //
                                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                                    if(e.getErrorCode()==101){
                                        final UserRealtion userRelation = new UserRealtion();
                                        userRelation.setAccount(current_MyUser.getUsername());
                                        userRelation.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null) {
                                                    if (userList != null && userList.size() > 0) {
                                                        for (MyUser user : userList) {
                                                            Log.e(TAG, "push");
                                                            push(user, userRelation);
                                                        }
                                                    }
                                                    if (user != null) {
                                                        push(user, userRelation);
                                                    }
                                                } else {
                                                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                    break;
                case SELECT_USER_DIR_DINGYUE:
                    if (data != null) {
                        List<MyUser> userList = (List<MyUser>) data.getSerializableExtra("userList");
                        if (userList != null && userList.size() > 0) {
                            for (MyUser user : userList) {
                                Log.e(TAG, "dingyue");
                                dingyue(user);
                            }
                        }
                        MyUser user = (MyUser) data.getSerializableExtra("user");
                        if (user != null) {
                            dingyue(user);
                        }
                    }
                    break;
            }
        }
    }

    private void push(final MyUser user, UserRealtion userRelation) {
        //
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        userRelation.setStudent(relation);
        userRelation.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUtils.showToast(UserAdminActivity.this, "设置成功");
                } else {
                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                    MyUtils.showToast(UserAdminActivity.this, "设置失败");
                }
            }
        });
    }

    private void dingyue(MyUser user) {
        BmobRelation relation = new BmobRelation();
        relation.add(user);
//        ((MyUser) current_MyUser).setDingYueUser(relation);
        ((MyUser) current_MyUser).update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUtils.showToast(UserAdminActivity.this, "分享成功");
                } else {
                    MyUtils.showToast(UserAdminActivity.this, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                fetchData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        // recyclerView
        bitmapUtils = new BitmapUtils(this);
        recyclerView = findView(R.id.recycler_view);
        adapter = new UserAdminAdapter(recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(0), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<UserAdminAdapter>() {
            @Override
            public void onItemClick(UserAdminAdapter adapter, View vew, int position) {
                Intent intent = new Intent(UserAdminActivity.this, UserStudentActivity.class);
                intent.putExtra("teacher",adapter.getItem(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(UserAdminAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                current_MyUser = adapter.getItem(position);
                current_position = position;
                Intent intent = new Intent(UserAdminActivity.this, UserSelectActivity.class);
                intent.putExtra("isUserRelation", true);
                startActivityForResult(intent, SELECT_USER);
            }
        });
    }

    private void fetchData() {
        allLists.clear();
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null) {
            //获取目录数据
            BmobQuery<MyUser> query = new BmobQuery<>();
            query.addWhereEqualTo("userType", 1);
            query.findObjects(new FindListener<MyUser>() {
                @Override
                public void done(List<MyUser> list, BmobException e) {
                    if (list != null && list.size() > 0) {
                        onFetchDataDone(true, list);
                    } else {
                        MyUtils.showToast(UserAdminActivity.this, "数据的获取失败");
                        Log.e(TAG, e.getErrorCode() + e.getMessage());
                        onFetchDataDone(false, null);
                    }
                }
            });
            //获取视频数据
        } else {
            //缓存用户对象为空时， 可打开用户注册界面…
            MyUtils.showToast(this, "当前用户尚未登录");
        }
    }

    private void onFetchDataDone(final boolean success, final List<MyUser> data) {
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
     * 显示操作对话框
     *
     * @param MyUser
     */
    private void showSelectDialog(final Object MyUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        if (MyUser instanceof Video) {
            //公开课的文件操作不可以分享，修改和删除
            if (isOpen) {
                builder.setItems(new String[]{"移动到", "设为公开课首页"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(UserAdminActivity.this, UserAdminActivity.class);
                                intent.putExtra("isSelect", true);
                                intent.putExtra("isFirst", true);
                                intent.putExtra("isOpen", isOpen);
                                if (MyUser instanceof MyUser) {
                                    intent.putExtra("id", ((MyUser) MyUser).getObjectId());
                                }
                                startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                break;
                            case 1:
                                //将文件夹置空释放掉
                                ((Video) MyUser).remove("MyUser");
                                ((Video) MyUser).update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.e(TAG, "设置成功");
                                        } else {
                                            Log.e(TAG, "设置失败" + e.getMessage() + e.getErrorCode());
                                        }
                                    }
                                });
                                adapter.remove(current_position);
                                break;
                        }
                    }
                });
            } else {
                builder.setItems(new String[]{"移动到", "推送给", "修改", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(UserAdminActivity.this, UserAdminActivity.class);
                                intent.putExtra("isSelect", true);
                                intent.putExtra("isFirst", true);
                                if (MyUser instanceof MyUser) {
                                    intent.putExtra("id", ((MyUser) MyUser).getObjectId());
                                }
                                startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                break;
                            case 1:
                                Intent intent1 = new Intent(UserAdminActivity.this, UserSelectActivity.class);
                                startActivityForResult(intent1, SELECT_USER);
                                break;
                            case 2:
                                showEditMyUser(MyUser);
                                break;
                            case 3:
                                showDelete(MyUser);
                                break;
                        }
                    }
                });
            }
        } else {
            if (isFirst) {
                builder.setItems(new String[]{"移动到", "分享给", "修改", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(UserAdminActivity.this, UserAdminActivity.class);
                                intent.putExtra("isSelect", true);
                                intent.putExtra("isDingYue", true);
                                intent.putExtra("isFirst", true);
                                if (MyUser instanceof MyUser) {
                                    intent.putExtra("id", ((MyUser) MyUser).getObjectId());
                                }
                                startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                break;
                            case 1:
                                Intent intent1 = new Intent(UserAdminActivity.this, UserSelectActivity.class);
                                startActivityForResult(intent1, SELECT_USER_DIR_DINGYUE);
                                break;
                            case 2:
                                showEditMyUser(MyUser);
                                break;
                            case 3:
                                showDelete(MyUser);
                                break;
                        }
                    }
                });
            } else {
                builder.setItems(new String[]{"移动到", "修改", "删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(UserAdminActivity.this, UserAdminActivity.class);
                                intent.putExtra("isSelect", true);
                                intent.putExtra("isFirst", true);
                                if (MyUser instanceof MyUser) {
                                    intent.putExtra("id", ((MyUser) MyUser).getObjectId());
                                }
                                startActivityForResult(intent, SELECT_VIDEO_DIR2);
                                break;
                            case 1:
//                                showEditMyUser(MyUser);
                                break;
                            case 2:
                                showDelete(MyUser);
                                break;
                        }
                    }
                });
            }
        }
        builder.show();
    }

    /**
     * 修改文件夹对话框
     *
     * @param MyUser
     */
    public void showDelete(final Object MyUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_alert);
//设置对话框标题
        builder.setTitle("提醒");
//设置对话框内的文本
        if (MyUser instanceof MyUser) {
            builder.setMessage("确定要删除该文件夹及其下面的子文件？");
        } else if (MyUser instanceof Video) {
            builder.setMessage("确定要删除该文件吗？");
        }
//设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                if (MyUser instanceof MyUser) {
                    ((MyUser) MyUser).delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //删除下面的子文件
                                MyUtils.showToast(UserAdminActivity.this, "删除成功");
                                adapter.remove(current_position);
                                dialog.dismiss();
                            } else {
                                MyUtils.showToast(UserAdminActivity.this, "删除失败");
                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                            }
                        }
                    });

                } else if (MyUser instanceof Video) {
                    ((Video) MyUser).update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                adapter.remove(current_position);
                                MyUtils.showToast(UserAdminActivity.this, "删除成功");
                                dialog.dismiss();
                            } else {
                                MyUtils.showToast(UserAdminActivity.this, "删除失败");
                                LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
//设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击取消按钮的业务逻辑
                dialog.dismiss();
            }
        });
//使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
//显示对话框
        dialog.show();
    }

    /**
     * 修改文件夹对话框
     *
     * @param MyUser
     */
    public void showEditMyUser(final Object MyUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_edit_video_dir, null);
        final AlertDialog dialog = builder.create();
        et_name = (EditText) dView.findViewById(R.id.et_name);
        if (MyUser instanceof MyUser) {
//            et_name.setText(((MyUser) MyUser).getName());
        } else if (MyUser instanceof Video) {
            et_name.setText(((Video) MyUser).getVideoName());
        }
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    if (MyUser instanceof MyUser) {
//                        ((MyUser) MyUser).setName(name);
                        ((MyUser) MyUser).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(UserAdminActivity.this, "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(UserAdminActivity.this, "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });

                    } else if (MyUser instanceof Video) {
                        ((Video) MyUser).setVideoName(name);
                        ((Video) MyUser).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(UserAdminActivity.this, "修改成功");
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showToast(UserAdminActivity.this, "修改失败");
                                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    MyUtils.showToast(UserAdminActivity.this, "名称不能为空");
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
     * 创建目录对话框
     */
    public void showAddMyUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_add_video_dir, null);
        final AlertDialog dialog = builder.create();
        final EditText et_tag = (EditText) dView.findViewById(R.id.et_name);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_tag.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                } else {
                    MyUtils.showToast(UserAdminActivity.this, "名称不能为空");
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
}
