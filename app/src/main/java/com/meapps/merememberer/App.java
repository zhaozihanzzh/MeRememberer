package com.meapps.merememberer;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import com.meapps.merememberer.activities.*;
import java.util.*;

public class App extends Application implements Thread.UncaughtExceptionHandler {
    private static List<Activity> mActivities = new ArrayList<>();
    public static void addActivity(Activity activity) {
        LogUtils.d(mActivities.add(activity) + " to Create activity " + activity);
    }
    public static List<Activity> getActivities() {
        return mActivities;
    }
    public static void removeActivity(Activity activity) {     
        LogUtils.d(mActivities.remove(activity) + " to Destroy activity " + activity);
    }
    public static void finishAll() {
        for (Activity a : mActivities) {
            if (!a.isFinishing()) {
                a.finish();
            }
        }
    }
    public static boolean getPermissionGranted(Activity activity, String name, int requestCode){
        LogUtils.d(activity.toString() + " requests "+ name);
        if(ContextCompat.checkSelfPermission(activity, name) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{name}, requestCode);
            return false;
        }
        return true;
    }
    public static Intent getDetailPermission(String packageName){
        Intent detailPermission = new Intent();
        detailPermission.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        detailPermission.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        detailPermission.setData(Uri.fromParts("package", packageName, null));
        return detailPermission;
    }

    @Override
    public void onCreate() {
        super.onCreate();
		//设置Thread Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(this);
        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
	@Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.e("Exception by MeRememberer: ",ex);
        final Intent intent = new Intent(this, Crash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  
                        Intent.FLAG_ACTIVITY_NEW_TASK);

        StringBuilder logWrongInfo = new StringBuilder();
        logWrongInfo.append("Model: " + Build.MODEL + "    SDK: " + Build.VERSION.SDK_INT
                            + "\n" + "Fingerprint: " + Build.FINGERPRINT
                            + "\n" + ex.toString());
        for(StackTraceElement e : ex.getStackTrace()){
            logWrongInfo.append("\n     at " +e.toString());
        }
        intent.putExtra("log", logWrongInfo.toString());
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit() or killProcess() is necessary here.
    }
}
