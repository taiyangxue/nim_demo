package com.loveplusplus.demo.image;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.zdp.aseo.content.AseoZdpAseo;

public class MainActivity extends ListActivity {

	public static final String TAG = "MainActivity";
	private MyListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AseoZdpAseo.init(this, AseoZdpAseo.INSERT_TYPE);
		new MyTask().execute();

	}

	class MyTask extends AsyncTask<Void, Void, MyMessage> {

		@Override
		protected MyMessage doInBackground(Void... params) {
			String json = "{\"code\":200,\"msg\":\"ok\",list:["
					+ "{\"id\":110,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing.jpg\",\"name\":\"张三\",\"content\":\"大家好\",\"urls\":[]},"
					+ "{\"id\":111,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing2.jpg\",\"name\":\"李四\",\"content\":\"大家好\",\"urls\":[\"http://d.hiphotos.baidu.com/album/w%3D2048/sign=14b0934b78310a55c424d9f4837d42a9/a8014c086e061d95e9fd3e807af40ad163d9cacb.jpg\"]},"
					+ "{\"id\":112,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing1.jpg\",\"name\":\"王五\",\"content\":\"大家好\",\"urls\":[\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=ccd33b46d53f8794d7ff4b26e2207fc9/0d338744ebf81a4c0f993437d62a6059242da6a1.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D900%3Bq%3D90/sign=b8658f17f3d3572c62e290dcba28121a/5fdf8db1cb134954bb97309a574e9258d0094a47.jpg\"]},"
					+ "{\"id\":113,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing6.jpg\",\"name\":\"赵六\",\"content\":\"大家好\",\"urls\":[\"http://f.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=6b62f61bac6eddc422e7b7f309e0c7c0/6159252dd42a2834510deef55ab5c9ea14cebfa1.jpg\",\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=e58fb67bc8ea15ce45eee301863b4bce/a5c27d1ed21b0ef4fd6140a0dcc451da80cb3e47.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=cdab1512d000baa1be2c44b3772bc82f/91529822720e0cf3855c96050b46f21fbf09aaa1.jpg\"]}]}";
			Gson gson = new Gson();
			MyMessage msg = gson.fromJson(json, MyMessage.class);
			return msg;
		}

		@Override
		protected void onPostExecute(MyMessage result) {
			mAdapter = new MyListAdapter(MainActivity.this, result.list);
			setListAdapter(mAdapter);

		}

	}
}
