package com.ubicomp.ketdiary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ubicomp.ketdiary.AboutActivity;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.SettingActivity;

@SuppressLint({ "ViewConstructor", "InlinedApi" })
public class CustomMenu extends PopupWindow {

	private View about_button, setting_button;
	private TextView about_text, setting_text;
	private Context context;

	private Typeface wordTypefaceBold;

	public CustomMenu(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menu = inflater.inflate(R.layout.menu, null);
		this.context = context;
		this.setContentView(menu);
		this.setFocusable(false);
		this.setOutsideTouchable(true);
		this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);

		this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(new ColorDrawable(
				android.graphics.Color.TRANSPARENT));
		about_button = menu.findViewById(R.id.menu_about);
		about_button.setOnClickListener(new MenuOnClickListener());

		setting_button = menu.findViewById(R.id.menu_setting);
		setting_button.setOnClickListener(new MenuOnClickListener());

		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		about_text = (TextView) menu.findViewById(R.id.menu_about_text);
		about_text.setTypeface(wordTypefaceBold);
		setting_text = (TextView) menu.findViewById(R.id.menu_setting_text);
		setting_text.setTypeface(wordTypefaceBold);
	}

	private class MenuOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.menu_about:
				intent = new Intent(context, AboutActivity.class);
				break;
			case R.id.menu_setting:
				intent = new Intent(context, SettingActivity.class);
				break;
			default:
				intent = null;
			}
			if (intent != null)
				context.startActivity(intent);
		}
	}

}
