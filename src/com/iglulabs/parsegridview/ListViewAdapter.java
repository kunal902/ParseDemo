package com.iglulabs.parsegridview;

import java.util.List;

import com.iglulabs.parsedemo.GridViewItemActivity;
import com.iglulabs.parsedemo.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private Context context;
	private List<ImageList> imagearraylist = null;

	public ListViewAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(context);
	}

	public void setListItem(List<ImageList> imagearraylist) {
		this.imagearraylist = imagearraylist;
	}

	public class ViewHolder {
		ImageView imageItem;
	}

	public int getCount() {
		return imagearraylist.size();
	}

	public Object getItem(int position) {
		return imagearraylist.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.gridview_item, null);
			holder.imageItem = (ImageView) view.findViewById(R.id.imageItem);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		imageLoader.DisplayImage(imagearraylist.get(position).getUrl(),
				holder.imageItem);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(context, GridViewItemActivity.class);
				intent.putExtra("imagePath",
						(imagearraylist.get(position).getUrl()));
				context.startActivity(intent);
			}
		});
		return view;
	}
}
