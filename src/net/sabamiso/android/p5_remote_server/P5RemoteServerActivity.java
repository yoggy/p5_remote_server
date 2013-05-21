package net.sabamiso.android.p5_remote_server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class P5RemoteServerActivity extends Activity {

	P5RemoteServerView view;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
		
		view = new P5RemoteServerView(this);
		setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.start();
	}

	@Override
	protected void onPause() {
		view.stop();
		super.onPause();
		finish();
	}
}
