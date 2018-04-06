package com.meapps.merememberer.activities;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.view.*;
import com.meapps.merememberer.*;
import java.io.*;


public class MainActivity extends AppCompatActivity {
	private boolean onBackDown = false;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Toolbar toolbar =(Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(toolbar);
		App.addActivity(this);
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
            .setTitle(R.string.about).setMessage(R.string.about_summary)
            .setNeutralButton(R.string.source_code, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface p1, int p2) {
                    Uri uri=Uri.parse("https://github.com/zhaozihanzzh/MeRememberer");
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            })
            .setNegativeButton(android.R.string.ok, null).show();
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
            builder.setTitle(R.string.changelogs).setMessage(result).setPositiveButton(android.R.string.ok, null).create().show();
        } catch (IOException e) {} 
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }
        if(onBackDown){
            App.finishAll();
        }
        Snackbar.make(getCurrentFocus(), R.string.press_again_to_exit, Snackbar.LENGTH_SHORT).show();
        onBackDown = true;
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    Thread.sleep(3000);
                    onBackDown = false;
                    // It seems that we must put onBackDown = false here.
                 } catch (InterruptedException e) {}
            }
        }).start();
        return false;
    }
}
