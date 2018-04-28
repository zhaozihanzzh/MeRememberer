package com.meapps.merememberer.activities;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.ViewGroup.*;
import com.meapps.merememberer.*;
import java.io.*;


public final class MainActivity extends AppCompatActivity {
	private boolean onBackDown = false;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		App.addActivity(this);
        setContentView(R.layout.main);
		Toolbar toolbar =(Toolbar) findViewById(R.id.main_toolbar);
		LayoutParams params = toolbar.getLayoutParams();
		params.height = params.height + (int) getResources().getDimension(R.dimen.tool_bar_padding_top);
		// We MUST use params.height instead of toolbar.getHeight()!
		toolbar.setLayoutParams(params);
		setSupportActionBar(toolbar);
		NavigationView navView = (NavigationView) findViewById(R.id.main_nav_view);
		navView.inflateMenu(R.menu.navigation_menu);
		navView.inflateHeaderView(R.layout.navigation_header);
		ViewGroup header = (ViewGroup) navView.getHeaderView(0);
		header.getChildAt(0).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					startActivity(new Intent(MainActivity.this, SettingsActivity.class));
					mDrawerLayout.closeDrawer(GravityCompat.START);
				}
			});
		header.getChildAt(1).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					showAboutDialog();
					mDrawerLayout.closeDrawer(GravityCompat.START);
				}
			});

		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		navView.setCheckedItem(R.id.nav_menu_fill_in_the_blank);
		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem) {
					mDrawerLayout.closeDrawers();
					switch (menuItem.getItemId()) {

					}
					return true;
				}
			});
		final boolean[] closedManually = {false};
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 
												  R.string.drawer_opened, R.string.drawer_closed) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				return;
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				closedManually[0] = true;
				// Make sure that if we close the drawer and open it again in 3 seconds, the drawer won't be closed.
				return;
			}
		};
		mDrawerToggle.syncState();
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.openDrawer(GravityCompat.START);

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

		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					runOnUiThread(new Runnable(){
							@Override
							public void run() {
								if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && ! closedManually[0]) {
									mDrawerLayout.closeDrawer(GravityCompat.START);
								}
							}
						});
				} catch (InterruptedException e) {}
			}
		}.start();

    }

	@Override
	protected void onDestroy() {
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
			.setNegativeButton(R.string.changelogs, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2) {
					showChangeLogDialog();
				}
			})
            .setPositiveButton(android.R.string.ok, null).show();
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
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        if (onBackDown) {
            App.finishAll();
        }
        Snackbar.make(getCurrentFocus(), R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show();
        onBackDown = true;
        new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						onBackDown = false;
						// It seems that we must put onBackDown = false here.
					} catch (InterruptedException e) {}
				}
			}).start();
        return false;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				break;
		}
		return true;
	}
}
