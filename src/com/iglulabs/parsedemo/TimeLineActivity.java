package com.iglulabs.parsedemo;

import java.util.ArrayList;
import java.util.List;
import com.iglulabs.assets.CustomScrollView;
import com.iglulabs.assets.CustomScrollView.OnRefreshListener;
import com.iglulabs.parsegridview.ListViewAdapter;
import com.iglulabs.parsegridview.ImageList;
import com.iglulabs.parsegridview.Utils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TimeLineActivity extends Activity implements OnClickListener {
	List<ParseObject> parseobject;
	private ProgressDialog mProgressDialog;
	private ListViewAdapter leftadapter;
	private ListViewAdapter middleadapter;
	private ListViewAdapter rightadapter;
	private ListView rightList;
	private ListView leftList;
	private ListView midleList;
	private List<ImageList> leftimagelists = null;
	private List<ImageList> middleimagelists = null;
	private List<ImageList> rightimagelists = null;
	private int maxpullToRefreshLimit;
	private int minpullToRefreshLimit;
	private int numberOfLists = 3;
	public static int maxHeight = 0;
	private CustomScrollView scrollview;
	private int listViewWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_timeline);
		TextView demobutton = (TextView) findViewById(R.id.demobutton);
		Button logoutbutton = (Button) findViewById(R.id.logoutbutton);
		ImageView backbutton = (ImageView) findViewById(R.id.backbutton);
		scrollview = (CustomScrollView) findViewById(R.id.timelineview);
		rightList = (ListView) findViewById(R.id.list_view_right);
		midleList = (ListView) findViewById(R.id.list_view_middle);
		leftList = (ListView) findViewById(R.id.list_view_left);
		leftimagelists = new ArrayList<ImageList>();
		middleimagelists = new ArrayList<ImageList>();
		rightimagelists = new ArrayList<ImageList>();
		listViewWidth = Utils.windowWidth / 3;
		leftadapter = new ListViewAdapter(this);
		middleadapter = new ListViewAdapter(this);
		rightadapter = new ListViewAdapter(this);
		maxpullToRefreshLimit = 2 * ApplicationConstants.pullToRefreshLimit;
		minpullToRefreshLimit = ApplicationConstants.pullToRefreshLimit;
		ParseUser currentUser = ParseUser.getCurrentUser();
		demobutton.setText(currentUser.getUsername());
		logoutbutton.setOnClickListener(this);
		backbutton.setOnClickListener(this);
		scrollview.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new GridDataTask(minpullToRefreshLimit, maxpullToRefreshLimit,
						true).execute();
			}
		});
		new GridDataTask(0, ApplicationConstants.pullToRefreshLimit, false)
				.execute();
	}

	private class GridDataTask extends AsyncTask<Void, Void, Void> {
		private int maxpullIndex;
		private boolean refreshFlag;
		private int minpullIndex;

		public GridDataTask(int minpullIndex, int maxpullIndex,
				boolean refreshFlag) {
			this.maxpullIndex = maxpullIndex;
			this.minpullIndex = minpullIndex;
			this.refreshFlag = refreshFlag;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(TimeLineActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
						ApplicationConstants.parseTableName);
				query.whereEqualTo("user", ParseUser.getCurrentUser());
				query.orderByDescending("createdAt");
				parseobject = query.find();
				for (int i = minpullIndex; (i < maxpullIndex)
						&& (i < parseobject.size()); i++) {
					ParseObject images = parseobject.get(i);
					ParseFile image = (ParseFile) images.get("image");
					int height = images.getInt("imageheight");
					int width = images.getInt("imagewidth");
					ImageList map = new ImageList();
					map.setUrl(image.getUrl());
					map.setHeight(height);
					map.setWidth(width);
					if ((i % numberOfLists) == 0) {
						leftimagelists.add(map);
					} else if ((i % numberOfLists) == 1) {
						middleimagelists.add(map);
					} else if ((i % numberOfLists) == 2) {
						rightimagelists.add(map);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			int rightHeight = getListViewHeightBasedOnChildren(rightimagelists);
			int middleHeight = getListViewHeightBasedOnChildren(middleimagelists);
			int leftHeight = getListViewHeightBasedOnChildren(leftimagelists);
			if ((leftHeight >= middleHeight) && (leftHeight >= rightHeight)) {
				if (middleHeight < rightHeight) {
					utilityfunctionToAdd(rightimagelists, middleimagelists,
							rightHeight, middleHeight);
				}
			} else if ((leftHeight < middleHeight)
					|| (leftHeight < rightHeight)) {
				int heightReduction;
				if (middleHeight > rightHeight) {
					heightReduction = utilityfunctionToAdd(middleimagelists,
							leftimagelists, middleHeight, leftHeight);
					if ((middleHeight - heightReduction) <= rightHeight) {
						utilityfunctionToAdd(rightimagelists, middleimagelists,
								rightHeight, middleHeight);
					}
				} else {
					heightReduction = utilityfunctionToAdd(rightimagelists,
							leftimagelists, rightHeight, leftHeight);
					if ((rightHeight - heightReduction) >= middleHeight) {
						utilityfunctionToAdd(rightimagelists, middleimagelists,
								rightHeight, middleHeight);
					}
				}
			}
			if (refreshFlag) {
				leftadapter.setListItem(leftimagelists);
				leftadapter.notifyDataSetChanged();
				middleadapter.setListItem(middleimagelists);
				middleadapter.notifyDataSetChanged();
				rightadapter.setListItem(rightimagelists);
				rightadapter.notifyDataSetChanged();
				maxpullToRefreshLimit += ApplicationConstants.pullToRefreshLimit;
				minpullToRefreshLimit += ApplicationConstants.pullToRefreshLimit;
			} else {
				leftadapter.setListItem(leftimagelists);
				leftList.setAdapter(leftadapter);
				middleadapter.setListItem(middleimagelists);
				midleList.setAdapter(middleadapter);
				rightadapter.setListItem(rightimagelists);
				rightList.setAdapter(rightadapter);
			}
			int setrightHeight = setListViewHeightBasedOnChildren(
					rightimagelists, rightList);
			int setmiddleHeight = setListViewHeightBasedOnChildren(
					middleimagelists, midleList);
			int setleftHeight = setListViewHeightBasedOnChildren(
					leftimagelists, leftList);
			maxHeight = (setrightHeight > setleftHeight) ? ((setrightHeight > setmiddleHeight) ? setrightHeight
					: setmiddleHeight)
					: (setleftHeight > setmiddleHeight ? setleftHeight
							: setmiddleHeight);
			scrollview.setRefreshFlag(true);
			mProgressDialog.dismiss();
		}
	}

	private int utilityfunctionToAdd(List<ImageList> maxlist,
			List<ImageList> minlist, int maxHeight, int minHeight) {
		int height = 0;
		for (int i = maxlist.size(); i > 0;) {
			ImageList imageItem = maxlist.get(i - 1);
			height += ((listViewWidth * imageItem.getHeight()) / imageItem
					.getWidth());
			if ((maxHeight - height) >= (minHeight + height)) {
				minlist.add(imageItem);
				maxlist.remove(i - 1);
				i--;
			} else {
				break;
			}
		}
		return height;
	}

	private int getListViewHeightBasedOnChildren(List<ImageList> inputList) {
		int totalHeight = 0;
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).getWidth() != 0) {
				totalHeight += ((listViewWidth * inputList.get(i).getHeight()) / inputList
						.get(i).getWidth());
			}
		}
		totalHeight += (Utils.convertDpToPixel(10, this) * inputList.size());
		return totalHeight;
	}

	private int setListViewHeightBasedOnChildren(List<ImageList> inputList,
			ListView listView) {
		int listViewWidth = Utils.windowWidth / 3;
		int totalHeight = 0;
		for (int i = 0; i < inputList.size(); i++) {
			if (inputList.get(i).getWidth() != 0) {
				totalHeight += ((listViewWidth * inputList.get(i).getHeight()) / inputList
						.get(i).getWidth());
			}
		}
		totalHeight += (Utils.convertDpToPixel(10, this) * inputList.size());
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);
		listView.requestLayout();
		return totalHeight;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.logoutbutton:
			ParseUser.logOut();
			finish();
			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivity(loginIntent);
			break;
		case R.id.backbutton:
			finish();
			break;
		}
	}

}
