package com.iglulabs.parsedemo;

import com.iglulabs.parsegridview.Utils;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText username;
	private EditText password;
	private TextView errortext;
	private ProgressDialog mProgressDialog;
	private boolean initializeFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Utils.windowHeight = metrics.heightPixels;
		Utils.windowWidth = metrics.widthPixels;
		checkInternetConnection();
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.txtUname);
		password = (EditText) findViewById(R.id.txtpassword);
		errortext = (TextView) findViewById(R.id.errortext);
		Button okbutton = (Button) findViewById(R.id.okbutton);
		okbutton.setOnClickListener(this);
		mProgressDialog = new ProgressDialog(this);
	}

	private boolean checkInternetConnection() {
		if (isConnectingToInternet()) {
			if (initializeFlag) {
				return true;
			} else {
				Parse.initialize(this, ApplicationConstants.appId,
						ApplicationConstants.clientKey);
				initializeFlag = true;
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (currentUser != null) {
					finish();
					Intent paintIntent = new Intent(this, PaintActivity.class);
					startActivity(paintIntent);
				}
				return true;
			}
		} else {
			Toast.makeText(this, "Please connect to internet to use the app",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.okbutton:
			checkErrorAndValidate();
			break;
		}

	}

	private void checkErrorAndValidate() {
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("Checking Credentials...");
		mProgressDialog.show();
		final String userName = username.getText().toString();
		final String passwordText = password.getText().toString();
		if (userName.equals("")) {
			errortext.setText("Please enter user name");
		} else if (passwordText.equals("")) {
			errortext.setText("Please enter password");
		}
		if (checkInternetConnection()) {
			if (!userName.equals("") && !passwordText.equals("")) {
				ParseUser.logInInBackground(userName, passwordText,
						new LogInCallback() {
							public void done(ParseUser user,
									ParseException exception) {
								if (user != null) {
									mProgressDialog.dismiss();
									finish();
									Intent paintIntent = new Intent(
											LoginActivity.this,
											PaintActivity.class);
									startActivity(paintIntent);
								} else {
									ParseUser newUser = new ParseUser();
									newUser.setUsername(userName);
									newUser.setPassword(passwordText);
									newUser.signUpInBackground(new SignUpCallback() {
										public void done(
												ParseException exception) {
											if (exception == null) {
												mProgressDialog.dismiss();
												finish();
												Intent paintIntent = new Intent(
														LoginActivity.this,
														PaintActivity.class);
												startActivity(paintIntent);
											} else {
												mProgressDialog.dismiss();
												switch (exception.getCode()) {
												case ParseException.USERNAME_TAKEN:
													errortext
															.setText("Please enter correct password");
													break;
												}
											}
										}
									});
								}
							}
						});
			}else{
				mProgressDialog.dismiss();
			}
		}else{
			mProgressDialog.dismiss();
		}
	}
}
