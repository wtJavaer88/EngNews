package com.wnc.news.engnews.ui.window;

import com.example.engnews.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WindowUtils {
	private static final String LOG_TAG = "WindowUtils";
	private static View mView = null;
	private static WindowManager mWindowManager = null;
	private static Context mContext = null;

	public static Boolean isShown = false;
	private static long AUTO_CLOSE_TIME = 5000;
	private static Thread antoCloseThread;
	private static String lock = "LLlock";

	/**
	 * 显示五秒后自动关闭
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showPopupWindow5Seconds(final Context context,
			final String msg) {
		showPopupWindow(context, msg);
		antoCloseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						isShown = true;
						lock.wait(AUTO_CLOSE_TIME);
						WindowUtils.hidePopupWindow();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						isShown = false;
					}

				}
			}
		});
		antoCloseThread.start();
	}

	/**
	 * 显示弹出框
	 * 
	 * @param context
	 */
	public static void showPopupWindow(final Context context, String msg) {
		if (isShown) {
			// 两次弹窗的间隔时间很短,强制取消上一次线程任务
			antoCloseThread.interrupt();
			hidePopupWindow();
		}

		// 获取应用的Context
		mContext = context.getApplicationContext();
		// 获取WindowManager
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		mView = setUpView(context, msg);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

		// 类型
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

		// 设置flag, 解决全屏模式显示不全问题 https://www.jianshu.com/p/3304dc5106ef
		int flags =  WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
		params.flags = flags;
		// 不设置这个弹出框的透明遮罩显示为黑色
		params.format = PixelFormat.TRANSLUCENT;
		// FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
		// 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
		// 不设置这个flag的话，home页的划屏会有问题

		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;

		params.gravity = Gravity.TOP;

		mWindowManager.addView(mView, params);
	}

	static View view;
	static TextView mAddress;

	private static View setUpView(Context context, final String msg) {
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.activity_window, null);
			mAddress = (TextView) view.findViewById(R.id.et_word);
		}
		mAddress.setText(msg);

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				System.out.println("isShown:" + isShown);
				// 如果线程正在等待, 则中断执行
				if (isShown) {
					antoCloseThread.interrupt();
				} else {
					hidePopupWindow();
				}
			}
		};

		view.setOnClickListener(onClickListener);
		return view;
	}

	/**
	 * 隐藏弹出框
	 */
	public static void hidePopupWindow() {
		if (null != mView) {
			mWindowManager.removeView(mView);
		}
		isShown = false;
	}
}