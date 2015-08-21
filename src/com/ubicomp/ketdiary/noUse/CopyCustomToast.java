package com.ubicomp.ketdiary.noUse;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.Typefaces;

public class CopyCustomToast {

	private static Toast toast = null;

	private static View layout = null;
//	private static TextView toastText, toastCounter;
//	private static ImageView toastImage;
	
	private static ImageView toastCircle, toastContent;
	private static ImageView toastCongra;
	private static TextView toastDescribe;
	private static int[] expId={android.R.color.transparent, R.drawable.toast_circle_level1,R.drawable.toast_circle_level2,R.drawable.toast_circle_level3
		,R.drawable.toast_circle_level4,R.drawable.toast_circle_level5,R.drawable.toast_circle_level6,
		R.drawable.toast_circle_level6,R.drawable.toast_circle_level7,R.drawable.toast_circle_level8,
		R.drawable.toast_circle_level9,R.drawable.toast_circle_level10}; 
	private static Drawable c1Drawable, c2Drawable, c3Drawable, okDrawable, 
			failDrawable;

	private static SoundPool soundpool;
	private static int soundId;

	public static void settingSoundPool() {
		soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		soundId = soundpool.load(App.getContext(), R.raw.ding, 1);
	}

	public static void generateToast(int str_id, int counter) {
		Context context = App.getContext();
		if (toast != null) {
			toast.cancel();
			toast = null;
		}
		toast = new Toast(context);

		if (layout == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.toast, null);
//			toastText = (TextView) layout.findViewById(R.id.custom_toast_text);
//			toastText.setTypeface(Typefaces.getWordTypefaceBold());
//			toastCounter = (TextView) layout
//					.findViewById(R.id.custom_toast_counter);
//			toastCounter.setTypeface(Typefaces.getWordTypefaceBold());
//			toastImage = (ImageView) layout
//					.findViewById(R.id.custom_toast_main_picture);
			
			toastDescribe= (TextView) layout.findViewById(R.id.toast_description);
			toastDescribe.setTypeface(Typefaces.getWordTypefaceBold());
			toastContent=(ImageView) layout
					.findViewById(R.id.toast_circlecontent);
			toastCircle=(ImageView) layout
					.findViewById(R.id.toast_circle);
			toastCongra=(ImageView) layout
					.findViewById(R.id.toast_congratulation);
			c1Drawable = context.getResources().getDrawable(
					R.drawable.toast_add_1);
			c2Drawable = context.getResources().getDrawable(
					R.drawable.toast_add_2);
			c3Drawable = context.getResources().getDrawable(
					R.drawable.toast_add_3);
			okDrawable = context.getResources().getDrawable(
					R.drawable.toast_goodjob);
			failDrawable = context.getResources().getDrawable(
					R.drawable.toast_fail);
		}
		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			soundId = soundpool.load(context, R.raw.ding, 1);
		}
		toast.setView(layout);
		toast.setMargin(0, 0);
		toast.setGravity(Gravity.FILL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toastDescribe.setText(str_id);
		
		int total_level = PreferenceControl.getPoint(); ///////////////////////////////////不確定抓經驗值的是什麼
		int counterLevel = total_level % 10;
		toastCircle.setImageResource(expId[counterLevel]);
		//levelCircle.setImageResource(levelId[counter]);
		
		
		switch (counter) {
		case 0:
			toastContent.setImageDrawable(okDrawable);
			toastCircle.setVisibility(View.VISIBLE);
			toastCongra.setVisibility(View.VISIBLE);
			//toastCounter.setVisibility(View.INVISIBLE);
			break;
		case 1:
			toastContent.setImageDrawable(c1Drawable);
			//toastCounter.setVisibility(View.VISIBLE);
			toastCircle.setVisibility(View.VISIBLE);
			toastCongra.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case 2:
			toastContent.setImageDrawable(c2Drawable);
			toastCircle.setVisibility(View.VISIBLE);
			toastCongra.setVisibility(View.VISIBLE);
			//toastCounter.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case 3:
			toastContent.setImageDrawable(c3Drawable);
			toastCircle.setVisibility(View.VISIBLE);
			toastCongra.setVisibility(View.VISIBLE);
			//toastCounter.setVisibility(View.VISIBLE);
			soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
			break;
		case -1:
			toastContent.setImageDrawable(failDrawable);
			toastCircle.setVisibility(View.INVISIBLE);
			toastCongra.setVisibility(View.INVISIBLE);
			//toastCounter.setVisibility(View.INVISIBLE);
			break;
		}

		toast.show();
	}

}
