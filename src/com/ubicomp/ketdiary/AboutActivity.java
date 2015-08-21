package com.ubicomp.ketdiary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.CustomToastSmall;
import com.ubicomp.ketdiary.ui.CustomTypefaceSpan;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * About Activity  "關於"頁面
 * 
 * @author Stanley Wang
 */
public class AboutActivity extends Activity {

	// TextView of About Activity
	private TextView titleText, aboutText, copyrightText, about, phone,
			phone_number, email, website;
	// ImageView of About Activity
	private ImageView logo, logo0, logo1, logo2;

	/** @see Typefaces */
	private Typeface wordTypeface, wordTypefaceBold, digitTypeface,
			digitTypefaceBold;

	// Link & COPYRIGHT
	private static final String EMAIL = "ubicomplab.ntu@gmail.com";
	//private static final String WEBSITE = "http://mll.csie.ntu.edu.tw/soberdiary/mobile/knowledge.php";
	private static final String COPYRIGHT = "\u00a9 2015 National Taiwan University,Intel-NTU Connected Context Computing Center, and Taipei City Hospital";

	/** Hidden state machine for entering developer page */
	private int hiddenState;

	// Activity
	private Activity activity;

	// LayoutInflater
	private LayoutInflater inflater;

	// RelativeLayout
	private RelativeLayout callLayout, bgLayout;

	// TextView in callLayout
	private TextView callOK, callCancel, callHelp;

	@Override
	/**OnCreate of the activity*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		activity = this;

		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		bgLayout = (RelativeLayout) this
				.findViewById(R.id.about_background_layout);
		titleText = (TextView) this.findViewById(R.id.about_title);
		phone = (TextView) this.findViewById(R.id.about_phone);
		phone_number = (TextView) this.findViewById(R.id.about_phone_number);
		email = (TextView) this.findViewById(R.id.about_email);
		//website = (TextView) this.findViewById(R.id.about_website);
		about = (TextView) this.findViewById(R.id.about_about);
		aboutText = (TextView) this.findViewById(R.id.about_content);
		logo = (ImageView) this.findViewById(R.id.about_logo);
		logo0 = (ImageView) this.findViewById(R.id.about_logo0);
		logo1 = (ImageView) this.findViewById(R.id.about_logo1);
		logo2 = (ImageView) this.findViewById(R.id.about_logo2);
		copyrightText = (TextView) this.findViewById(R.id.about_copyright);

		titleText.setTypeface(wordTypefaceBold);
		about.setTypeface(wordTypefaceBold);
		aboutText.setTypeface(wordTypefaceBold);
		phone.setTypeface(wordTypeface);
		phone_number.setTypeface(digitTypefaceBold);
		email.setTypeface(digitTypefaceBold);
		copyrightText.setTypeface(digitTypeface);
		copyrightText.setText(COPYRIGHT);

		logo.setOnTouchListener(new View.OnTouchListener() {
			@Override
			/** Change hiddenState and verify if the user can access DeveloperActivity*/
			public boolean onTouch(View v, MotionEvent event) {
				if (hiddenState == 0)
					++hiddenState;
				else if (hiddenState == 4) {
					//Intent newIntent = new Intent(activity,DevActivity.class);
					boolean debug = PreferenceControl.isDebugMode();
					
					if(debug){
						Intent newIntent = new Intent(activity,PreSettingActivity.class);
						activity.startActivity(newIntent);
					}
					else{
						Intent newIntent = new Intent(activity,DeveloperActivity.class);
						activity.startActivity(newIntent);
					}
					
				} else
					hiddenState = 0;
				return false;
			}
		});
		logo0.setOnTouchListener(new View.OnTouchListener() {
			@Override
			/**Change hiddenState*/
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (hiddenState == 1)
						++hiddenState;
					else
						hiddenState = 0;
				}
				return false;
			}
		});
		logo1.setOnTouchListener(new View.OnTouchListener() {
			@Override
			/**Change hiddenState*/
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (hiddenState == 2)
						++hiddenState;
					else
						hiddenState = 0;
				}
				return false;
			}
		});
		logo2.setOnTouchListener(new View.OnTouchListener() {
			@Override
			/**Change hiddenState*/
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (hiddenState == 3)
						++hiddenState;
					else
						hiddenState = 0;
				}
				return false;
			}
		});

		String[] message = getResources().getStringArray(R.array.about_message);
		String ntu = getString(R.string.ntu);
		String dot = getString(R.string.dot);
		String intel_ntu = getString(R.string.intel_ntu);
		String taipei_city_hospital = getString(R.string.taipei_city_hospital);
		String happ_design = getString(R.string.happ_design);

		String curVersion = getString(R.string.current_version);
		String rickie_wu = getString(R.string.rickie_wu);
		String yuga_huang = getString(R.string.yuda_huang);
		String versionName = " unknown";
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
		}

		Spannable helpSpannable = new SpannableString(message[0] + "\n" + ntu
				+ dot + intel_ntu + dot + "\n" + taipei_city_hospital + message[1]
				+ "\n\n" + message[2] + "\n" + message[3] + "\n" + message[4] 
				+ "\n\n" +curVersion + versionName + "\n" + message[5] 
				+ happ_design 
				);
		int start = 0;
		int end = message[0].length() + 1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1", wordTypeface,
				0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + ntu.length() + dot.length() + intel_ntu.length()
				+ dot.length() + taipei_city_hospital.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom2",
				wordTypefaceBold, 0xFF727171), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end + 1;
		end = start + message[1].length() + 2 + message[2].length() + 1
				+ message[3].length() + 1 + message[4].length() + 1 + curVersion.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1", wordTypeface,
				0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + versionName.length() + 1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",
				digitTypefaceBold, 0xFF727171), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[5].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1", wordTypeface,
				0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + happ_design.length() + 1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",
				digitTypefaceBold, 0xFF727171), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		start = end;
