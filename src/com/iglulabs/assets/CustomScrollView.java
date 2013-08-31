package com.iglulabs.assets;

import com.iglulabs.parsedemo.ApplicationConstants;
import com.iglulabs.parsedemo.TimeLineActivity;
import com.iglulabs.parsegridview.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

	private OnRefreshListener mOnRefreshListener;
	private boolean refreshFlag = false;
	private int minwindowHeight;
	private int minPullToRefreshYLimit;

	public CustomScrollView(Context context) {
		this(context, null);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		minwindowHeight = (int) (Utils.windowHeight - Utils.convertDpToPixel(
				80, context));
		int multiplier;
		if(Utils.windowWidth > 450){
			multiplier = 60;
		}else{
			multiplier = 30;
		}
		minPullToRefreshYLimit = ApplicationConstants.pullToRefreshLimit * multiplier;
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if ((TimeLineActivity.maxHeight != 0)
				&& ((y + minwindowHeight) > Math.max(minPullToRefreshYLimit, TimeLineActivity.maxHeight))) {
			if (refreshFlag) {
				refreshFlag = false;
				System.out.println("max height "+TimeLineActivity.maxHeight+" min window height "+minwindowHeight+" and y is "+y);
				onRefresh();
			}
		}
	}

	public boolean isRefreshFlag() {
		return refreshFlag;
	}

	public void setRefreshFlag(boolean refreshFlag) {
		this.refreshFlag = refreshFlag;
	}
}