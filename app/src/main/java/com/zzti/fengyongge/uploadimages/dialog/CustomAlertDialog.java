package com.zzti.fengyongge.uploadimages.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.zzti.fengyongge.uploadimages.R;
import com.zzti.fengyongge.uploadimages.app.Config;
import com.zzti.fengyongge.uploadimages.view.RoundProgressBar;


/**
 * 自定义alertdialog,用于进度更新
 * @author fengyongge
 *
 */
public class CustomAlertDialog extends AlertDialog {
	private RoundProgressBar mProgress;
	private int mMax;
	private int mProgressVal;
	private boolean mHasStarted;
	private int width;
	public CustomAlertDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected CustomAlertDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected CustomAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_update);
		initData();
	}

	private void initData(){
		mProgress = (RoundProgressBar) findViewById(R.id.roundProgressBar);
		width= Config.ScreenMap.get("width")/4;
		mProgress.getLayoutParams().height=width;
		mProgress.getLayoutParams().width=width;
		mProgress.setTextSize(120);
	}




	public void setProgress(int value) {
		if (mHasStarted) {
			mProgress.setProgress(value);

		} else {
			mProgressVal = value;
		}
	}

	public int getProgress() {
		if (mProgress != null) {
			return mProgress.getProgress();
		}
		return mProgressVal;
	}

	public void setMax(int max) {
		if (mProgress != null) {
			mProgress.setMax(max);

		} else {
			mMax = max;
		}
	}

	public int getMax() {
		if (mProgress != null) {
			return mProgress.getMax();
		}
		return mMax;
	}

	@Override
	public void onStart() {
		super.onStart();
		mHasStarted = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHasStarted = false;
	}

}
