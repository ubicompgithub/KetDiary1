package com.ubicomp.ketdiary;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.clicklog.ClickLog;
import com.ubicomp.ketdiary.system.clicklog.ClickLogId;
import com.ubicomp.ketdiary.ui.ScaleOnTouchListener;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for user tutorial
 * 
 * @author Stanley Wang
 */
public class TutorialActivity extends Activity {

	private ImageView replay, arrow1, arrow2, arrow3;
	private ImageView tab;
	private ImageView tutorial1_bg2, tutorial2_bg2, tutorial3_bg2, tutorial4_bg2, tutorial5_bg2, tutorial6_bg2;
	private ImageView tutorial1_flash, tutorial2_flash, tutorial3_flash, tutorial4_flash, tutorial5_flash, tutorial6_flash;
	private TextView step;
	private TextView help;
	private TextView notify;

	private RelativeLayout layout;
	private Typeface digitTypeface;
	private Typeface wordTypefaceBold;

	private Animation anim1, anim2, anim3, anim4, anim5, anim6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_tutorial);

		digitTypeface = Typefaces.getDigitTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		tutorial1_bg2 = (ImageView) this.findViewById(R.id.tutorial1_bg2);
		tutorial2_bg2 = (ImageView) this.findViewById(R.id.tutorial2_bg2);
		tutorial3_bg2 = (ImageView) this.findViewById(R.id.tutorial3_bg2);
		tutorial4_bg2 = (ImageView) this.findViewById(R.id.tutorial4_bg2);
		tutorial5_bg2 = (ImageView) this.findViewById(R.id.tutorial5_bg2);
		tutorial6_bg2 = (ImageView) this.findViewById(R.id.tutorial6_bg2);
		tutorial1_flash = (ImageView) this.findViewById(R.id.tutorial1_flash);
		tutorial2_flash = (ImageView) this.findViewById(R.id.tutorial2_flash);
		tutorial3_flash = (ImageView) this.findViewById(R.id.tutorial3_flash);
		tutorial4_flash = (ImageView) this.findViewById(R.id.tutorial4_flash);
		tutorial5_flash = (ImageView) this.findViewById(R.id.tutorial5_flash);
		tutorial6_flash = (ImageView) this.findViewById(R.id.tutorial6_flash);
		//replay = (ImageView) this.findViewById(R.id.tutorial_replay);
		//replay.setOnTouchListener(new ScaleOnTouchListener());
		//arrow1 = (ImageView) this.findViewById(R.id.tutorial_arrow1);
		//arrow2 = (ImageView) this.findViewById(R.id.tutorial_arrow2);
		//arrow3 = (ImageView) this.findViewById(R.id.tutorial_arrow3);

		//step = (TextView) this.findViewById(R.id.tutorial_step);
		//step.setTypeface(digitTypeface);

		//notify = (TextView) this.findViewById(R.id.tutorial_notify);
		//notify.setTypeface(wordTypefaceBold);

		//help = (TextView) this.findViewById(R.id.tutorial_help);
		//help.setTypeface(wordTypefaceBold);

		//tab = (ImageView) this.findViewById(R.id.tutorial_tab);
		layout = (RelativeLayout) this.findViewById(R.id.tutorial_layout);

		anim1 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);
		anim2 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);
		anim3 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);
		anim4 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);
		anim5 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);
		anim6 = AnimationUtils.loadAnimation(this,
				R.anim.animation_tutorial_arrow);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.TUTORIAL_ENTER);
		settingState(0);
	}

	protected void onPause() {
		if (anim1 != null)
			anim1.cancel();
		if (anim2 != null)
			anim2.cancel();
		if (anim3 != null)
			anim3.cancel();
		if (anim4 != null)
			anim4.cancel();
		if (anim5 != null)
			anim5.cancel();
		if (anim6 != null)
			anim6.cancel();
		ClickLog.Log(ClickLogId.TUTORIAL_LEAVE);
		super.onPause();
	}

	private void settingState(int state) {
		//step.setText(String.valueOf(state + 1));
		if (anim1 != null)
			anim1.cancel();
		if (anim2 != null)
			anim2.cancel();
		if (anim3 != null)
			anim3.cancel();
		if (anim4 != null)
			anim4.cancel();
		if (anim5 != null)
			anim5.cancel();
		if (anim6 != null)
			anim6.cancel();
		switch (state) {
		case 0:
			
			layout.setBackgroundResource(R.drawable.tutorial1_bg);
			layout.setOnClickListener(new Listener(0));
			tutorial6_bg2.setVisibility(View.GONE);
			tutorial6_flash.setVisibility(View.GONE);
			tutorial1_bg2.setVisibility(View.VISIBLE);
			tutorial1_flash.setVisibility(View.VISIBLE);
			
			tutorial6_flash.setAnimation(null);
			tutorial1_flash.setAnimation(anim1);
//			help.setText(R.string.tutorial_step1);
//			layout.setOnClickListener(new Listener(0));
//			replay.setOnClickListener(null);
//			replay.setVisibility(View.INVISIBLE);
//			tab.setVisibility(View.INVISIBLE);
//			arrow1.setVisibility(View.VISIBLE);
//			arrow2.setVisibility(View.GONE);
//			arrow3.setVisibility(View.GONE);
//			arrow1.setAnimation(anim1);
//			arrow2.setAnimation(null);
//			arrow3.setAnimation(null);
//			anim1.start();
			break;
		case 1:
			
			layout.setBackgroundResource(R.drawable.tutorial2_bg);
			layout.setOnClickListener(new Listener(1));
			tutorial1_bg2.setVisibility(View.GONE);
			tutorial1_flash.setVisibility(View.GONE);
			tutorial2_bg2.setVisibility(View.VISIBLE);
			tutorial2_flash.setVisibility(View.VISIBLE);
			
			tutorial1_flash.setAnimation(null);
			tutorial2_flash.setAnimation(anim1);
//			help.setText(R.string.tutorial_step2);
//			layout.setOnClickListener(new Listener(1));
//			replay.setOnClickListener(null);
//			replay.setVisibility(View.INVISIBLE);
//			tab.setVisibility(View.VISIBLE);
//			arrow1.setVisibility(View.GONE);
//			arrow2.setVisibility(View.VISIBLE);
//			arrow3.setVisibility(View.GONE);
//			arrow1.setAnimation(null);
//			arrow2.setAnimation(anim2);
//			arrow3.setAnimation(null);
//			anim2.start();
			break;
		case 2:
			
			layout.setBackgroundResource(R.drawable.tutorial3_bg);
			layout.setOnClickListener(new Listener(2));
			tutorial2_bg2.setVisibility(View.GONE);
			tutorial2_flash.setVisibility(View.GONE);
			tutorial3_bg2.setVisibility(View.VISIBLE);
			tutorial3_flash.setVisibility(View.VISIBLE);
			
			tutorial2_flash.setAnimation(null);
			tutorial3_flash.setAnimation(anim1);
//			help.setText(R.string.tutorial_step3);
//			layout.setOnClickListener(new EndListener());
//			replay.setOnClickListener(new Listener(-1));
//			replay.setVisibility(View.VISIBLE);
//			tab.setVisibility(View.INVISIBLE);
//			arrow1.setVisibility(View.GONE);
//			arrow2.setVisibility(View.GONE);
//			arrow3.setVisibility(View.VISIBLE);
//			arrow1.setAnimation(null);
//			arrow2.setAnimation(null);
//			arrow3.setAnimation(anim3);
//			anim3.start();
			break;
		case 3:
			layout.setBackgroundResource(R.drawable.tutorial4_bg);
			layout.setOnClickListener(new Listener(3));
			tutorial3_bg2.setVisibility(View.GONE);
			tutorial3_flash.setVisibility(View.GONE);
			tutorial4_bg2.setVisibility(View.VISIBLE);
			tutorial4_flash.setVisibility(View.VISIBLE);
			
			tutorial3_flash.setAnimation(null);
			tutorial4_flash.setAnimation(anim1);
			break;
		case 4:
			layout.setBackgroundResource(R.drawable.tutorial5_bg);
			layout.setOnClickListener(new Listener(4));
			tutorial4_bg2.setVisibility(View.GONE);
			tutorial4_flash.setVisibility(View.GONE);
			tutorial5_bg2.setVisibility(View.VISIBLE);
			tutorial5_flash.setVisibility(View.VISIBLE);
			
			tutorial4_flash.setAnimation(null);
			tutorial5_flash.setAnimation(anim1);
			break;
		case 5:
			layout.setBackgroundResource(R.drawable.tutorial6_bg);
			layout.setOnClickListener(new EndListener());
			tutorial5_bg2.setVisibility(View.GONE);
			tutorial5_flash.setVisibility(View.GONE);
			tutorial6_bg2.setVisibility(View.VISIBLE);
			tutorial6_flash.setVisibility(View.VISIBLE);
			
			tutorial5_flash.setAnimation(null);
			tutorial6_flash.setAnimation(anim1);
			break;

		}
	}

	private class Listener implements View.OnClickListener {

		private int step;

		Listener(int step) {
			this.step = step;
		}

		@Override
		public void onClick(View v) {
			if (step == -1)
				ClickLog.Log(ClickLogId.TUTORIAL_REPLAY);
			else
				ClickLog.Log(ClickLogId.TUTORIAL_NEXT);
			settingState(step + 1);
		}

	}

	private class EndListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.TUTORIAL_NEXT);
			if (anim1 != null)
				anim1.cancel();
			if (anim2 != null)
				anim2.cancel();
			if (anim3 != null)
				anim3.cancel();
			finish();
		}
	}

}
