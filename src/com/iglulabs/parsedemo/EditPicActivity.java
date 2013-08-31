package com.iglulabs.parsedemo;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.iglulabs.parsegridview.Utils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EditPicActivity extends Activity implements OnClickListener {
	private PaintView paintView;
	private String imagePath;
	private Bitmap resultBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_editpic);
		TextView demobutton = (TextView) findViewById(R.id.demobutton);
		Button logoutbutton = (Button) findViewById(R.id.logoutbutton);
		Button clearbutton = (Button) findViewById(R.id.refreshbutton);
		Button savebutton = (Button) findViewById(R.id.savebutton);
		ImageView backbutton = (ImageView) findViewById(R.id.backbutton);
		paintView = (PaintView) findViewById(R.id.basepic);
		imagePath = getIntent().getStringExtra("imagePath");
		if (imagePath != null) {
			resultBitmap = Utils.decodeSampledBitmapFromFile(imagePath,
					Utils.windowWidth, Utils.windowHeight);
			paintView.setImageBitmap(resultBitmap);
		}
		ParseUser currentUser = ParseUser.getCurrentUser();
		demobutton.setText(currentUser.getUsername());
		logoutbutton.setOnClickListener(this);
		backbutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
		savebutton.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		if (resultBitmap != null) {
			resultBitmap.recycle();
			resultBitmap = null;
		}
		unbindDrawables(findViewById(R.id.editrootview));
		System.gc();
		super.onDestroy();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public void onBackPressed() {
		if (resultBitmap != null) {
			resultBitmap.recycle();
			resultBitmap = null;
		}
		super.onBackPressed();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		if ((resultBitmap == null) && imagePath != null) {
			resultBitmap = Utils.decodeSampledBitmapFromFile(imagePath,
					Utils.windowWidth, Utils.windowHeight);
			paintView.setImageBitmap(resultBitmap);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.paint_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.p_white_id: {
			paintView.changeColour(0);
			return true;
		}
		case R.id.p_blue_id: {
			paintView.changeColour(1);
			return true;
		}
		case R.id.p_green_id: {
			paintView.changeColour(2);
			return true;
		}
		case R.id.p_red_id: {
			paintView.changeColour(3);
			return true;
		}
		case R.id.p_black_id: {
			paintView.changeColour(4);
			return true;
		}

		case R.id.w_small: {
			paintView.changeWidth(5);
			return true;
		}
		case R.id.w_medium: {
			paintView.changeWidth(10);
			return true;
		}
		case R.id.w_large: {
			paintView.changeWidth(15);
			return true;
		}
		default: {
			return true;
		}
		}
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
		case R.id.refreshbutton:
			paintView.clearPoints();
			break;
		case R.id.savebutton:
			saveImage();
			break;
		}
	}

	private void saveImage() {
		paintView.setDrawingCacheEnabled(true);
		Bitmap snapShot = paintView.getDrawingCache();
		snapShot = Bitmap.createBitmap(snapShot, 0, 0, snapShot.getWidth(),
				snapShot.getHeight());
		final int imageHeight = snapShot.getHeight();
		final int imageWidth = snapShot.getWidth();
		final ProgressDialog saveDialog = new ProgressDialog(
				EditPicActivity.this);
		saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		saveDialog.setMessage("Saving...");
		saveDialog.show();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		snapShot.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] data = stream.toByteArray();
		final String fileName = System.currentTimeMillis() + "_demo.png";
		final ParseFile imageFile = new ParseFile(fileName, data);
		imageFile.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				if (ex == null) {
					ParseObject pObject = new ParseObject(
							ApplicationConstants.parseTableName);
					pObject.put("user", ParseUser.getCurrentUser());
					pObject.put("image", imageFile);
					pObject.put("imageheight", imageHeight);
					pObject.put("imagewidth", imageWidth);
					pObject.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException ex) {
							if (ex == null) {
								saveDialog.dismiss();
								finish();
								Intent timelineIntent = new Intent(
										EditPicActivity.this,
										TimeLineActivity.class);
								startActivity(timelineIntent);
							} else {
								saveDialog.dismiss();
								showDialog("Error while saving user image");
							}
						}
					});
				} else {
					saveDialog.dismiss();
					showDialog("Error while saving image");
				}
			}
		});
		File fileToBeDeleted = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/ParseTemp/");
		if (fileToBeDeleted != null && fileToBeDeleted.isDirectory()) {
			File[] files = fileToBeDeleted.listFiles();
			if (files != null) {
				for (File f : files) {
					f.delete();
				}
			}
		}
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
}
