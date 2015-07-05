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

	private String[] detectionComment = App.getContext().getResources()
			.getStringArray(R.array.radar_0);
	private String[] adviceQuestionComment = App.getContext().getResources()
			.getStringArray(R.array.radar_1_0);
	private String[] adviceEmotionDIYComment = App.getContext().getResources()
			.getStringArray(R.array.radar_1_1);
	private String[] manageVoiceComment = App.getContext().getResources()
			.getStringArray(R.array.radar_2_0);
	private String[] manageEmotionComment = App.getContext().getResources()
			.getStringArray(R.array.radar_2_1);
	private String[] manageAdditionalComment = App.getContext().getResources()
			.getStringArray(R.array.radar_2_2);
	private String[] storyReadComment = App.getContext().getResources()
			.getStringArray(R.array.radar_3_0);
	private String[] storyTestComment = App.getContext().getResources()
			.getStringArray(R.array.radar_3_1);
	private String[] storyFbComment = App.getContext().getResources()
			.getStringArray(R.array.radar_3_2);

	public static final int TYPE_DETECTION = 0;
	public static final int TYPE_ADVICE = 1;
	public static final int TYPE_MANAGE = 2;
	public static final int TYPE_STORY = 3;

	private int type = TYPE_DETECTION;
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
	 *            TYPE_DETECTION, TYPE_ADVICE, TYPE_MANAGE, and TYPE_STORY
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
			/*
			switch (type) {
			case TYPE_DETECTION:
				title.setText(R.string.radar_label0_full);

				len = Math.min(rank.getTest() * total_len / 600, total_len);
				idx = Math.min(rank.getTest() * 3 / 600,
						detectionComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label0_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(detectionComment[idx]);

				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
				break;

			case TYPE_ADVICE:
				title.setText(R.string.radar_label1_full);

				len = Math.min(rank.getAdviceQuestionnaire() * total_len / 300,
						total_len);
				idx = Math.min(rank.getAdviceQuestionnaire() * 3 / 300,
						adviceQuestionComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label1_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(adviceQuestionComment[idx]);

				len = Math.min(rank.getAdviceEmotionDiy() * total_len / 300,
						total_len);
				idx = Math.min(rank.getAdviceEmotionDiy() * 3 / 300,
						adviceEmotionDIYComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label1_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(adviceEmotionDIYComment[idx]);
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
				break;

			case TYPE_MANAGE:
				title.setText(R.string.radar_label2_full);

				len = Math.min(rank.getManageVoice() * total_len / 300,
						total_len);
				idx = Math.min(rank.getManageVoice() * 3 / 300,
						manageVoiceComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label2_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(manageVoiceComment[idx]);

				len = Math.min(rank.getManageEmotion() * total_len / 300,
						total_len);
				idx = Math.min(rank.getManageEmotion() * 3 / 300,
						manageEmotionComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label2_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(manageEmotionComment[idx]);

				len = Math.min(rank.getManageAdditional() * total_len / 100,
						total_len);
				idx = Math.min(rank.getManageEmotion() * 2 / 100,
						manageAdditionalComment.length - 1);
//				params[2].width = len;
//				subtitles[2].setText(R.string.radar_label2_2);
//				items[2].updateViewLayout(bar_progress[2], params[2]);
//				comments[2].setText(manageAdditionalComment[idx]);

				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				//items[2].setVisibility(View.VISIBLE);
				break;

			case TYPE_STORY:
				title.setText(R.string.radar_label3_full);

				len = Math
						.min(rank.getStoryRead() * total_len / 300, total_len);
				idx = Math.min(rank.getStoryRead() * 3 / 300,
						storyReadComment.length - 1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label3_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(storyReadComment[idx]);

				len = Math
						.min(rank.getStoryTest() * total_len / 300, total_len);
				idx = Math.min(rank.getStoryTest() * 3 / 300,
						storyTestComment.length - 1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label3_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(storyTestComment[idx]);

				len = Math.min(rank.getStoryFb() * total_len / 100 * 7,
						total_len);
				idx = Math.min(rank.getStoryFb() * 2 / 100 * 7,
						storyFbComment.length - 1);
//				params[2].width = len;
//				subtitles[2].setText(R.string.radar_label3_2);
//				items[2].updateViewLayout(bar_progress[2], params[2]);
//				comments[2].setText(storyFbComment[idx]);

				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				//items[2].setVisibility(View.VISIBLE);
				break;
			default:
				items[0].setVisibility(View.INVISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				//items[2].setVisibility(View.INVISIBLE);
			}*/
		}

	}

}