//		end = start + message[5].length();
//		helpSpannable.setSpan(new CustomTypefaceSpan("custom1", wordTypeface,
//				0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		start = end;
//		end = start + rickie_wu.length() + 1;
//		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",
//				digitTypefaceBold, 0xFF727171), start, end,
//				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		start = end;
//		end = start + message[6].length();
//		helpSpannable.setSpan(new CustomTypefaceSpan("custom1", wordTypeface,
//				0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		start = end;
//		end = start + yuga_huang.length() + 1;
//		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",
//				digitTypefaceBold, 0xFF727171), start, end,
//				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		aboutText.setText(helpSpannable);

		inflater = LayoutInflater.from(this);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_callout_check, null);
		initializeCallCheckDialog();

		phone_number.setOnClickListener(new CallCheckOnClickListener());
		email.setOnClickListener(new EmailOnClickListener());
		//website.setOnClickListener(new WebsiteOnClickListener());
	}

	/** Initialize the dialog to check if the user call out to the developer */
	private void initializeCallCheckDialog() {

		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);

		callHelp.setTypeface(wordTypefaceBold);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTypeface(wordTypefaceBold);
	}

	/** OnClickListener for clicking and showing call check dialog */
	private class CallCheckOnClickListener implements View.OnClickListener {

		@Override
		/**Click and show the dialog*/
		public void onClick(View v) {
			bgLayout.addView(callLayout);

			RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) callLayout
					.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;

			callHelp.setText(R.string.phone_check);
			callOK.setOnClickListener(new CallOutOnClickListener());
			callCancel.setOnClickListener(new CallCancelOnClickListener());
			phone_number.setOnClickListener(null);
			email.setOnClickListener(null);
			//website.setOnClickListener(null);
			ClickLog.Log(ClickLogId.ABOUT_CALL);
		}
	}

	/** OnClickListener for canceling and dismiss the call check dialog */
	private class CallCancelOnClickListener implements View.OnClickListener {

		@Override
		/**Cancel and dismiss the call check dialog*/
		public void onClick(View v) {
			bgLayout.removeView(callLayout);
			phone_number.setOnClickListener(new CallCheckOnClickListener());
			email.setOnClickListener(new EmailOnClickListener());
			//website.setOnClickListener(new WebsiteOnClickListener());
			ClickLog.Log(ClickLogId.ABOUT_CALL_CANCEL);
		}

	}

	/** OnClickListener for calling out */
	private class CallOutOnClickListener implements View.OnClickListener {
		@Override
		/**Calling out*/
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.ABOUT_CALL_OK);
			Intent intentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:0233664926"));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}

	/** OnClickListener for sending email */
	private class EmailOnClickListener implements View.OnClickListener {

		@Override
		/**Call outside app for sending email*/
		public void onClick(View v) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { EMAIL });

			String uid = PreferenceControl.getUID();
			if (uid.equals("sober_default_test")) {
				CustomToastSmall.generateToast(R.string.email_reject);
				return;
			}
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject)
					+ " " + uid);
			try {
				startActivity(Intent.createChooser(i,
						getString(R.string.email_message)));
			} catch (android.content.ActivityNotFoundException ex) {
				CustomToastSmall.generateToast(R.string.email_fail);
			}
			ClickLog.Log(ClickLogId.ABOUT_EMAIL);
		}
	}

	/** OnClickListener for going to our mobile website */
//	private class WebsiteOnClickListener implements View.OnClickListener {
//
//		@Override
//		/**Call app to browse the website*/
//		public void onClick(View v) {
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//					Uri.parse(WEBSITE));
//			startActivity(browserIntent);
//			ClickLog.Log(ClickLogId.ABOUT_WEBSITE);
//		}
//	}


	/**Override for hijacking BACK key*/
	/*
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (callLayout.getParent() != null
					&& callLayout.getParent().equals(bgLayout)) {
				bgLayout.removeView(callLayout);
				phone_number.setOnClickListener(new CallCheckOnClickListener());
				email.setOnClickListener(new EmailOnClickListener());
				website.setOnClickListener(new WebsiteOnClickListener());
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}*/

	@Override
	/**onResume of AboutActivity. Override for ClickLog*/
	public void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.ABOUT_ENTER);
	}

	@Override
	/**onPause of AboutActivity. Override for ClickLog*/
	public void onPause() {
		ClickLog.Log(ClickLogId.ABOUT_LEAVE);
		super.onPause();
	}

}