package net.sabamiso.android.p5_remote_server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class P5RemoteServerView extends View implements
		RemoteServerUpdateBitmapListener {
	RemoteServerTiny remote_server;
	private Bitmap bitmap;

	Paint p_default;
	Paint p_text_white;
	Paint p_text_black;

	Handler handler = new Handler();

	GestureDetector gesture_detector;
	P5RemoteServerViewGuestureListener guesture_listener;

	boolean debug = false;

	public P5RemoteServerView(Context context) {
		super(context);
		setBackgroundColor(Color.BLACK);

		p_default = new Paint();

		p_text_white = new Paint();
		p_text_white.setColor(Color.WHITE);
		p_text_white.setTypeface(Typeface.DEFAULT_BOLD);

		p_text_black = new Paint();
		p_text_black.setColor(Color.BLACK);
		p_text_black.setTypeface(Typeface.DEFAULT_BOLD);

		guesture_listener = new P5RemoteServerViewGuestureListener(this);
		gesture_detector = new GestureDetector(context, guesture_listener);
	}

	public boolean getDebug() {
		return this.debug;
	}

	public void setDebug(boolean val) {
		this.debug = val;

		handler.post(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		});
	}

	public void start() {
		stop();
		remote_server = new RemoteServerTiny(12345);
		remote_server.setUpdateBitmapListener(this);
		boolean rv = remote_server.start();

		if (rv == false) {
			Toast.makeText(getContext(), "listen port failed...port="
					+ remote_server.getLietenPort(), Toast.LENGTH_LONG).show();
		}
	}

	public void stop() {
		if (remote_server != null) {
			remote_server.stop();
		}
	}

	@Override
	public void updateBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;

		handler.post(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		});
	}

	@Override
	public void onDraw(Canvas canvas) {

		drawBitmap(canvas);
		drawDebugInfo(canvas);
	}

	void drawBitmap(Canvas canvas) {
		if (bitmap == null)
			return;
		if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0)
			return;

		//
		int screen_w = getWidth();
		int screen_h = getHeight();
		float screen_aspect = (float) screen_w / (float) screen_h;

		int bitmap_w = bitmap.getWidth();
		int bitmap_h = bitmap.getHeight();
		float bitmap_aspect = (float) bitmap_w / (float) bitmap_h;

		int x, y, w, h;
		if (screen_aspect >= bitmap_aspect) {
			w = (int) (screen_h * bitmap_aspect);
			h = screen_h;
			x = (screen_w - w) / 2;
			y = 0;
		} else {
			w = screen_w;
			h = (int) (screen_w / bitmap_aspect);
			x = 0;
			y = (screen_h - h) / 2;
		}

		Rect src = new Rect(0, 0, bitmap_w, bitmap_h);
		Rect dst = new Rect(x, y, x + w, y + h);

		canvas.drawBitmap(bitmap, src, dst, p_default);
	}

	private void drawDebugInfo(Canvas canvas) {
		if (!debug)
			return;

		int size = 42;
		drawText(canvas, 10, 30 + size * 0, size,
				"p5_remote_server for android");
		drawText(canvas, 30, 30 + size * 1, size, "ip address = "
				+ getIpAddress());
		drawText(canvas, 30, 30 + size * 2, size, "listen port = "
				+ remote_server.getLietenPort());
	}

	public void drawText(Canvas canvas, float x, float y, int size, String msg) {
		p_text_white.setTextSize(size);
		p_text_black.setTextSize(size);

		for (int dy = -2; dy <= 2; dy += 2) {
			for (int dx = -2; dx <= 2; dx += 2) {
				canvas.drawText(msg, x + dx, y + dy, p_text_black);
			}
		}

		canvas.drawText(msg, x, y, p_text_white);
	}

	private String getIpAddress() {
		WifiManager wifi_manager = (WifiManager) getContext().getSystemService(
				Context.WIFI_SERVICE);
		WifiInfo info = wifi_manager.getConnectionInfo();

		int ip = info.getIpAddress();
		if (ip == 0) {
			return "ERROR: please check wifi connection...";
		}

		String ip_str = "" + ((ip >> 0) & 0xFF) + "." + ((ip >> 8) & 0xFF)
				+ "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);

		return ip_str;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gesture_detector.onTouchEvent(event);
	}
}

class P5RemoteServerViewGuestureListener extends SimpleOnGestureListener {
	P5RemoteServerView view;

	public P5RemoteServerViewGuestureListener(P5RemoteServerView view) {
		this.view = view;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		view.setDebug(!view.getDebug());
		return super.onDown(e);
	}
}