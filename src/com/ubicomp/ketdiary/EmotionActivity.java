package com.ubicomp.ketdiary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.CustomToastSmall;
import com.ubicomp.ketdiary.ui.ScreenSize;
import com.ubicomp.ketdiary.ui.Typefaces;

/** Activity of Emotion DIY */
public class EmotionActivity extends Activity {

	private LayoutInflater inflater;
	private Typeface wordTypefaceBold;
	private RelativeLayout bgLayout, callLayout, animEndLayout, barLayout;
	private RelativeLayout.LayoutParams boxParam;
	private LinearLayout mainTop, mainLayout, titleLayout;
	private TextView callOK, callCancel, callHelp, animOK, animCancel,
			animHelp, endButton;
	private ImageView animLeft, animCenter, animationImg, barBg, bar, barStart,
			barEnd;
	private Activity activity;
	private MediaPlayer mediaPlayer;
	private AnimationDrawable animation;

	private static String[] solutionTexts;
	private DatabaseControl db;

	private int state = 0;
	private int intentType = -1;
	private int[] intentSequence = null;

	private CountDownTimer musicTimer;

	private int animId, mediaId;

	private static final int MIN_BARS = ScreenSize.getMinBars();

	private Runnable animRunnable = new AnimationRunnable();

	private static final int TYPE_SOCIAL = 4, TYPE_FAMILY = 5;
	private OnClickListener[] ClickListeners = {
			new AnimationSelectionOnClickListener(0),
			new AnimationSelectionOnClickListener(1),
			new AnimationSelectionOnClickListener(2),
			new RecreationOnClickListener(), new HelpOnClickListener(4),
			new HelpOnClickListener(5) };

	private final AnimationPlayPauseClickListener animationPlayPauseClickListener = new AnimationPlayPauseClickListener();
	private final AnimationStopClickListener animationStopClickListener = new AnimationStopClickListener();
	private final MediaOnCompletionListener mediaOnCompletionListener = new MediaOnCompletionListener();

	@Override
	/** onCreate*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion);

		// Receive intent from the caller
		Intent fromIntent = this.getIntent();
		this.intentType = fromIntent.getIntExtra("type", -2);
		this.intentSequence = fromIntent.getIntArrayExtra("seq");

		solutionTexts = getResources().getStringArray(
				R.array.emotionDIY_solution);

		activity = this;
		bgLayout = (RelativeLayout) findViewById(R.id.emotion_all_layout);
		titleLayout = (LinearLayout) findViewById(R.id.emotion_title_layout);
		mainLayout = (LinearLayout) findViewById(R.id.emotion_main_layout);
		mainTop = (LinearLayout) findViewById(R.id.emotion_main_top);
		inflater = (LayoutInflater) App.getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		callLayout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_callout_check, null);
		animEndLayout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_end_animation, null);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		initializeCallCheckDialog();
		initializeAnimEndDialog();
		db = new DatabaseControl();

		View title = BarButtonGenerator
				.createTitleView(R.string.emotionDIY_title);
		titleLayout.addView(title);

	}

	@Override
	/**onResume. Write click log and set questionns*/
	protected void onResume() {
		super.onResume();
		//ClickLog.Log(ClickLogId.EMOTION_DIY_ENTER);
		enableBack = true;
		setEmotionQuestion();
	}

	@Override
	/**onPause. Write click log and release resources*/
	protected void onPause() {
		if (callLayout != null)
			bgLayout.removeView(callLayout);
		if (animEndLayout != null)
			bgLayout.removeView(animEndLayout);
		if (musicTimer != null) {
			musicTimer.cancel();
			musicTimer = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		int item_count = mainLayout.getChildCount();
		for (int i = 0; i < item_count; ++i)
			mainLayout.getChildAt(i).setEnabled(true);
		//ClickLog.Log(ClickLogId.EMOTION_DIY_LEAVE);
		super.onPause();
	}

	/** Initialize call check dialog */
	private void initializeCallCheckDialog() {

		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout
				.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);

		callHelp.setTypeface(wordTypefaceBold);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTypeface(wordTypefaceBold);

	}

