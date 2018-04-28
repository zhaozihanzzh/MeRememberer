package com.meapps.merememberer.activities;
import android.preference.*;
import android.os.*;
import com.meapps.merememberer.*;
import android.support.design.widget.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.content.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;

public final class SettingsActivity extends CompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        App.addActivity(this);
		setContentView(R.layout.settings);
		Toolbar toolbar =(Toolbar) findViewById(R.id.settings_toolbar);
		LayoutParams params = toolbar.getLayoutParams();
		params.height = params.height + (int) getResources().getDimension(R.dimen.tool_bar_padding_top);
		// We MUST use params.height instead of toolbar.getHeight()!
		toolbar.setLayoutParams(params);
		setSupportActionBar(toolbar);
        addPreferencesFromResource(R.xml.settings);
            final CheckBoxPreference darkMode = (CheckBoxPreference) findPreference("dark_mode");
            darkMode.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener(){
                @Override
                public boolean onPreferenceChange(Preference p, Object value){
                    AppCompatDelegate.setDefaultNightMode(darkMode.isChecked() ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
                    rebootToWork();
                    return true;
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.removeActivity(this);
    }
    private void rebootToWork(){
        Snackbar.make(getListView(), R.string.settings_need_reboot, Snackbar.LENGTH_SHORT)
                .setAction(R.string.reboot_now, new View.OnClickListener(){
                @Override
                public void onClick(View p1) {
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    App.finishAll();
                    startActivity(intent);
                }
        }).show();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
