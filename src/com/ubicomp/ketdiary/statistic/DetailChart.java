package com.ubicomp.ketdiary.statistic;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Control the view in the StatisticFragment which shows the user's detail
 * performance
 * 
 * @author Stanley Wang
 */
public class DetailChart {

	private FrameLayout layout;
	private TextView title;
	private RelativeLayout[] items = new RelativeLayout[2];
	private TextView[] subtitles = new TextView[2];
	private TextView[] comments = new TextView[2];
	private ImageView[] bars = new ImageView[2];
	private ImageView[] bar_starts = new ImageView[2];
	private ImageView[] bar_ends = new ImageView[2];
	private ImageView[] bar_progress = new ImageView[2];

	private String[] testComment = App.getContext().getResources()
			.getStringArray(R.array.radar_0_0);
	private String[] passComment = App.getContext().getResources()
			.getStringArray(R.array.radar_0_1);
	private String[] copingComment = App.getContext().getResources()
			.getStringArray(R.array.radar_1_0);
	private String[] questionComment = App.getContext().getResources()
			.getStringArray(R.array.radar_2_0);
	private String[] randomComment = App.getContext().getResources()
			.getStringArray(R.array.radar_2_1);	
	private String[] noteComment = App.getContext().getResources()
			.getStringArray(R.array.radar_3_0);
	

	public static final int TYPE_TEST = 0;
	public static final int TYPE_COPING = 1;
	public static final int TYPE_QUESTION = 2;
	public static final int TYPE_NOTE = 3;

	private int type = TYPE_TEST;
	private Rank rank = new Rank("", 0);

	private UIHandler handler = new UIHandler();

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Activity context
	 */
	public DetailChart(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (FrameLayout) inflater.inflate(R.layout.dialog_detail_rank,
				null);

		Typeface wordTypeface = Typefaces.getWordTypeface();
		Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();

		title = (TextView) layout.findViewById(R.id.detail_title);
		title.setTypeface(wordTypefaceBold);

		items[0] = (RelativeLayout) layout.findViewById(R.id.detail_item_0);
		items[1] = (RelativeLayout) layout.findViewById(R.id.detail_item_1);
		//items[2] = (RelativeLayout) layout.findViewById(R.id.detail_item_2);

		subtitles[0] = (TextView) layout.findViewById(R.id.detail_item_text_0);
		subtitles[1] = (TextView) layout.findViewById(R.id.detail_item_text_1);
		//subtitles[2] = (TextView) layout.findViewById(R.id.detail_item_text_2);

		comments[0] = (TextView) layout.findViewById(R.id.detail_comment_0);
		comments[1] = (TextView) layout.findViewById(R.id.detail_comment_1);
		//comments[2] = (TextView) layout.findViewById(R.id.detail_comment_2);

		for (int i = 0; i < 2; ++i) {
			subtitles[i].setTypeface(wordTypefaceBold);
			comments[i].setTypeface(wordTypeface);
		}

		bars[0] = (ImageView) layout.findViewById(R.id.detail_progress_bg_0);
		bars[1] = (ImageView) layout.findViewById(R.id.detail_progress_bg_1);
		//bars[2] = (ImageView) layout.findViewById(R.id.detail_progress_bg_2);

		bar_starts[0] = (ImageView) layout
				.findViewById(R.id.detail_progress_start_0);
		bar_starts[1] = (ImageView) layout
				.findViewById(R.id.detail_progress_start_1);
//		bar_starts[2] = (ImageView) layout
//				.findViewById(R.id.detail_progress_start_2);

		bar_ends[0] = (ImageView) layout
				.findViewById(R.id.detail_progress_end_0);
		bar_ends[1] = (ImageView) layout
				.findViewById(R.id.detail_progress_end_1);
//		bar_ends[2] = (ImageView) layout
//				.findViewById(R.id.detail_progress_end_2);

		bar_progress[0] = (ImageView) layout
				.findViewById(R.id.detail_progress_inner_0);
		bar_progress[1] = (ImageView) layout
				.findViewById(R.id.detail_progress_inner_1);
//		bar_progress[2] = (ImageView) layout
//				.findViewById(R.id.detail_progress_inner_2);

	}

