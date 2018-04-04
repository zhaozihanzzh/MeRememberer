package com.meapps.merememberer.activities;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import com.meapps.merememberer.*;
import java.io.*;

public class MainActivity extends Activity {
	private boolean isDarkMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		App.addActivity(this);
        isDarkMode = getIntent().getBooleanExtra("dark_mode", isDarkMode);
        LogUtils.d("Dark:" + isDarkMode);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int installedVersion = -1;
		try {
			final int VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            installedVersion = preferences.getInt("install_version", -1);
            if (installedVersion < VERSION) {
                showChangeLogDialog();
                preferences.edit().putInt("install_version", VERSION).commit();
            }

            if (installedVersion == -1) {
                showAboutDialog();
            }
        } catch (PackageManager.NameNotFoundException e) {}

    }

	@Override
	protected void onDestroy(){
		super.onDestroy();
		App.removeActivity(this);
	}
	
	private void showAboutDialog() {
        AlertDialog.Builder aboutDialog=new AlertDialog.Builder(this);
        aboutDialog.setCancelable(true).setIcon(R.drawable.ic_launcher)
            .setTitle("关于").setMessage(R.string.about)
            .setNeutralButton("源代码", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    Uri uri=Uri.parse("https://github.com/zhaozihanzzh/MeRememberer");
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            })
            .setNegativeButton("好", null).show();
    }
    private void showChangeLogDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        try {
            InputStream is = getAssets().open("changelogs.txt");
            int lenght = is.available();  
            byte[]  buffer = new byte[lenght];  
            is.read(buffer);
            is.close();
            String result = new String(buffer, "utf8");
            builder.setTitle("更新日志").setMessage(result).setPositiveButton("好", null).create().show();
        } catch (IOException e) {} 
    }

}
