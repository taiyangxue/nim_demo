package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loveplusplus.demo.image.HackyViewPager;
import com.loveplusplus.demo.image.ImageDetailFragment;
import com.loveplusplus.demo.image.SharedPreferencesUtils;
import com.loveplusplus.demo.image.TuyaActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.VideoRet;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.zdp.aseo.content.AseoZdpAseo;

import java.util.List;

public class ImagePagerActivity2 extends FragmentActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_DATA_BEANS = "data_beans";
    public static final String EXTRA_DAAN = "daan";
    public static final String EXTRA_DAANS = "daans";
    public static final String EXTRA_DAAN_URL = "daan_url";
    public static final String EXTRA_DAAN_URLS = "daan_urls";
    public static final String EXTRA_VIDEO_URL = "video_url";
    public static final String EXTRA_VIDEO_URLS = "video_urls";
    public static final String EXTRA_VIDEO_IDS = "video_ids";
    public static final String EXTRA_IS_VIDEO = "is_video";
    public static final String EXTRA_IS_COLLECT = "is_collect";
    public static final String EXTRA_COURSE = "course";

    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private ImageView iv_daan;
    private ImageView iv_shipin;
    private ImageView iv_hudong;
    private ImageView iv_collect;
    private String daan;
    private String daan_url;
    private String video_url;
    private boolean is_video;
    private List<VideoRet.DataBean> data_beans;
    private String[] daans;
    private String[] daan_urls;
    private String[] video_urls;
    private ImageView iv_yidu;
    private String[] video_ids;
    private String current_id;
    private int current_position;
    private int course;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.loveplusplus.demo.image.R.layout.image_detail_pager);
        AseoZdpAseo.initType(this, AseoZdpAseo.SCREEN_TYPE);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        daan = getIntent().getStringExtra(EXTRA_DAAN);
        current_position = pagerPosition;
        daans = getIntent().getStringArrayExtra(EXTRA_DAANS);
        daan_url = getIntent().getStringExtra(EXTRA_DAAN_URL);
        daan_urls = getIntent().getStringArrayExtra(EXTRA_DAAN_URLS);
        data_beans = (List<VideoRet.DataBean>) getIntent().getSerializableExtra(EXTRA_DATA_BEANS);
        video_url = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        video_urls = getIntent().getStringArrayExtra(EXTRA_VIDEO_URLS);
        video_ids = getIntent().getStringArrayExtra(EXTRA_VIDEO_IDS);

        course = getIntent().getIntExtra(EXTRA_COURSE, 0);
        is_video = getIntent().getBooleanExtra(EXTRA_IS_VIDEO, false);
        if (is_video) {
            current_id = video_ids[pagerPosition];
        }
        mPager = (HackyViewPager) findViewById(com.loveplusplus.demo.image.R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(com.loveplusplus.demo.image.R.id.indicator);
        CharSequence text = getString(com.loveplusplus.demo.image.R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        iv_yidu = (ImageView) findViewById(com.loveplusplus.demo.image.R.id.iv_yidu);
        if (is_video) {
            if (SharedPreferencesUtils.getBoolean(ImagePagerActivity2.this, current_id, false)) {
                iv_yidu.setImageResource(com.loveplusplus.demo.image.R.drawable.icon_dui_green);
            } else {
                iv_yidu.setImageResource(com.loveplusplus.demo.image.R.drawable.icon_cuo_green);
            }
        }
        iv_collect = (ImageView) findViewById(com.loveplusplus.demo.image.R.id.iv_collect);
        iv_daan = (ImageView) findViewById(com.loveplusplus.demo.image.R.id.iv_daan);
        iv_shipin = (ImageView) findViewById(com.loveplusplus.demo.image.R.id.iv_shipin);
        iv_hudong = (ImageView) findViewById(com.loveplusplus.demo.image.R.id.iv_hudong);
        updatepage(0);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(final int position) {
                CharSequence text = getString(com.loveplusplus.demo.image.R.string.viewpager_indicator,
                        position + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                updatepage(position);
                current_position = position;

            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
        findViewById(com.loveplusplus.demo.image.R.id.iv_caogao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImagePagerActivity2.this, TuyaActivity.class));
            }
        });


        findViewById(com.loveplusplus.demo.image.R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_video) {
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("position", current_position);
                    setResult(1, intent);
                    finish();
                }
            }
        });

        if (is_video) {
            iv_collect.setVisibility(View.VISIBLE);
            iv_hudong.setVisibility(View.VISIBLE);


//			if(daan_url.length()>0){
//				iv_daan.setVisibility(View.VISIBLE);
//			}
//			if(!TextUtils.isEmpty(video_url)){
//				iv_shipin.setVisibility(View.VISIBLE);
//			}
            iv_yidu.setVisibility(View.VISIBLE);
            iv_yidu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferencesUtils.setBoolean(ImagePagerActivity2.this, current_id, true);
                    iv_yidu.setImageResource(com.loveplusplus.demo.image.R.drawable.icon_dui);
                }
            });
            iv_daan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//					if(!TextUtils.isEmpty(daan)){
//						showDaan(daan);
//					}else {
                    //展示答案
//						if(daan_url!=null){
                    Intent intent = new Intent(ImagePagerActivity2.this, ImagePagerActivity2.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity2.EXTRA_IMAGE_URLS, daan_urls);
                    intent.putExtra(ImagePagerActivity2.EXTRA_IMAGE_INDEX, current_position);