	/** Initialize Animation end check dialog */
	private void initializeAnimEndDialog() {

		animOK = (TextView) animEndLayout.findViewById(R.id.anim_ok_button);
		animCancel = (TextView) animEndLayout
				.findViewById(R.id.anim_cancel_button);
		animHelp = (TextView) animEndLayout.findViewById(R.id.anim_help);

		animHelp.setTypeface(wordTypefaceBold);
		animOK.setTypeface(wordTypefaceBold);
		animCancel.setTypeface(wordTypefaceBold);
	}

	/** Set questions of emotion */
	protected void setEmotionQuestion() { //modify by Andy
		state = 0;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		View tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help);
		mainLayout.addView(tv);

		for (int i = 0; i < solutionTexts.length; ++i) {
			//View v = BarButtonGenerator.createIconView(solutionTexts[i],DRAWABLE_ID[i], ClickListeners[i]);
			//mainLayout.addView(v);
		}

		int from = mainLayout.getChildCount();
		for (int i = from; i < MIN_BARS; ++i) {
			View v = BarButtonGenerator.createBlankView();
			mainLayout.addView(v);
		}

	}

	/**
	 * Ask the user which one he/she wants to call for
	 * 
	 * @param type
	 *            trigger reason type
	 */
	private void setCallQuestion(int type) {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		View tv = BarButtonGenerator.createTextView(R.string.call_to);
		mainLayout.addView(tv);

		String[] names = new String[3];
		String[] calls = new String[3];

		if (type == TYPE_FAMILY) {
			names = PreferenceControl.getConnectFamilyName();
			calls = PreferenceControl.getConnectFamilyPhone();
		} else if (type == TYPE_SOCIAL) {
			/*
			int[] idxs = PreferenceControl.getConnectSocialHelpIdx();
			names[0] = ConnectSocialInfo.NAME[idxs[0]];
			names[1] = ConnectSocialInfo.NAME[idxs[1]];
			names[2] = ConnectSocialInfo.NAME[idxs[2]];
			calls[0] = ConnectSocialInfo.PHONE[idxs[0]];
			calls[1] = ConnectSocialInfo.PHONE[idxs[1]];
			calls[2] = ConnectSocialInfo.PHONE[idxs[2]];*/
		}

		int counter = 0;
		for (int i = 0; i < 3; ++i) {
			OnClickListener listener = new CallCheckOnClickListener(type,
					names[i], calls[i]);
			String text = names[i];
			if (names[i].length() > 0) {
				View vv = BarButtonGenerator.createIconView(text,
						R.drawable.icon_call, listener);
				mainLayout.addView(vv);
				++counter;
			}
		}
		if (counter == 0) {
			mainLayout.removeAllViews();

			View tv2 = BarButtonGenerator
					.createTextView(R.string.emotion_connect_null);
			mainLayout.addView(tv2);
		}

		int from = mainLayout.getChildCount();
		for (int i = from; i < MIN_BARS; ++i) {
			View v = BarButtonGenerator.createBlankView();
			mainLayout.addView(v);
		}
	}

	/**
	 * Set the animation by the user's selection
	 * 
	 * @param selection
	 *            which animation help selected by the user
	 */
	private void setAnimationView(int selection) {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		View tv;
		switch (selection) {
		case 0:
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case0);
			animId = R.anim.animation_music;
			mediaId = R.raw.emotion_0;
			break;
		case 1:
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case1);
			animId = R.anim.animation_breath;
			mediaId = R.raw.emotion_1;
			break;
		case 2:
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case2);
			animId = R.anim.animation_walk;
			mediaId = R.raw.emotion_2;
			break;
		default:
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case1);
			animId = R.anim.animation_breath;
			mediaId = R.raw.emotion_1;
			break;
		}
		mainTop.addView(tv);

		if (animationImg != null) {
			animationImg.removeCallbacks(animRunnable);
		}
		if (animation != null) {
			animation.stop();
			animation = null;
		}
		if (musicTimer != null) {
			musicTimer.cancel();
			musicTimer = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}

		RelativeLayout av = null;
		av = (RelativeLayout) BarButtonGenerator.createAnimationView(animId);

		barLayout = (RelativeLayout) av
				.findViewById(R.id.question_progress_layout);
		barBg = (ImageView) av.findViewById(R.id.question_progress_bar_bg);
		bar = (ImageView) av.findViewById(R.id.question_progress_bar);
		barStart = (ImageView) av
				.findViewById(R.id.question_progress_bar_start);
		barEnd = (ImageView) av.findViewById(R.id.question_progress_bar_end);

		animationImg = (ImageView) av.findViewById(R.id.question_animation);
		animation = (AnimationDrawable) animationImg.getDrawable();
		if (Build.VERSION.SDK_INT < 14)
			animationImg.post(animRunnable);
		else
			animation.start();

		endButton = (TextView) av
				.findViewById(R.id.question_animation_right_button);
		endButton.setOnClickListener(new AnimCheckOnClickListener(selection));

		mediaPlayer = MediaPlayer.create(getApplicationContext(), mediaId);
		mediaPlayer.setOnCompletionListener(mediaOnCompletionListener);

		animLeft = (ImageView) av
				.findViewById(R.id.question_animation_left_button);
		animCenter = (ImageView) av
				.findViewById(R.id.question_animation_center_button);

		animCenter.setImageResource(R.drawable.icon_stop);
		animLeft.setImageResource(R.drawable.icon_pause);
		animCenter.setOnClickListener(animationStopClickListener);
		animLeft.setOnClickListener(animationPlayPauseClickListener);

		mainTop.addView(av);

		int total_time = mediaPlayer.getDuration();
		musicTimer = new MusicTimer(total_time);
		mediaPlayer.start();
		musicTimer.start();
	}

	/** OnClickListener for playing/pausing the animation */
	private class AnimationPlayPauseClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (musicTimer != null) {
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				animLeft.setImageResource(R.drawable.icon_play);
				animLeft.setOnClickListener(animationPlayPauseClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
				//ClickLog.Log(ClickLogId.EMOTION_DIY_PAUSE);
			} else {
				musicTimer = new MusicTimer(mediaPlayer.getDuration()
						- mediaPlayer.getCurrentPosition());
				mediaPlayer.start();
				musicTimer.start();
				animLeft.setImageResource(R.drawable.icon_pause);
				animLeft.setOnClickListener(animationPlayPauseClickListener);
				animCenter.setImageResource(R.drawable.icon_stop);
				animCenter.setOnClickListener(animationStopClickListener);
				animation.start();
				//ClickLog.Log(ClickLogId.EMOTION_DIY_PLAY);
			}
		}
	}

	/** OnClickListener for stopping the animation */
	private class AnimationStopClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (musicTimer != null) {
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				mediaPlayer.seekTo(0);
				animLeft.setImageResource(R.drawable.icon_play);
				animLeft.setOnClickListener(animationPlayPauseClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
				//ClickLog.Log(ClickLogId.EMOTION_DIY_STOP);
			}
		}
	}

	/** Handling what should do on the media completion */
	private class MediaOnCompletionListener implements
			MediaPlayer.OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (musicTimer != null) {
				musicTimer.cancel();
				musicTimer = null;
			}
			mp.seekTo(0);
			animLeft.setImageResource(R.drawable.icon_play);
			animLeft.setOnClickListener(animationPlayPauseClickListener);
			animCenter.setImageResource(0);
			animCenter.setOnClickListener(null);
			animation.stop();
		}
	}

	/**
	 * Set end view when the user select to do recreation
	 * 
	 * @param recreation
	 *            recreation selected by the user
	 */
	private void setRecreationEnd(String recreation) {
		state = 2;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		String text = getString(R.string.emotionDIY_help_case4) + recreation;
		View tv;
		tv = BarButtonGenerator.createTextView(text);
		mainLayout.addView(tv);
		View vv = BarButtonGenerator.createIconView(R.string.try_to_do,
				R.drawable.icon_ok, new EndOnClickListener(3, recreation));
		mainLayout.addView(vv);

		int from = mainLayout.getChildCount();
		for (int i = from; i < MIN_BARS; ++i) {
			View v = BarButtonGenerator.createBlankView();
			mainLayout.addView(v);
		}
	}

	/** Set questions for asking recreations */
	private void setRecreationQuestion() {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		String[] recreation = PreferenceControl.getRecreations();
		if (recreation[0].length() == 0)
			recreation[0] = getString(R.string.default_recreation_1);

		if (recreation[1].length() == 0)
			recreation[1] = getString(R.string.default_recreation_2);

		if (recreation[2].length() == 0)
			recreation[2] = getString(R.string.default_recreation_3);

		boolean[] has_value = { recreation[0].length() > 0,
				recreation[1].length() > 0, recreation[2].length() > 0,
				recreation[3].length() > 0, recreation[4].length() > 0 };

		boolean exist = false;

		for (int i = 0; i < has_value.length; ++i)
			exist |= has_value[i];

		View tv;
		if (exist)
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case3);
		else
			tv = BarButtonGenerator
					.createTextView(R.string.emotionDIY_help_case3_2);
		mainLayout.addView(tv);

		for (int i = 0; i < has_value.length; ++i) {
			if (has_value[i]) {
				View v = BarButtonGenerator.createIconView(recreation[i], 0,
						new RecreationSelectionOnClickListener(recreation[i]));
				mainLayout.addView(v);
			}
		}

		int from = mainLayout.getChildCount();
		for (int i = from; i < MIN_BARS; ++i) {
			View v = BarButtonGenerator.createBlankView();
			mainLayout.addView(v);
		}
	}

	/** OnClickListener at the end of Emotion DIY Activity */
	private class EndOnClickListener implements View.OnClickListener {
		private int selection;
		private String recreation = null;

		/**
		 * Constructor without recreation
		 * 
		 * @param selection
		 *            what method selected by the user
		 */
		EndOnClickListener(int selection) {
			this.selection = selection;
		}

		/**
		 * Constructor without recreation
		 * 
		 * @param selection
		 *            what method selected by the user
		 * @param recreation
		 *            recreation selected by the user
		 */
		EndOnClickListener(int selection, String recreation) {
			this.selection = selection;
			this.recreation = recreation;
		}

		@Override
		public void onClick(View v) {

			long ts = System.currentTimeMillis();
			//int addScore = db.insertEmotionDIY(new EmotionDIY(ts, selection,recreation, 0));
			int addScore2 = 0;
			if (intentType > -2) {
				//addScore2 = db.insertQuestionnaire(new Questionnaire(ts,intentType, seq_toString(), 0));
				PreferenceControl.setTestResult(-1);
			}
			//CustomToast.generateToast(R.string.emotionDIY_end_toast, addScore+ addScore2);
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
			activity.finish();
		}
	}

	/** OnClickListener for checking if stop the animation and leave Emotion DIY */
	private class AnimCheckOnClickListener implements View.OnClickListener {
		private int selection;

		/**
		 * Constructor
		 * 
		 * @param selection
		 *            method selected by the user
		 * */
		AnimCheckOnClickListener(int selection) {
			this.selection = selection;
		}

		@Override
		public void onClick(View v) {

			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				if (musicTimer != null) {
					musicTimer.cancel();
					musicTimer = null;
				}
				mediaPlayer.pause();
				animLeft.setImageResource(R.drawable.icon_play);
				animLeft.setOnClickListener(animationPlayPauseClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
			}

			animLeft.setEnabled(false);
			animCenter.setEnabled(false);
			animationImg.setEnabled(false);
			bgLayout.addView(animEndLayout);

			boxParam = (LayoutParams) animEndLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT);

			animOK.setOnClickListener(new EndOnClickListener(selection));
			animCancel.setOnClickListener(new AnimCancelOnClickListener());
			//ClickLog.Log(ClickLogId.EMOTION_DIY_END_PLAY);
		}
	}

	/** OnClickListener for canceling animation and leaving Emotion DIY */
	private class AnimCancelOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			animLeft.setEnabled(true);
			animCenter.setEnabled(true);
			animationImg.setEnabled(true);
			bgLayout.removeView(animEndLayout);
			//ClickLog.Log(ClickLogId.EMOTION_DIY_END_PLAY_CANCEL);
		}
	}

	/** Used for showing dialog to ask if the user wants to call out for help */
	private class CallCheckOnClickListener implements View.OnClickListener {

		private int selection;
		private String name;
		private String call;

		CallCheckOnClickListener(int selection, String name, String call) {
			this.selection = selection;
			this.name = name;
			this.call = call;
		}

		@SuppressLint("InlinedApi")
		@Override
		public void onClick(View v) {
			int item_count = mainLayout.getChildCount();
			for (int i = 0; i < item_count; ++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;

			bgLayout.addView(callLayout);

			boxParam = (LayoutParams) callLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT);

			String call_check = getResources().getString(
					R.string.call_check_help);
			String question_sign = getResources().getString(
					R.string.question_sign);
			callHelp.setText(call_check + " " + name + " " + question_sign);
			callOK.setOnClickListener(new CallOnClickListener(selection, name,
					call));
			callCancel.setOnClickListener(new CallCancelOnClickListener());
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}

	}

	/** OnClickListener for user selecting a recreation */
	private class RecreationSelectionOnClickListener implements
			View.OnClickListener {

		private String recreation;

		public RecreationSelectionOnClickListener(String recreation) {
			this.recreation = recreation;
		}

		@Override
		public void onClick(View v) {
			setRecreationEnd(recreation);
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}

	}

	/** Used for canceling calling out */
	private class CallCancelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			bgLayout.removeView(callLayout);
			int item_count = mainLayout.getChildCount();
			for (int i = 0; i < item_count; ++i)
				mainLayout.getChildAt(i).setEnabled(true);
			enableBack = true;
			//ClickLog.Log(ClickLogId.EMOTION_DIY_CALL_CANCEL);
		}

	}

	/** Used for calling out */
	private class CallOnClickListener implements View.OnClickListener {
		private int selection;
		private String call;

		// private String name;

		CallOnClickListener(int selection, String name, String call) {
			this.selection = selection;
			// this.name = name;
			this.call = call;
		}

		@Override
		public void onClick(View v) {
			long ts = System.currentTimeMillis();
			//db.insertEmotionDIY(new EmotionDIY(ts, selection, "", 0));
			if (intentType > -2) {
				//db.insertQuestionnaire(new Questionnaire(ts, intentType,seq_toString(), 0));
				PreferenceControl.setTestResult(-1);
			}
			//ClickLog.Log(ClickLogId.EMOTION_DIY_CALL_OK);
			Intent intentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + call));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}

	/** Parse intentSequence received from the caller activity */
	private String seq_toString() {
		int size = intentSequence.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			sb.append(intentSequence[i]);
			if (i < size - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	/** OnClickListener for selecting method with animation */
	private class AnimationSelectionOnClickListener implements
			View.OnClickListener {
		private int selection;

		AnimationSelectionOnClickListener(int selection) {
			this.selection = selection;
		}

		@Override
		public void onClick(View v) {
			setAnimationView(selection);
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	/** OnClickListener for selecting do recreation method */
	private class RecreationOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			setRecreationQuestion();
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	/** OnClickListener for finding help */
	private class HelpOnClickListener implements View.OnClickListener {
		private int type;

		/**
		 * Constructor
		 * 
		 * @param type
		 *            Type of the callee
		 */
		HelpOnClickListener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			setCallQuestion(type);
			//ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	/** CountDownTimer for the music progress bar */
	private class MusicTimer extends CountDownTimer {

		/**
		 * Constructor
		 * 
		 * @param totalMillis
		 *            duration of the music in millis
		 */
		public MusicTimer(long totalMillis) {
			super(totalMillis, 50);
		}

		@Override
		public void onFinish() {
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (bar != null) {
				RelativeLayout.LayoutParams barParam = (LayoutParams) bar
						.getLayoutParams();
				int total_len = barBg.getWidth() - barStart.getWidth()
						- barEnd.getWidth();
				barParam.width = total_len * mediaPlayer.getCurrentPosition()
						/ mediaPlayer.getDuration();
				barLayout.updateViewLayout(bar, barParam);
			}
		}
	}

	private boolean enableBack = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//ClickLog.Log(ClickLogId.EMOTION_DIY_RETURN);
			if (!enableBack)
				return false;
			if (animationImg != null) {
				animationImg.removeCallbacks(animRunnable);
			}
			if (animation != null) {
				animation.stop();
				animation = null;
			}
			if (musicTimer != null) {
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer != null) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
			if (animEndLayout != null && animEndLayout.getParent() != null
					&& animEndLayout.getParent().equals(bgLayout)) {
				bgLayout.removeView(animEndLayout);
				return false;
			}

			if (state == 0) {
				CustomToastSmall.generateToast(R.string.emotionDIY_toast);
				--state;
			} else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else {
				--state;
				if (state == 0)
					setEmotionQuestion();
				else if (state == 1)
					setRecreationQuestion();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class AnimationRunnable implements Runnable {
		@Override
		public void run() {
			if (animation != null)
				animation.start();
		}
	}
}
