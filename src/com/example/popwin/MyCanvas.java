package com.example.popwin;

import android.R;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MyCanvas {
	public static Drawable draw() {
		//����һ����Bitmap����   
		  
		   Bitmap bitmap = Bitmap.createBitmap(200, 100, Config.ARGB_8888) ;  
		  //����һ��canvas���󣬲��ҿ�ʼ��ͼ  
		   Canvas canvas = new Canvas (bitmap) ;  
		   canvas.drawText("hello", 0, 0, new Paint());
		   Drawable drawable = new BitmapDrawable(bitmap);
		   return drawable;
	}
}
