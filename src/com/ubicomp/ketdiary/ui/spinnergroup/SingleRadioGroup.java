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
import com.ubicomp.ketdiary.ui.Typefaces;

public class SingleRadioGroup {

	private LayoutInflater inflater;
	private LinearLayout layout;
	private String[] choices;
	private int select_id = 0;
	private ImageView[] icons;

	private Typeface wordTypeface = Typefaces.getWordTypeface();
	private long clicklogid;

	public SingleRadioGroup(Context context, String[] choices, int select_id,
			long clicklogid) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.choices = choices;
		layout = (LinearLayout) inflater
				.inflate(R.layout.bar_radio_group, null);

		this.select_id = select_id;
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

	public int getResult() {
		return select_id;
	}

	public View createItem(int id) {

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.bar_radio_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypeface);
		text.setText(choices[id]);

		icons[id] = (ImageView) layout.findViewById(R.id.question_select);
		if (id == select_id)
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
			select_id = id;
			for (int i = 0; i < icons.length; ++i)
				icons[i].setVisibility(View.INVISIBLE);
			icons[select_id].setVisibility(View.VISIBLE);
		}

	}
}
