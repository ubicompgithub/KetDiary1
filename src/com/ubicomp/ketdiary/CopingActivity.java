package com.ubicomp.ketdiary;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.CopingSkill;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for All Coping Skill
 * 
 * @author Andy Chen & Blue
 */
public class CopingActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout, mainTop;

	private Activity activity;
	
	private View[] recreationViews;
	private View breathView, muscleView, stretchView, peacefulView, musicView, meditationView;
	private View awayView, toldView, repeatView;
	private View[] sponsorViews;
	private View encouragementView, harmView, lifestyleView, lapseView;

	
	private RelativeLayout bgLayout, callLayout, animEndLayout, barLayout;
	private TextView callOK, callCancel, callHelp, animOK, animCancel, animHelp, endButton;

	private int state = 0;
	private int animId, mediaId;
	private ImageView animLeft, animCenter, animationImg, barBg, bar, barStart, barEnd;
	private Runnable animRunnable = new AnimationRunnable();
	private AnimationDrawable animation;
	private CountDownTimer musicTimer;
	private MediaPlayer mediaPlayer;

	private final AnimationPlayPauseClickListener animationPlayPauseClickListener = new AnimationPlayPauseClickListener();
	private final AnimationStopClickListener animationStopClickListener = new AnimationStopClickListener();
	private final MediaOnCompletionListener mediaOnCompletionListener = new MediaOnCompletionListener();

	private DatabaseControl db;
	private int intentType = -1;
	private RelativeLayout.LayoutParams boxParam;
	private int[] intentSequence = null;
	private int skillType = -1, skillSelect = -1;
	private String send_recreation = "";
	
	//relax
	private static final int SELECT_BREATH = 0;
	private static final int SELECT_WALK = 1;
	private static final int SELECT_STRETCH = 2;
	private static final int SELECT_MUSIC = 3;
	
	//life coping
	private static final int SELECT_LEAVE = 4;
	private static final int SELECT_TOLD = 5;
	private static final int SELECT_CD = 6;
	
	//information
	private static final int SELECT_POSITIVE = 7;
	private static final int SELECT_POISON = 8;
	private static final int SELECT_SUGGESTION = 9;
	private static final int SELECT_HOW = 10;

	
	// These are used for folding lists
	private ImageView[] listDownImg = new ImageView[5];
	private boolean[] listVisible = new boolean[] {false, false, false, false, false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coping);

		// Receive intent from the caller
		Intent fromIntent = this.getIntent();
		this.intentType = fromIntent.getIntExtra("type", -2);
		this.intentSequence = fromIntent.getIntArrayExtra("seq");

		this.activity = this;
		titleLayout = (LinearLayout) this
				.findViewById(R.id.coping_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.coping_main_layout);
		mainTop = (LinearLayout) findViewById(R.id.coping_main_top);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		View title = BarButtonGenerator.createTitleView(R.string.coping_page);
		titleLayout.addView(title);
		bgLayout = (RelativeLayout) findViewById(R.id.coping_all_layout);
		
		callLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_callout_check, null);
		initializeCallCheckDialog();

		animEndLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_end_animation, null);
		db = new DatabaseControl();
		initializeAnimEndDialog();

		resetCopingActivityViews();
		
	}
 

	private void foldRelaxedView(){
		breathView.setVisibility(View.GONE);
		muscleView.setVisibility(View.GONE);
		stretchView.setVisibility(View.GONE);
		musicView.setVisibility(View.GONE);
		//meditationView.setVisibility(View.GONE);
		
		if(listDownImg[0] != null)
			listDownImg[0].setVisibility(View.INVISIBLE);

		listVisible[0] = false;
	}

	private void unfoldRelaxedView(){
		breathView.setVisibility(View.VISIBLE);
		muscleView.setVisibility(View.VISIBLE);
		stretchView.setVisibility(View.VISIBLE);
		musicView.setVisibility(View.VISIBLE);
		//meditationView.setVisibility(View.VISIBLE);
		
		if(listDownImg[0] != null)
			listDownImg[0].setVisibility(View.VISIBLE);

		listVisible[0] = true;
		skillType = 0;
	}

	private void foldRecreationView(){
		for (int i = 0; i < recreationViews.length; ++i)
			recreationViews[i].setVisibility(View.GONE);

		if(listDownImg[1] != null)
			listDownImg[1].setVisibility(View.INVISIBLE);

		listVisible[1] = false;
	}

	private void unfoldRecreationView(){
		for (int i = 0; i < recreationViews.length; ++i)
			recreationViews[i].setVisibility(View.VISIBLE);

		if(listDownImg[1] != null)
			listDownImg[1].setVisibility(View.VISIBLE);

		listVisible[1] = true;
		skillType = 1;
	}

	private void foldInterpersonView(){
		awayView.setVisibility(View.GONE);
		toldView.setVisibility(View.GONE);
		repeatView.setVisibility(View.GONE);
		
		if(listDownImg[2] != null)
			listDownImg[2].setVisibility(View.INVISIBLE);

		listVisible[2] = false;
	}

	private void unfoldInterpersonView(){
		awayView.setVisibility(View.VISIBLE);
		toldView.setVisibility(View.VISIBLE);
		repeatView.setVisibility(View.VISIBLE);
		
		if(listDownImg[2] != null)
			listDownImg[2].setVisibility(View.VISIBLE);

		listVisible[2] = true;
		skillType = 2;
	}
	
	private void foldSponsorView(){
		for (int i = 0; i < sponsorViews.length; ++i)
			sponsorViews[i].setVisibility(View.GONE);

		if(listDownImg[3] != null)
			listDownImg[3].setVisibility(View.INVISIBLE);

		listVisible[3] = false;
	}

	private void unfoldSponsorView(){
		for (int i = 0; i < sponsorViews.length; ++i)
			sponsorViews[i].setVisibility(View.VISIBLE);

		if(listDownImg[3] != null)
			listDownImg[3].setVisibility(View.VISIBLE);

		listVisible[3] = true;
		skillType = 3;
	}

	private void foldInfoView(){
		encouragementView.setVisibility(View.GONE);
		harmView.setVisibility(View.GONE);
		lifestyleView.setVisibility(View.GONE);
		lapseView.setVisibility(View.GONE);
		
		if(listDownImg[4] != null)
			listDownImg[4].setVisibility(View.INVISIBLE);

		listVisible[4] = false;
	}

	private void unfoldInfoView(){
		encouragementView.setVisibility(View.VISIBLE);
		harmView.setVisibility(View.VISIBLE);
		lifestyleView.setVisibility(View.VISIBLE);
		lapseView.setVisibility(View.VISIBLE);
		
		if(listDownImg[4] != null)
			listDownImg[4].setVisibility(View.VISIBLE);

		listVisible[4] = true;
		skillType = 4;
	}


	private void setRelaxedView(){
		RelativeLayout relaxedView = createListView(R.string.coping_relaxed,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						listDownImg[0] = (ImageView) v.findViewById(R.id.question_list);
						if (listVisible[0]) {
							foldRelaxedView();
						} else {
							unfoldRelaxedView();
							foldRecreationView();
							foldInterpersonView();
							foldSponsorView();
							foldInfoView();
						}
					}
				});
		mainLayout.addView(relaxedView);

		
		breathView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_breath, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_BREATH;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						setAnimationView(1);
						
					}

				});
		breathView.setVisibility(View.GONE);
		mainLayout.addView(breathView);
		
		muscleView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_muscle, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_WALK;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						setAnimationView(2);
						
					}

				});
		muscleView.setVisibility(View.GONE);
		mainLayout.addView(muscleView);
		
		stretchView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_stretch, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_STRETCH;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						setAnimationView(3);
					}

				});
		stretchView.setVisibility(View.GONE);
		mainLayout.addView(stretchView);
		
		musicView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_music, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_MUSIC;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						setAnimationView(0);
						
					}

				});
		
		musicView.setVisibility(View.GONE);
		mainLayout.addView(musicView);
		
		/*
		meditationView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_meditation, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		
		meditationView.setVisibility(View.GONE);
		mainLayout.addView(meditationView);*/
	}

	private void setRecreationView(){
		RelativeLayout recreationView = createListView(
				R.string.coping_recreation, new OnClickListener() {

					@Override
					public void onClick(View v) {
						listDownImg[1] = (ImageView) v.findViewById(R.id.question_list);
						// ImageView list = (ImageView) v
						// 		.findViewById(R.id.question_list);
						if (listVisible[1]) {
							foldRecreationView();
						} else {
							unfoldRecreationView();
							foldRelaxedView();
							foldInterpersonView();
							foldSponsorView();
							foldInfoView();
						}
					}
				});
		mainLayout.addView(recreationView);
		
		String[] recreations = PreferenceControl.getRecreations();
		String[] nonemptyRecreations = new String[recreations.length];
		int numOfRecreations = 0;
		for (int i = 0; i < recreations.length; ++i){
			if (recreations[i].length() > 0){
				nonemptyRecreations[numOfRecreations] = recreations[i];
				numOfRecreations++;
			}
		}
		recreationViews = new RelativeLayout[numOfRecreations];
		
		for (int i = 0; i < numOfRecreations; ++i) {
		 	recreationViews[i] = BarButtonGenerator.createSettingButtonView2(
		 			nonemptyRecreations[i], new RecreationOnClickListener(recreations[i]) );

		 	recreationViews[i].setVisibility(View.GONE);
		 	mainLayout.addView(recreationViews[i]);
		}

		/*
		recreationViews[0] = BarButtonGenerator.createSettingButtonView(
			R.string.coping_default_recreation0, new OnClickListener() {

				@Override
				public void onClick(View v) {
					generateDialog(R.string.coping_recreation_dialog);
				}

			});

		recreationViews[0].setVisibility(View.GONE);
		mainLayout.addView(recreationViews[0]);
		
		recreationViews[1] = BarButtonGenerator.createSettingButtonView(
			R.string.coping_default_recreation1, new OnClickListener() {

				@Override
				public void onClick(View v) {
					generateDialog(R.string.coping_recreation_dialog);
				}

			});

		recreationViews[1].setVisibility(View.GONE);
		mainLayout.addView(recreationViews[1]);

		recreationViews[2] = BarButtonGenerator.createSettingButtonView(
			R.string.coping_default_recreation2, new OnClickListener() {

				@Override
				public void onClick(View v) {
					generateDialog(R.string.coping_recreation_dialog);
				}

			});

		recreationViews[2].setVisibility(View.GONE);
		mainLayout.addView(recreationViews[2]);*/

		// for (int i = 0; i < recreations.length; ++i) {
		// 	recreationViews[i] = BarButtonGenerator.createSettingButtonView(
		// 		R.string.coping_default_recreation, new OnClickListener() {

		// 			@Override
		// 			public void onClick(View v) {
						
		// 			}

		// 		});

		// 	recreationViews[i].setVisibility(View.GONE);
		// 	mainLayout.addView(recreationViews[i]);
		// }


	}
	
	private class RecreationOnClickListener implements View.OnClickListener {
		private String recreation = null;
		
		public RecreationOnClickListener(String recreation){
			this.recreation = recreation;
		}
		
		@Override
		public void onClick(View v) {
			send_recreation = recreation;
			generateDialog(R.string.coping_recreation_dialog);
			
		}

	}

	private void setInterpersonView(){
		RelativeLayout interpersonView = createListView(R.string.coping_interperson,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						listDownImg[2] = (ImageView) v.findViewById(R.id.question_list);
						// ImageView list = (ImageView) v.findViewById(R.id.question_list);
						if (listVisible[2]) {
							foldInterpersonView();
						} else {
							unfoldInterpersonView();
							foldRelaxedView();
							foldRecreationView();
							foldSponsorView();
							foldInfoView();
						}
					}
				});
		mainLayout.addView(interpersonView);
		
		awayView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_away, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_LEAVE;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						generateDialog(R.string.coping_away_dialog);
						
					}

				});
		awayView.setVisibility(View.GONE);
		mainLayout.addView(awayView);
		
		toldView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_told, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_TOLD;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						generateDialog(R.string.coping_told_dialog);
						
					}

				});
		toldView.setVisibility(View.GONE);
		mainLayout.addView(toldView);

		repeatView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_repeat, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_CD;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						generateDialog(R.string.coping_repeat_dialog);
							
					}

				});
		repeatView.setVisibility(View.GONE);
		mainLayout.addView(repeatView);

	}

	
	private void setSponsorView(){
		RelativeLayout sponsorView = createListView(
				R.string.coping_sponsor, new OnClickListener() {
					@Override
					public void onClick(View v) {
						listDownImg[3] = (ImageView) v.findViewById(R.id.question_list);
						if (listVisible[3]) {
							foldSponsorView();
						} else {
							unfoldSponsorView();
							foldRelaxedView();
							foldRecreationView();
							foldInterpersonView();
							foldInfoView();
						}
					}
				});
		mainLayout.addView(sponsorView);
		

		String[] sponsorNames = PreferenceControl.getSponsorName();
		String[] sponsorPhones = PreferenceControl.getSponsorPhone();
		String[] nonemptySponsorNames = new String[sponsorNames.length];
		String[] nonemptySponsorPhones = new String[sponsorPhones.length];
		int numOfSponsors = 0;
		for (int i = 0; i < sponsorNames.length; i++){
			if (sponsorNames[i].length() > 0){
				nonemptySponsorNames[numOfSponsors] = sponsorNames[i];
				nonemptySponsorPhones[numOfSponsors] = sponsorPhones[i];
				numOfSponsors++;
			}
		}

		sponsorViews = new RelativeLayout[numOfSponsors];
		for (int i = 0; i < numOfSponsors; ++i) {
			OnClickListener listener = new CallCheckOnClickListener(nonemptySponsorNames[i], nonemptySponsorPhones[i]);
			sponsorViews[i] = BarButtonGenerator.createIconView(
				nonemptySponsorNames[i], R.drawable.icon_call, listener);

			sponsorViews[i].setVisibility(View.GONE);
			mainLayout.addView(sponsorViews[i]);
		}


	}


	private void setInfoView(){
		RelativeLayout infoView = createListView(R.string.coping_information,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						listDownImg[4] = (ImageView) v.findViewById(R.id.question_list);
						// ImageView list = (ImageView) v.findViewById(R.id.question_list);
						if (listVisible[4]) {
							foldInfoView();
						} else {
							unfoldInfoView();
							foldRelaxedView();
							foldRecreationView();
							foldInterpersonView();
							foldSponsorView();
						}
					}
				});
		mainLayout.addView(infoView);
		
		encouragementView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_encouragement, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_POSITIVE;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						String[] tmp = activity.getResources().getStringArray(R.array.question_reading_question);
						Random rand = new Random();
						int idx = rand.nextInt(tmp.length);
						generateDialog(tmp[idx]);
						
					}

				});
		encouragementView.setVisibility(View.GONE);
		mainLayout.addView(encouragementView);
		
		harmView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_harm, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_POISON;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						String[] tmp = activity.getResources().getStringArray(R.array.knowing_list);
						Random rand = new Random();
						int idx = rand.nextInt(tmp.length);
						generateDialog2(tmp[idx]);
						
					}

				});
		harmView.setVisibility(View.GONE);
		mainLayout.addView(harmView);

		lifestyleView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_lifestyle, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_SUGGESTION;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						String[] tmp = activity.getResources().getStringArray(R.array.lifestyle_list);
						Random rand = new Random();
						int idx = rand.nextInt(tmp.length);
						generateDialog(tmp[idx]);
						
					}

				});
		lifestyleView.setVisibility(View.GONE);
		mainLayout.addView(lifestyleView);

		lapseView = BarButtonGenerator.createSettingButtonView(
				R.string.coping_lapse, new OnClickListener() {

					@Override
					public void onClick(View v) {
						skillSelect = SELECT_HOW;
						ClickLog.Log(ClickLogId.COPING_SELECTION + skillSelect);
						String[] tmp = activity.getResources().getStringArray(R.array.lapse_list);
						Random rand = new Random();
						int idx = rand.nextInt(tmp.length);
						generateDialog(tmp[idx]);
						
					}

				});
		lapseView.setVisibility(View.GONE);
		mainLayout.addView(lapseView);

	}

	private void generateDialog(int textResource){
		// Create custom dialog object
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        dialog.setContentView(R.layout.dialog);
        TextView dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText(textResource);
        dialogText.setTextColor(getResources().getColor(R.color.dark_gray));
        
        dialog.show();
         
        TextView dialogOKButton = (TextView) dialog.findViewById(R.id.ok_button);
        /*dialogOKButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                
            }
        });*/
        dialogOKButton.setOnClickListener(new EndOnClickListener() );
	}
	
	private void generateDialog(String textResource){
		// Create custom dialog object
        final Dialog dialog = new Dialog(activity);
        
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        dialog.setContentView(R.layout.dialog);
        TextView dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText(textResource);
        dialogText.setTextColor(getResources().getColor(R.color.dark_gray));
        
        dialog.show();
         
        TextView dialogOKButton = (TextView) dialog.findViewById(R.id.ok_button);
        /*dialogOKButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                
            }
        });*/
        dialogOKButton.setOnClickListener(new EndOnClickListener() );
	}
	
	private void generateDialog2(String textResource){
		// Create custom dialog object
        final Dialog dialog = new Dialog(activity);
        
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        dialog.setContentView(R.layout.dialog);
        TextView dialogText = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogText.setText(Html.fromHtml(textResource));
        dialogText.setTextColor(getResources().getColor(R.color.dark_gray));
        
        dialog.show();
         
        TextView dialogOKButton = (TextView) dialog.findViewById(R.id.ok_button);
        /*dialogOKButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                
            }
        });*/
        dialogOKButton.setOnClickListener(new EndOnClickListener() );
	}
	

	private void resetCopingActivityViews(){

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		setRelaxedView();
		setRecreationView();
		setInterpersonView();
		setSponsorView();
		setInfoView();

		for (int i = 0; i < listVisible.length; i++)
			listVisible[i] = false;

		state = 0;
	}
	

	@Override
	protected void onResume() {
		ClickLog.Log(ClickLogId.COPING_ENTER);
		super.onResume();
	}

	@Override
	protected void onPause() {

		//PreferenceControl.setNotificationTimeIdx(notificationGroup.getResult());

//		BootBoardcastReceiver.setRegularNotification(getBaseContext(),
//				getIntent());
		ClickLog.Log(ClickLogId.COPING_LEAVE);
		super.onPause();
	}

	private RelativeLayout createListView(int titleStr, OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_list_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);
		layout.setOnClickListener(listener);
		return layout;
	}


	/** Initialize Animation end check dialog */
	private void initializeAnimEndDialog() {

		animOK = (TextView) animEndLayout.findViewById(R.id.anim_ok_button);
		animCancel = (TextView) animEndLayout.findViewById(R.id.anim_cancel_button);
		animHelp = (TextView) animEndLayout.findViewById(R.id.anim_help);

		animHelp.setTypeface(wordTypefaceBold);
		animOK.setTypeface(wordTypefaceBold);
		animCancel.setTypeface(wordTypefaceBold);
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
			// Set animation of listening to music
			tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help_case0);
			animId = R.anim.animation_music;
			mediaId = R.raw.emotion_0;
			break;
		case 1:
			// Set animation of deep breath
			tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help_case1);
			animId = R.anim.animation_breath;
			mediaId = R.raw.emotion_1;
			break;
		case 2:
			// Set animation of walk
			tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help_case2);
			animId = R.anim.animation_walk;
			mediaId = R.raw.emotion_2;
			break;
		case 3:	
			tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help_case2_2);
			animId = R.anim.animation_stretch;
			mediaId = R.raw.emotion_3;
			break;
		default:
			tv = BarButtonGenerator.createTextView(R.string.emotionDIY_help_case1);
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

		barLayout = (RelativeLayout) av.findViewById(R.id.question_progress_layout);
		barBg = (ImageView) av.findViewById(R.id.question_progress_bar_bg);
		bar = (ImageView) av.findViewById(R.id.question_progress_bar);
		barStart = (ImageView) av.findViewById(R.id.question_progress_bar_start);
		barEnd = (ImageView) av.findViewById(R.id.question_progress_bar_end);

		animationImg = (ImageView) av.findViewById(R.id.question_animation);
		animation = (AnimationDrawable) animationImg.getDrawable();
		if (Build.VERSION.SDK_INT < 14)
			animationImg.post(animRunnable);
		else
			animation.start();

		endButton = (TextView) av.findViewById(R.id.question_animation_right_button);
		endButton.setOnClickListener(new AnimCheckOnClickListener(selection));

		mediaPlayer = MediaPlayer.create(getApplicationContext(), mediaId);
		mediaPlayer.setOnCompletionListener(mediaOnCompletionListener);

		animLeft = (ImageView) av.findViewById(R.id.question_animation_left_button);
		animCenter = (ImageView) av.findViewById(R.id.question_animation_center_button);

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
				ClickLog.Log(ClickLogId.COPING_PAUSE);
			} else {
				musicTimer = new MusicTimer(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition());
				mediaPlayer.start();
				musicTimer.start();
				animLeft.setImageResource(R.drawable.icon_pause);
				animLeft.setOnClickListener(animationPlayPauseClickListener);
				animCenter.setImageResource(R.drawable.icon_stop);
				animCenter.setOnClickListener(animationStopClickListener);
				animation.start();
				ClickLog.Log(ClickLogId.COPING_PLAY);
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
				ClickLog.Log(ClickLogId.COPING_STOP);
			}
		}
	}

	/** Handling what should do on the media completion */
	private class MediaOnCompletionListener implements MediaPlayer.OnCompletionListener {

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
	// private void setRecreationEnd(String recreation) {
	// 	state = 2;

	// 	mainLayout.removeAllViews();
	// 	mainTop.removeAllViews();

	// 	String text = getString(R.string.emotionDIY_help_case4) + recreation;
	// 	View tv;
	// 	tv = BarButtonGenerator.createTextView(text);
	// 	mainLayout.addView(tv);
	// 	View vv = BarButtonGenerator.createIconView(R.string.try_to_do, R.drawable.icon_ok, new EndOnClickListener(3, recreation));
	// 	mainLayout.addView(vv);

	// 	int from = mainLayout.getChildCount();
	// 	for (int i = from; i < MIN_BARS; ++i) {
	// 		View v = BarButtonGenerator.createBlankView();
	// 		mainLayout.addView(v);
	// 	}
	// }

	/** Set questions for asking recreations */
	// prii

	/** OnClickListener at the end of Emotion DIY Activity */
	private class EndOnClickListener implements View.OnClickListener {
		private int selection;
		private String recreation = null;
		
		
		EndOnClickListener() {
		}
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
			 int addScore = db.insertCopingSkill(new CopingSkill(ts, skillType, skillSelect, send_recreation, 0));
			// int addScore2 = 0;
			// if (intentType > -2) {
			// 	addScore2 = db.insertQuestionnaire(new Questionnaire(ts, intentType, seq_toString(), 0));
			// 	PreferenceControl.setTestResult(-1);
			// }
			CustomToast.generateToast(R.string.emotionDIY_end_toast, addScore);
			PreferenceControl.setPoint(addScore);
			
			ClickLog.Log(ClickLogId.COPING_SELECTION);
			activity.finish();
		}
	}


	/** Initialize call check dialog */
	private void initializeCallCheckDialog() {

		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);

		callHelp.setTypeface(wordTypefaceBold);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTypeface(wordTypefaceBold);

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
			ClickLog.Log(ClickLogId.COPING_END_PLAY);
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
			ClickLog.Log(ClickLogId.COPING_END_PLAY_CANCEL);
		}
	}


	/** Used for showing dialog to ask if the user wants to call out for help */
	private class CallCheckOnClickListener implements View.OnClickListener {

		private String name;
		private String call;

		CallCheckOnClickListener(String name, String call) {
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

			String call_check = getResources().getString(R.string.call_check_help);
			String question_sign = getResources().getString(R.string.question_sign);
			callHelp.setText(call_check + " " + name + " " + question_sign);
			callOK.setOnClickListener(new CallOnClickListener(name, call));
			callCancel.setOnClickListener(new CallCancelOnClickListener());
//			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}

	}
//
//	/** OnClickListener for user selecting a recreation */
//	private class RecreationSelectionOnClickListener implements View.OnClickListener {
//
//		private String recreation;
//
//		public RecreationSelectionOnClickListener(String recreation) {
//			this.recreation = recreation;
//		}
//
//		@Override
//		public void onClick(View v) {
////			setRecreationEnd(recreation);
//			ClickLog.Log(ClickLogId.COPING_SELECTION);
//		}
//
//	}
//
//	/** Used for canceling calling out */
	private class CallCancelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			bgLayout.removeView(callLayout);
			int item_count = mainLayout.getChildCount();
			for (int i = 0; i < item_count; ++i)
				mainLayout.getChildAt(i).setEnabled(true);
			enableBack = true;
			ClickLog.Log(ClickLogId.COPING_CALL_CANCEL);
		}

	}

	/** Used for calling out */
	private class CallOnClickListener implements View.OnClickListener {
		private int selection;
		private String call;

		// private String name;

		CallOnClickListener(String name, String call) {
			// this.selection = selection;
			// this.name = name;
			this.call = call;
		}

		@Override
		public void onClick(View v) {
			// long ts = System.currentTimeMillis();
			// db.insertEmotionDIY(new EmotionDIY(ts, selection, "", 0));
			// if (intentType > -2) {
			// 	db.insertQuestionnaire(new Questionnaire(ts, intentType, seq_toString(), 0));
			// 	PreferenceControl.setTestResult(-1);
			// }
			// ClickLog.Log(ClickLogId.EMOTION_DIY_CALL_OK);
			Intent intentDial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + call));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}

