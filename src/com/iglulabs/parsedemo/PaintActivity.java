package com.iglulabs.parsedemo;

import java.io.File;
import java.io.IOException;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PaintActivity extends Activity implements OnClickListener {
	private File photoFile;
	private final int CAMERA_RESULT = 1;
	private final int SELECT_PICTURE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_paint);
		TextView demobutton = (TextView) findViewById(R.id.demobutton);
		Button logoutbutton = (Button) findViewById(R.id.logoutbutton);
		Button camerabutton = (Button) findViewById(R.id.camerabutton);
		Button gallerybutton = (Button) findViewById(R.id.gallerybutton);
		Button timelinebutton = (Button) findViewById(R.id.timelinebutton);
		ParseUser currentUser = ParseUser.getCurrentUser();
		demobutton.setText(currentUser.getUsername());
		logoutbutton.setOnClickListener(this);
		camerabutton.setOnClickListener(this);
		gallerybutton.setOnClickListener(this);
		timelinebutton.setOnClickListener(this);
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
		case R.id.camerabutton:
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/ParseTemp/" + "camera.jpg";
				photoFile = new File(path);
				try {
					if (photoFile.exists()) {
						photoFile.delete();
						photoFile.createNewFile();
					} else {
						photoFile.getParentFile().mkdirs();
						photoFile.createNewFile();
					}
				} catch (IOException fileException) {
					fileException.printStackTrace();
				}
				Uri fileUri = Uri.fromFile(photoFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(intent, CAMERA_RESULT);
			} else {
				showDialog("External Storage (SD Card) is required.\nCurrent state: "
						+ Environment.getExternalStorageState());
			}
			break;
		case R.id.gallerybutton:
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				photoPickerIntent.setType("image/*");
				startActivityForResult(Intent.createChooser(photoPickerIntent,
						"Select Picture"), SELECT_PICTURE);
			} else {
				showDialog("No SDCARD Mounted.\nCurrent state: "
						+ Environment.getExternalStorageState());
			}
			break;
		case R.id.timelinebutton:
			Intent timelineIntent = new Intent(this, TimeLineActivity.class);
			startActivity(timelineIntent);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((resultCode == RESULT_OK) && (requestCode == CAMERA_RESULT)) {
			if (photoFile != null) {
				Intent editPicIntent = new Intent(this, EditPicActivity.class);
				editPicIntent.putExtra("imagePath", photoFile.getPath());
				startActivity(editPicIntent);
			} else {
				showDialog("Error while writing file");
			}
		}
		if ((resultCode == RESULT_OK) && (requestCode == SELECT_PICTURE)) {
			if (data != null) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				Intent editPicIntent = new Intent(this, EditPicActivity.class);
				editPicIntent.putExtra("imagePath", filePath);
				startActivity(editPicIntent);
			}
		}
	}

}
