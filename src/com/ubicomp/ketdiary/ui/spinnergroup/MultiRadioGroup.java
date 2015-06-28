package com.ubicomp.ketdiary.ui.spinnergroup;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.ui.CustomToastSmall;
import com.ubicomp.ketdiary.ui.Typefaces;

public class MultiRadioGroup {

	private LayoutInflater inflater;
	private LinearLayout layout;
	private String[] choices;
	private boolean[] isSelect;
	private int numSelect = 0;
	private int max_selection = 1;
	private ImageView[] icons;
	private int toast_id;

	private Typeface wordTypeface = Typefaces.getWordTypeface();
	private long clicklogid;

	public MultiRadioGroup(Context context, String[] choices,
			boolean[] isSelect, int max_selection, int toast_id, long clicklogid) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.choices = choices;
		this.isSelect = isSelect;
		layout = (LinearLayout) inflater
				.inflate(R.layout.bar_radio_group, null);
		for (int i = 0; i < isSelect.length; ++i)
			if (isSelect[i])
				++numSelect;
		this.max_selection = max_selection;
		this.toast_id = toast_id;
		icons = new ImageView[choices.length];
		this.clicklogid = clicklogid;

		for (int i = 0; i < choices.length; ++i) {
			View v = createItem(i);
			layout.addView(v);
		}
	}

	public View getView() {
		return layout;
	}

	public boolean[] getResult() {
		return isSelect;
	}

	public View createItem(int id) {

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_radio_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypeface);
		text.setText(choices[id]);

		icons[id] = (ImageView) layout.findViewById(R.id.question_select);
		if (isSelect[id])
			icons[id].setVisibility(View.VISIBLE);
		else
			icons[id].setVisibility(View.INVISIBLE);

		layout.setOnClickListener(new ItemOnClickListener(id));

		return layout;
	}

	private class ItemOnClickListener implements OnClickListener {

		private int id;

		public ItemOnClickListener(int id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			//ClickLog.Log(clicklogid);
			if (isSelect[id]) {
				--numSelect;
				isSelect[id] = false;
				icons[id].setVisibility(View.INVISIBLE);
			} else if (numSelect < max_selection) {
				++numSelect;
				isSelect[id] = true;
				icons[id].setVisibility(View.VISIBLE);
			} else {
				CustomToastSmall.generateToast(toast_id);
			}
		}

	}
}