//	/** Parse intentSequence received from the caller activity */
//	private String seq_toString() {
//		int size = intentSequence.length;
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < size; ++i) {
//			sb.append(intentSequence[i]);
//			if (i < size - 1)
//				sb.append(",");
//		}
//		return sb.toString();
//	}
//
//	/** OnClickListener for selecting method with animation */
//	private class AnimationSelectionOnClickListener implements View.OnClickListener {
//		private int selection;
//
//		AnimationSelectionOnClickListener(int selection) {
//			this.selection = selection;
//		}
//
//		@Override
//		public void onClick(View v) {
//			setAnimationView(selection);
//			ClickLog.Log(ClickLogId.COPING_SELECTION);
//		}
//	}
//
//	/** OnClickListener for selecting do recreation method */
//	private class Recreation123OnClickListener implements View.OnClickListener {
//		@Override
//		public void onClick(View v) {
////			setRecreationQuestion();
//			ClickLog.Log(ClickLogId.COPING_SELECTION);
//		}
//	}
//
//	/** OnClickListener for finding help */
//	private class HelpOnClickListener implements View.OnClickListener {
//		private int type;
//
//		/**
//		 * Constructor
//		 * 
//		 * @param type
//		 *            Type of the callee
//		 */
//		HelpOnClickListener(int type) {
//			this.type = type;
//		}
//
//		@Override
//		public void onClick(View v) {
//			// setCallQuestion(type);
//			ClickLog.Log(ClickLogId.COPING_SELECTION);
//		}
//	}


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
				RelativeLayout.LayoutParams barParam = (LayoutParams) bar.getLayoutParams();
				int total_len = barBg.getWidth() - barStart.getWidth() - barEnd.getWidth();
				barParam.width = total_len * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
				barLayout.updateViewLayout(bar, barParam);
			}
		}
	}

	private boolean enableBack = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ClickLog.Log(ClickLogId.COPING_RETURN);
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

			if (state == 0){
				return super.onKeyDown(keyCode, event);
			}
			else{
				resetCopingActivityViews();
				return false;
			}



		
			// return super.onKeyDown(keyCode, event);

// 			if (state == 0) {
// 				// CustomToastSmall.generateToast(R.string.emotionDIY_toast);
// 				--state;
// 			} else if (state == -1)
// 				return super.onKeyDown(keyCode, event);
// 			else {
// 				--state;
// //				if (state == 0)
// 					// setEmotionQuestion();
// //				else if (state == 1)
// //					setRecreationQuestion();
// 			}
// 			return false;
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