	/**
	 * set which type of the detail chart
	 * 
	 * @param type
	 *            TYPE_TEST, TYPE_COPING, TYPE_QUESTION, and TYPE_NOTE
	 * @param rank
	 *            Rank information
	 */
	public void setting(int type, Rank rank) {
		this.type = type;
		this.rank = rank;
		handler.sendEmptyMessage(0);
	}

	/** Hide the detail chart */
	public void hide() {
		items[0].setVisibility(View.INVISIBLE);
		items[1].setVisibility(View.INVISIBLE);
		//items[2].setVisibility(View.INVISIBLE);
	}

	/**
	 * Get the view of the detail chart
	 * 
	 * @return view of the detail chart
	 */
	public View getView() {
		return layout;
	}

	/** Handler for setting the detail chart */
	private class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			int total_len = bars[0].getWidth() - bar_starts[0].getWidth()
					- bar_ends[0].getWidth();
			RelativeLayout.LayoutParams[] params = new RelativeLayout.LayoutParams[3];
			for (int i = 0; i < 2; ++i)
				params[i] = (LayoutParams) bar_progress[i].getLayoutParams();

			int len = 0;
			int idx = 0;
			
			switch (type) {
			case TYPE_TEST:
				title.setText(R.string.radar_label0_full);

				len = Math.min(rank.getTestTimes() * total_len / 100, total_len);
				idx = Math.min(rank.getTestTimes() * 3 / 100,
						testComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label0_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(testComment[idx]);

				len = Math.min(rank.getTestPass() * total_len / 100,
						total_len);
				idx = Math.min(rank.getTestPass() * 3 / 100,
						passComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label0_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(passComment[idx]);
				
				items[0].setVisibility(View.VISIBLE);	
				items[1].setVisibility(View.VISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
				break;

			case TYPE_COPING:
				title.setText(R.string.radar_label1_full);

				len = Math.min(rank.getCoping() * total_len / 300,
						total_len);
				idx = Math.min(rank.getCoping() * 3 / 300,
						copingComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label1_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(copingComment[idx]);
				
				/*
				len = Math.min(rank.getCoping() * total_len / 300,
						total_len);
				idx = Math.min(rank.getCoping() * 3 / 300,
						adviceEmotionDIYComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label1_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(adviceEmotionDIYComment[idx]);*/
				
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
				break;

			case TYPE_QUESTION:
				title.setText(R.string.radar_label2_full);

				len = Math.min(rank.getNormalQ() * total_len / 200,
						total_len);
				idx = Math.min(rank.getNormalQ() * 3 / 200,
						questionComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label2_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(questionComment[idx]);

				len = Math.min(rank.getRandomQ() * total_len / 300,
						total_len);
				idx = Math.min(rank.getRandomQ() * 3 / 300,
						randomComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label2_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(randomComment[idx]);

//				len = Math.min(rank.getManageAdditional() * total_len / 100,
//						total_len);
//				idx = Math.min(rank.getManageEmotion() * 2 / 100,
//						manageAdditionalComment.length - 1);
//				params[2].width = len;
//				subtitles[2].setText(R.string.radar_label2_2);
//				items[2].updateViewLayout(bar_progress[2], params[2]);
//				comments[2].setText(manageAdditionalComment[idx]);

				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				//items[2].setVisibility(View.VISIBLE);
				break;

			case TYPE_NOTE:
				title.setText(R.string.radar_label3_full);

				len = Math
						.min(rank.getNote() * total_len / 300, total_len);
				idx = Math.min(rank.getNote() * 3 / 300,
						noteComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label3_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(noteComment[idx]);

				/*
				len = Math
						.min(rank.getNote() * total_len / 300, total_len);
				idx = Math.min(rank.getNote() * 3 / 300,
						storyTestComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label3_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(storyTestComment[idx]);*/

//				len = Math.min(rank.getStoryFb() * total_len / 100 * 7,
//						total_len);
//				idx = Math.min(rank.getStoryFb() * 2 / 100 * 7,
//						storyFbComment.length - 1);
//				params[2].width = len;
//				subtitles[2].setText(R.string.radar_label3_2);
//				items[2].updateViewLayout(bar_progress[2], params[2]);
//				comments[2].setText(storyFbComment[idx]);

				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				//items[2].setVisibility(View.VISIBLE);
				break;
			default:
				items[0].setVisibility(View.INVISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
			}
		}

	}

}
