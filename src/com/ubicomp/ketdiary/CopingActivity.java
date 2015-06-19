package com.ubicomp.ketdiary;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for All Coping Skill
 * 
 * @author Andy Chen
 */
public class CopingActivity extends EmotionActivity {

	private LayoutInflater inflater;

	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;

	private View fbView;
	private View uvView;
	private RelativeLayout[] recreationViews;
	private RelativeLayout[] contactViews;
	//private MultiRadioGroup socialGroup;
	private View socialGroupView;
	//private SingleRadioGroup notificationGroup;
	private View notificationGroupView;
	private View breathView, musleView, stretchView, peacefulView, musicView, meditationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		this.activity = this;
		titleLayout = (LinearLayout) this
				.findViewById(R.id.setting_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.setting_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		View title = BarButtonGenerator.createTitleView(R.string.coping_page);
		titleLayout.addView(title);

		setting();

	}

	private void setting() {

		RelativeLayout relaxedView = createListView(R.string.coping_relaxed,
				new OnClickListener() {
					private boolean visible = false;

					@Override
					public void onClick(View v) {
						
						ImageView list = (ImageView) v.findViewById(R.id.question_list);
						if (visible) {
							breathView.setVisibility(View.GONE);
							musleView.setVisibility(View.GONE);
							stretchView.setVisibility(View.GONE);
							musicView.setVisibility(View.GONE);
							meditationView.setVisibility(View.GONE);
							
							list.setVisibility(View.INVISIBLE);
						} else {
							breathView.setVisibility(View.VISIBLE);
							musleView.setVisibility(View.VISIBLE);
							stretchView.setVisibility(View.VISIBLE);
							musicView.setVisibility(View.VISIBLE);
							meditationView.setVisibility(View.VISIBLE);
							
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
		mainLayout.addView(relaxedView);
		
		breathView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_breath, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		breathView.setVisibility(View.GONE);
		mainLayout.addView(breathView);
		
		musleView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_musle, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		musleView.setVisibility(View.GONE);
		mainLayout.addView(musleView);
		
		stretchView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_stretch, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		stretchView.setVisibility(View.GONE);
		mainLayout.addView(stretchView);
		
		musicView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_music, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		
		musicView.setVisibility(View.GONE);
		mainLayout.addView(musicView);
		
		meditationView= BarButtonGenerator.createSettingButtonView(
				R.string.coping_meditation, new OnClickListener() {

					@Override
					public void onClick(View v) {
						
					}

				});
		
		meditationView.setVisibility(View.GONE);
		mainLayout.addView(meditationView);
		
		

		//-------------
		RelativeLayout recreationView = createListView(
				R.string.setting_recreation, new OnClickListener() {

					private boolean visible = false;

					@Override
					public void onClick(View v) {
				
						ImageView list = (ImageView) v
								.findViewById(R.id.question_list);
						if (visible) {
							for (int i = 0; i < recreationViews.length; ++i)
								recreationViews[i].setVisibility(View.GONE);
							list.setVisibility(View.INVISIBLE);
						} else {
							for (int i = 0; i < recreationViews.length; ++i)
								recreationViews[i].setVisibility(View.VISIBLE);
							list.setVisibility(View.VISIBLE);
						}
						visible = !visible;
					}
				});
		mainLayout.addView(recreationView);
		

		String[] recreations = PreferenceControl.getRecreations();
		recreationViews = new RelativeLayout[recreations.length];
		for (int i = 0; i < recreations.length; ++i) {
			recreationViews[i] = createEditRecreationView(recreations[i], i);
			recreationViews[i].setVisibility(View.GONE);
			mainLayout.addView(recreationViews[i]);
		}


	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {

	
		//PreferenceControl.setNotificationTimeIdx(notificationGroup.getResult());


		BootBoardcastReceiver.setRegularNotification(getBaseContext(),
				getIntent());

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

	private RelativeLayout createEditRecreationView(String defaultText, int id) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_edit_recreation_item, null);

		TextView text = (TextView) layout.findViewById(R.id.question_text);
		text.setTypeface(wordTypeface);
		text.setText(defaultText);

		EditText edit = (EditText) layout.findViewById(R.id.question_edit);
		edit.setTypeface(wordTypefaceBold);
		edit.setText(defaultText);
		edit.setVisibility(View.INVISIBLE);

		TextView button = (TextView) layout.findViewById(R.id.question_button);
		button.setTypeface(wordTypefaceBold);
		button.setOnClickListener(new RecreationOnClickListener(text, edit,
				button, id));

		return layout;
	}

	private class RecreationOnClickListener implements View.OnClickListener {
		private boolean editable = false;

		private TextView text;
		private EditText editText;
		private TextView button;
		private int id;

		private String ok = App.getContext().getString(R.string.ok);
		private String edit = App.getContext().getString(R.string.edit);

		private int ok_color = App.getContext().getResources()
				.getColor(R.color.lite_orange);
		private int edit_color = App.getContext().getResources()
				.getColor(R.color.text_gray);

		public RecreationOnClickListener(TextView text, EditText editText,
				TextView button, int id) {
			this.text = text;
			this.editText = editText;
			this.button = button;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			
			if (editable) {
				String recreation = editText.getText().toString();
				text.setText(recreation);
				text.setVisibility(View.VISIBLE);
				editText.setVisibility(View.INVISIBLE);
				button.setText(edit);
				button.setTextColor(edit_color);
				PreferenceControl.setRecreation(recreation, id);
			} else {
				text.setVisibility(View.INVISIBLE);
				editText.setText(text.getText());
				editText.setVisibility(View.VISIBLE);
				button.setText(ok);
				button.setTextColor(ok_color);
			}
			editable = !editable;
		}

	}

	private RelativeLayout createEditPhoneView(String defaultName,
			String defaultPhone, int id) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_edit_contact_item, null);

		TextView nt = (TextView) layout.findViewById(R.id.question_name_text);
		nt.setTypeface(wordTypeface);
		nt.setText(defaultName);
		TextView pt = (TextView) layout.findViewById(R.id.question_phone_text);
		pt.setTypeface(wordTypeface);
		pt.setText(defaultPhone);

		EditText name = (EditText) layout.findViewById(R.id.question_name);
		name.setTypeface(wordTypefaceBold);
		name.setVisibility(View.INVISIBLE);
		EditText phone = (EditText) layout.findViewById(R.id.question_phone);
		phone.setTypeface(wordTypefaceBold);
		phone.setVisibility(View.INVISIBLE);

		TextView button = (TextView) layout.findViewById(R.id.question_button);
		button.setTypeface(wordTypefaceBold);
		button.setOnClickListener(new PhoneOnClickListener(nt, pt, name, phone,
				button, id));

		return layout;
	}

	private class PhoneOnClickListener implements View.OnClickListener {
		private boolean editable = false;

		private TextView nt, pt;
		private EditText name, phone;
		private TextView button;
		private int id;

		private String ok = App.getContext().getString(R.string.ok);
		private String edit = App.getContext().getString(R.string.edit);

		private int ok_color = App.getContext().getResources()
				.getColor(R.color.lite_orange);
		private int edit_color = App.getContext().getResources()
				.getColor(R.color.text_gray);

		public PhoneOnClickListener(TextView nt, TextView pt, EditText name,
				EditText phone, TextView button, int id) {
			this.nt = nt;
			this.pt = pt;
			this.name = name;
			this.phone = phone;
			this.button = button;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
		
			if (editable) {
				String name_text = name.getText().toString();
				nt.setText(name_text);
				nt.setVisibility(View.VISIBLE);
				name.setVisibility(View.INVISIBLE);
				String phone_text = phone.getText().toString();
				pt.setText(phone_text);
				pt.setVisibility(View.VISIBLE);
				phone.setVisibility(View.INVISIBLE);
				button.setText(edit);
				button.setTextColor(edit_color);
				PreferenceControl.setFamilyCallData(name_text, phone_text, id);
			} else {
				nt.setVisibility(View.INVISIBLE);
				pt.setVisibility(View.INVISIBLE);
				name.setText(nt.getText());
				name.setVisibility(View.VISIBLE);
				phone.setText(pt.getText());
				phone.setVisibility(View.VISIBLE);
				button.setText(ok);
				button.setTextColor(ok_color);
			}
			editable = !editable;
		}

	}

	private View createCheckBoxView(int str_id,
			OnCheckedChangeListener listener, boolean defaultCheck) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_checkbox_item, null);

		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setText(str_id);
		text.setTypeface(wordTypeface);

		RadioGroup radio = (RadioGroup) layout
				.findViewById(R.id.question_check);
		RadioButton yes = (RadioButton) layout
				.findViewById(R.id.question_check_yes);
		RadioButton no = (RadioButton) layout
				.findViewById(R.id.question_check_no);
		yes.setTypeface(wordTypefaceBold);
		no.setTypeface(wordTypefaceBold);

		if (defaultCheck)
			radio.check(R.id.question_check_yes);
		else
			radio.check(R.id.question_check_no);

		radio.setOnCheckedChangeListener(listener);

		return layout;
	}

}
