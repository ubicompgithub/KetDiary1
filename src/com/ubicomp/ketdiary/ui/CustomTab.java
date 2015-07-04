package com.ubicomp.ketdiary.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;

public class CustomTab {

	private Context context;
	private View view;
	private int iconId;
	private int iconOnId;
	private LayoutInflater inflater;

	private ImageView bg, icon, highlight;
	private Drawable onDrawable, offDrawable;
	private Drawable iconDrawable, iconOnDrawable;

	public CustomTab(int id, int onId) {
		this.context = App.getContext();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.iconId = id;
		this.iconOnId = onId;
		setting();
	}

	private void setting() {
		view = inflater.inflate(R.layout.tab_item, null);
		bg = (ImageView) view.findViewById(R.id.tab_icon_bg);
		icon = (ImageView) view.findViewById(R.id.tab_icon_icon);
		iconDrawable = context.getResources().getDrawable(iconId);
		iconOnDrawable = context.getResources().getDrawable(iconOnId);
		icon.setImageDrawable(iconDrawable);
		highlight = (ImageView) view.findViewById(R.id.tab_highlight);
		onDrawable = context.getResources()
				.getDrawable(R.drawable.bottom_bar_pressed2);
		offDrawable = context.getResources().getDrawable(
				R.drawable.tab_bg_selector);
	}

	public View getTab() {
		return view;
	}

	public void changeState(boolean selected) {
		if (selected) {
			bg.setImageDrawable(onDrawable);
			icon.setImageDrawable(iconOnDrawable);
		} else {
			bg.setImageDrawable(offDrawable);
			icon.setImageDrawable(iconDrawable);
		}
	}

	public void clear() {

		bg.setImageDrawable(null);
		icon.setImageDrawable(null);
	}

	public void showHighlight(boolean visible) {
		if (visible)
			highlight.setVisibility(View.VISIBLE);
		else
			highlight.setVisibility(View.INVISIBLE);
	}
}
