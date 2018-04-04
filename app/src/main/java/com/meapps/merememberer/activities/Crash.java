package com.meapps.merememberer.activities;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meapps.merememberer.*;

/**
 * When app crashes, start this activity.
 * @author zhaozihanzzh
 */

public final class Crash extends Activity implements View.OnClickListener {
    private String log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        log = getIntent().getStringExtra("log");
        setContentView(R.layout.crash);
        final TextView error=(TextView)findViewById(R.id.crash_text);
        error.setText(log);
        findViewById(R.id.crash_copy).setOnClickListener(this);
        findViewById(R.id.crash_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.crash_close:
                finish();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                // Go to launcher to avoid the app restarts itself and enter the Crash again.
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.crash_copy:
                final android.text.ClipboardManager cmb=(android.text.ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(log);
                Toast.makeText(this, "已将Log复制到剪贴板。", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
