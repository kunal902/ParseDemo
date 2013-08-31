package com.iglulabs.parsedemo;

import com.iglulabs.parsegridview.ImageLoader;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewItemActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_griditem);
		TextView demobutton = (TextView) findViewById(R.id.demobutton);
		Button logoutbutton = (Button) findViewById(R.id.logoutbutton);
		ImageView backbutton = (ImageView) findViewById(R.id.backbutton);
		ImageView paintView = (ImageView) findViewById(R.id.basepic);
		String imagePath = getIntent().getStringExtra("imagePath");
		ImageLoader imageloader = new ImageLoader(this);
		ParseUser currentUser = ParseUser.getCurrentUser();
		demobutton.setText(currentUser.getUsername());
		logoutbutton.setOnClickListener(this);
		backbutton.setOnClickListener(this);
		imageloader.DisplayImage(imagePath, paintView);
	}

	public void showDialog(String message) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.setMessage(message);
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
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
