package com.meapps.merememberer.activities;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import com.meapps.merememberer.*;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
	}
}