//							intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_IDS, video_ids);
//							if(!TextUtils.isEmpty(daan)){
//								intent.putExtra(ImagePagerActivity.EXTRA_DAAN ,daan);
//							}
//							intent.putExtra(ImagePagerActivity.EXTRA_DAANS ,daans);
//							if(!TextUtils.isEmpty(daan_url)){
////                        if(video.getAnswer_image().startsWith("http")){
////                            ans_imgurl=video.getAnswer_image();
////                        }else {
////                            ans_imgurl=ApiUtils.STATIC_HOST+video.getAnswer_image();
////                        }
//								intent.putExtra(ImagePagerActivity.EXTRA_DAAN_URL,daan_url);
//							}
//							intent.putExtra(ImagePagerActivity.EXTRA_DAAN_URLS,daan_urls);
//							intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_URLS,video_urls);
//							intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_URL,video_url);
//							intent.putExtra(ImagePagerActivity.EXTRA_IS_VIDEO,false);
                    startActivityForResult(intent, 1);
//						}else {
//							Toast.makeText(ImagePagerActivity.this,"该视频未上传答案",Toast.LENGTH_SHORT).show();
//						}
//					}
                }
            });
            iv_shipin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("intent.action.sun");
                    intent.putExtra("type", 1);
                    intent.putExtra("url", video_url);
                    sendBroadcast(intent);
                }
            });
        }
    }

    private void updatepage(final int position) {
        if (is_video) {
            daan = daans[position];
            daan_url = daan_urls[position];
            video_url = video_urls[position];
            current_id = video_ids[position];
            if (SharedPreferencesUtils.getBoolean(ImagePagerActivity2.this, current_id, false)) {
                iv_yidu.setImageResource(com.loveplusplus.demo.image.R.drawable.icon_dui_green);
            } else {
                iv_yidu.setImageResource(com.loveplusplus.demo.image.R.drawable.icon_cuo_green);
            }
            if (data_beans.get(position).isIscollect()) {
                iv_collect.setImageResource(R.drawable.ali_shoucang2);
            } else {
                iv_collect.setImageResource(R.drawable.icon_shoucang_green);
            }
            if (!TextUtils.isEmpty(data_beans.get(position).getAnswer_image())) {
                iv_daan.setVisibility(View.VISIBLE);
            } else {
                iv_daan.setVisibility(View.INVISIBLE);
            }
//                    Log.e("TAG",data_beans.get(position).getSnapshotUrl_image());
//                    Log.e("TAG",data_beans.get(position).getOrigUrl());
            if (!TextUtils.isEmpty(data_beans.get(position).getOrigUrl())) {
                iv_shipin.setVisibility(View.VISIBLE);
            } else {
                iv_shipin.setVisibility(View.GONE);
            }
            iv_hudong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                            Intent intent = new Intent();
//                            intent.setAction("intent.action.sun");
//                            intent.putExtra("type", 3);
//                            intent.putExtra("video_id", data_beans.get(position).getId());
//                            sendBroadcast(intent);
                    Intent intent=new Intent(ImagePagerActivity2.this,HudongActivity.class);
                    intent.putExtra("video_id",data_beans.get(position).getId());
                    startActivity(intent);
                }
            });
            iv_collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data_beans.get(position).isIscollect()) {
                        ApiUtils.getInstance().video_canclecollect(com.netease.nim.demo.common.util.SharedPreferencesUtils.getInt(ImagePagerActivity2.this, "account_id", 0) + "",
                                data_beans.get(position).getId() + "", course, new ApiListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        MyUtils.showToast(ImagePagerActivity2.this, "取消成功");
                                        iv_collect.setImageResource(R.drawable.icon_shoucang_green);
                                    }

                                    @Override
                                    public void onFailed(String errorMsg) {
                                        MyUtils.showToast(ImagePagerActivity2.this, errorMsg);
                                    }
                                });
                    } else {
                        ApiUtils.getInstance().video_collect(com.netease.nim.demo.common.util.SharedPreferencesUtils.getInt(ImagePagerActivity2.this, "account_id", 0) + "",
                                data_beans.get(position).getId() + "", course, new ApiListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        MyUtils.showToast(ImagePagerActivity2.this, "收藏成功");
                                        iv_collect.setImageResource(R.drawable.ali_shoucang2);
                                    }

                                    @Override
                                    public void onFailed(String errorMsg) {
                                        MyUtils.showToast(ImagePagerActivity2.this, errorMsg);
                                    }
                                });
                    }

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public String[] fileList;

        public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList[position];
            return ImageDetailFragment.newInstance(url);
        }
    }

    public void showDaan(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dView = View.inflate(this, com.loveplusplus.demo.image.R.layout.dialog_daan1, null);
        final AlertDialog dialog = builder.create();
        final TextView tv_content = (TextView) dView.findViewById(com.loveplusplus.demo.image.R.id.tv_content);
        tv_content.setText(content);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
    }
//	private OnClickCallback<Integer> mClickCallback;
//	public void setMyClick(OnClickCallback<Integer> clickCallback){
//		mClickCallback=clickCallback;
//	}
//	//阿里云服务器返回数据回调接口
//	public interface OnClickCallback<T> {
//		void myClick(T t);
//	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		Log.e("TAG",requestCode+"??"+resultCode+"??"+data.getIntExtra("position",0));
        switch (requestCode) {
            case 1:
//				current_position=data.getIntExtra("position",0);
//				current_id=video_ids[current_position];
                mPager.setCurrentItem(data.getIntExtra("position", 0));
                break;
        }
    }
}