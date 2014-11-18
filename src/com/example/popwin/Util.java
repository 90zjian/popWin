package com.example.popwin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.popwin.net.FetchTask;
import com.example.popwin.net.sqlite.App;
import com.example.popwin.util.Common;
import com.example.popwin.util.LogUtil;
import com.legame.eightplatform.R;
import com.legame.np.util.SharedPreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Util {

    public static ArrayList<HashMap<String, Object>> getList(int num){
  	   ArrayList<HashMap<String , Object>> item=new ArrayList<HashMap<String , Object>>();
  	   for (int i=0;i<num;i++){
  		   HashMap<String , Object> map=new HashMap<String, Object>();
  		   map.put("itemImage", "Image"+i);
  		   map.put("itemName", "name-"+i);
  		   item.add(map);
  	   }
		return item;
    }
    
	public static ArrayList<HashMap<String, Object>> getList(Activity ac) {
		PackageManager pm = ac.getPackageManager();
		List<PackageInfo> lp = pm.getInstalledPackages(0);
		ArrayList<HashMap<String, Object>> ah = new ArrayList<HashMap<String, Object>>();
		List<App> lApp=new ArrayList<App>();
		Boolean flag=false;
    	List<App> list = FetchTask.saveTask(SharedPreferencesUtil.getValue(ac, "postResult", FetchTask.demoJson()), ac);
		for(App listapp:list){
			flag=false;
			for(PackageInfo listpi:lp){
				if(listapp.getPackageName().equals(listpi.packageName)){
					ah.add(getmap(pm, listpi));
					flag=true;
					LogUtil.e("initList", ah);
					break;
				}else{
					continue;
				}
			}
			if(flag==false){
				lApp.add(listapp);
				LogUtil.e("initList", lApp);
			}
		}
		Collections.shuffle(lApp);
		MainActivity.arrDownMap.clear();
		MainActivity.arrDownMap.addAll(lApp);
		return ah;
	}

//    public static ArrayList<HashMap<String, Object>> getDownList(){
//    	ArrayList<HashMap<String, Object>> ah=new ArrayList<HashMap<String, Object>>();
//    	ah=getList(MainActivity.myActivity);
//		return ah;
//    }
    
    private static HashMap<String, Object> Hmap;
    @SuppressLint("NewApi") public static HashMap<String, Object> getmap(PackageManager pm, PackageInfo pi){
    	Hmap=new HashMap<String, Object>();
    	Hmap.put("itemImage", pi.applicationInfo.loadIcon(pm));
    	Hmap.put("itemName", pi.applicationInfo.loadLabel(pm));
    	Drawable da=pi.applicationInfo.loadIcon(pm);
    	System.out.println(((BitmapDrawable)da).getBitmap().getByteCount()+"--uncompress size"+pi.applicationInfo.loadLabel(pm));
    	Intent intent = pm.getLaunchIntentForPackage(pi.packageName);
    	Hmap.put("itemIntent", intent);
    	return Hmap;
    }

	public static void addshortcut(Activity ac){
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");  
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, ac.getString(R.string.app_name));  
	    shortcut.putExtra("duplicate", false); //�������ظ�����  
	    System.out.println(MainActivity.class.getName()+"===========");
	    ComponentName comp = new ComponentName(ac.getPackageName(),MainActivity.class.getName());  
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp)); 
	    ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(ac, R.drawable.icon);  
	    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);  
	    
	          
	    ac.sendBroadcast(shortcut); 
	}
	

	public static void copyBigDataToSD(Context con) throws IOException 
    {
        InputStream myInput;
        final String saveUrl = Common.getSavePath(con, con.getResources().getString(R.string.defaultFileName));
        File file=new File(saveUrl);
        if(file.exists()) {
        	LogUtil.e("filecopy", "file-exists");
        	return;
        }
        OutputStream myOutput = new FileOutputStream(saveUrl);  
        myInput = con.getAssets().open(MainActivity.myActivity.getResources().getString(R.string.defaultFileName)+".apk");  
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    }
	
    public static void removeFiles(String fileDir) {
        File pushf = new File(fileDir);
        if (pushf.exists()) {
            boolean isDelete = pushf.delete();
            //LogUtil.e(TAG, "delete " + fileDir + "is : " + isDelete);
        }
    }
	
	public static App searchApp(App app, List<App> tmp) {
		Iterator<App> it;
		App ap = null;
		for (it = tmp.iterator(); it.hasNext();) {
			ap = it.next();
			LogUtil.e("searchApp",
					ap.getPackageName() + "===" + app.getPackageName());
			if (ap.getPackageName().equals(app.getPackageName())) {
				LogUtil.e("searchApp", "return here!!!!!");
				return ap;
			}
		}
		return null;
	}

	public static void copyImageToSD(MainActivity con, String str) throws IOException {
		// TODO Auto-generated method stub

        InputStream myInput;
        final String saveUrl = Common.getImagePath(con, str);
        File file=new File(saveUrl);
        if(file.exists()) {
        	LogUtil.e("filecopy", "file-exists");
        	return;
        }
        OutputStream myOutput = new FileOutputStream(saveUrl);  
        myInput = con.getAssets().open(str+".png");
        byte[] buffer = new byte[1024];  
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length); 
            length = myInput.read(buffer);
        }
        
        myOutput.flush();  
        myInput.close();  
        myOutput.close();        
    
	}
}
