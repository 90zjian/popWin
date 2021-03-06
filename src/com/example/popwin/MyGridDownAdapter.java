package com.example.popwin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.popwin.net.sqlite.AdHandler;
import com.example.popwin.net.sqlite.App;
import com.example.popwin.service.DownloadService;
import com.example.popwin.util.LogUtil;
import com.legame.eightplatform.R;
import com.legame.np.util.ImageUtils;
import com.legame.np.util.TaskUtil;

public class MyGridDownAdapter extends BaseAdapter {

	private Context mContext;
	List<App> arrList = new ArrayList<App>();
	List<TextView> list_Down=new ArrayList<TextView>();
	List<TextView> list_Inst=new ArrayList<TextView>();
	Activity MainAc;
	// TextView tv;
	// View cv;
	int page;
	public static Handler compDownHandler;
	public static Handler compInstHandler;
	
	public static ImageFetcher mImageFetcher;
	
	public MyGridDownAdapter() {
	}

	public MyGridDownAdapter(Context context, List<App> list, Activity ac,
			int page) {
		mContext = context;
		this.page = page;
		if (list.size() > 0) {
			LogUtil.e("list.size", list.size());
			if (list.size() < 5)
				arrList.addAll(list);
			else
				arrList = list.subList(0, 4);
			LogUtil.e("arrList", arrList);
		}
		MainAc = ac;
		mImageFetcher = new ImageFetcher(context, 480);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arrList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public void updateGridDownView(List<App> list) {
		arrList.clear();
		if (list.size() < 5)
			arrList.addAll(list);
		else
			arrList = list.subList(0, 4);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final HolderDown holder;
		// Boolean clickable=true;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.popwininfo, null);
			holder = new HolderDown();
			holder.image = (ImageView) convertView.findViewById(R.id.ImageV);
			holder.name = (TextView) convertView.findViewById(R.id.textV2);
			// tv = (TextView) convertView.findViewById(R.id.textV2);
			// cv = convertView;
			convertView.setTag(holder);
		} else {
			holder = (HolderDown) convertView.getTag();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String link = (String) arrList.get(position).getUrl();
				System.out.println("click the GridDownButton");
				if (arrList.get(position).getStatus() != TaskUtil.STATUS_DOWNLOADING) {
					showDialog(arrList.get(position));
				}
			}

			private void showDialog(final App app) {
				View infoView = LayoutInflater.from(mContext).inflate(
						R.layout.infopop, null);
				ImageView imageView = (ImageView) infoView
						.findViewById(R.id.imageView);
				TextView nameText = (TextView) infoView
						.findViewById(R.id.nameView);
				TextView infoText = (TextView) infoView
						.findViewById(R.id.infoView);
				TextView sizeView= (TextView)infoView.findViewById(R.id.sizeView);
				Button button = (Button) infoView.findViewById(R.id.instButton);
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory
							.decodeStream(new FileInputStream(new File(
									ImageUtils.getLocalFileName(
											MainActivity.myActivity,
											app.getIconUrl()))));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageView.setImageBitmap(bitmap);
				nameText.setText(app.getAppName());
				infoText.setText(app.getDesc());
				sizeView.setText((double) ((app.getFileSize()*100)/1024/1024/100.0)+"M");

				final MyDialog md = new MyDialog(mContext, infoView);
				md.requestWindowFeature(Window.FEATURE_NO_TITLE);

				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						arrList.get(position).setStatus(
								TaskUtil.STATUS_DOWNLOADING);

						Intent intent = new Intent(MainActivity.myActivity,
								DownloadService.class);
						intent.putExtra(App.APPID, arrList.get(position)
								.getAppId());
						new AdHandler(mContext).putOneToDownload(arrList
								.get(position));
						MainActivity.myActivity.startService(intent);
						md.dismiss();
//						Message msg = new Message();
//						msg.what = 1;
//						msg.obj = app;
//						MainActivity.waitDownHandler.sendMessage(msg);
						list_Down.add(holder.name);
//						list_Inst.add(holder.name);
						compDownHandler=new Handler(){

							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								System.out.println(holder.name.getText());
//								holder.name.setText("下载完成");
								if(list_Down.size()>0){
									list_Down.get(0).setText("下载完成");
									list_Inst.add(list_Down.get(0));
									list_Down.remove(0);
								}
								super.handleMessage(msg);
							}
						};
						
						compInstHandler=new Handler(){

							@Override
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								switch(msg.what){
								case 1:
									if(list_Inst.size()>0){
										list_Inst.get(0).setText("已安装");
										list_Inst.remove(0);
									}
									break;
								case 2:
									if(list_Inst.size()>0){
										list_Inst.get(0).setText("未安装");
										list_Inst.remove(0);
									}
									break;
								default:
									list_Inst.remove(0);
									break;
								}
								super.handleMessage(msg);
							}
						};
						System.out.println(holder.name.getText().toString());
						holder.name.setText("下载中...");
					
					}
				});
				md.show();
			}

		});
		holder.name.setText((CharSequence) arrList.get(position).getAppName());
		App app = arrList.get(position);
//		Bitmap bitmap = null;
//		if (!new File(ImageUtils.getLocalFileName(MainActivity.myActivity,
//				app.getIconUrl())).exists()) {
//			bitmap = BitmapFactory.decodeResource(
//					MainActivity.myActivity.getResources(), R.drawable.loading);
//		} else
//			try {
//				bitmap = BitmapFactory.decodeStream(new FileInputStream(
//						new File(ImageUtils.getLocalFileName(
//								MainActivity.myActivity, app.getIconUrl()))));
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		holder.image.setImageBitmap(bitmap);
		mImageFetcher.loadImage(app.getIconUrl().toString(), holder.image);
		return convertView;
	}

	public class HolderDown {
		ImageView image;
		TextView name;
		Intent intent;
	}

	class netRun implements Runnable {

		public String fileName = "default";
		public String Link = "http://apk.r1.market.hiapk.com/data/upload/2014/09_29/13/com.iflytek.inputmethod_135036.apk";

		public netRun(String link, String itemName) {
			if (link != null)
				fileName = itemName;
			if (itemName != null)
				link = Link;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println();
			// String image=NetUtil.getApkmapFromServer(Link,fileName);
			Bitmap apk = NetUtil.getBitmapFromServer(Link);
		}
	}
}
