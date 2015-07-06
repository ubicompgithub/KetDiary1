package com.ubicomp.ketdiary.statistic.coping;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.clicklog.ClickLog;
import com.ubicomp.ketdiary.clicklog.ClickLogId;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Dialog of the '!'Questionnaire
 * 
 * @author Stanley Wang
 */
public class QuestionnaireDialog {

	private ArrayList<Integer> clickSequence;
	private ArrayList<QuestionnaireContent> contentSequence;

	private QuestionnaireDialogCaller quesDialogCaller;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;

	private RelativeLayout mainLayout;
	private LinearLayout questionLayout;
	private ImageView closeButton;
	private TextView help, next;
	private Drawable choiceDrawable, choiceSelectedDrawable;
	private Resources r;
	private DatabaseControl db;
	private Typeface wordTypefaceBold;
	private int type;

	private MediaPlayer mediaPlayer;

	private LinearLayout.LayoutParams questionParam;

	public QuestionnaireDialog(QuestionnaireDialogCaller quesDialogCaller, RelativeLayout mainLayout) {
		this.context = quesDialogCaller.getContext();
		this.quesDialogCaller = quesDialogCaller;
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		db = new DatabaseControl();
		clickSequence = new ArrayList<Integer>();
		contentSequence = new ArrayList<QuestionnaireContent>();
		type = -1;

		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		boxLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_statistic_questionnaire, null);
		boxLayout.setVisibility(View.INVISIBLE);

		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionParam = (LinearLayout.LayoutParams) questionLayout.getLayoutParams();

		help = (TextView) boxLayout.findViewById(R.id.question_text);
		next = (TextView) boxLayout.findViewById(R.id.question_next);

		closeButton = (ImageView) boxLayout.findViewById(R.id.question_exit);
	}

	@SuppressLint("InlinedApi")
	/**initialize the QuestionnaireDialog*/
	public void initialize() {

		mainLayout.addView(boxLayout);
		RelativeLayout.LayoutParams mainParam = (LayoutParams) boxLayout.getLayoutParams();
		mainParam.width = mainParam.height = LayoutParams.MATCH_PARENT;
		help.setTypeface(wordTypefaceBold);
		next.setTypeface(wordTypefaceBold);
		choiceDrawable = r.getDrawable(R.drawable.radio_button_normal);
		choiceSelectedDrawable = r.getDrawable(R.drawable.radio_button_checked);
		closeButton.setOnClickListener(new ExitListener());
	}

	/** Remove the dialog and release the resources */
	public void clear() {
		closeMediaPlayer();
		if (boxLayout != null)
			mainLayout.removeView(boxLayout);
	}

	/** Generate the dialog if the user passes the BrAC test and feels well */
	public void generateType0Dialog() {
		type = 0;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size() - 1).onPush();
	}

	/** Generate the dialog if the user passes the BrAC test but feels bad */
	public void generateType1Dialog() {
		type = 1;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type1Content(this));
		contentSequence.get(contentSequence.size() - 1).onPush();
	}

	/** Generate the dialog if the user lapses */
	public void generateType2Dialog() {
		type = 2;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type2Content(this));
		contentSequence.get(contentSequence.size() - 1).onPush();
	}

	/** Generate the dialog if the user relapses */
	public void generateType3Dialog() {
		type = 3;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type3Content(this));
		contentSequence.get(contentSequence.size() - 1).onPush();
	}

	/** Generate the dialog when the user does not do BrAC tests */
	public void generateNormalBox() {
		type = -1;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size() - 1).onPush();
	}

	/** show the dialog */
	public void showDialog() {
		quesDialogCaller.enablePage(false);
		boxLayout.setVisibility(View.VISIBLE);
		return;
	}

	/** show the dialog */
	public void closeDialog() {
		closeDialog(R.string.after_questionnaire);
	}

	/** stop the media player and release the resources */
	private void closeMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * create a media player
	 * 
	 * @param id
	 *            music id
	 * @return generated media player
	 */
	public MediaPlayer createMediaPlayer(int id) {
		closeMediaPlayer();
		mediaPlayer = MediaPlayer.create(getContext(), id);
		return mediaPlayer;
	}

	/**
	 * close the Dialog and show a toast
	 * 
	 * @param toastId
	 *            string id of the toast
	 */
	public void closeDialog(int toastId) {
		closeMediaPlayer();

		PreferenceControl.setTestResult(-1);

		int addScore = insertSequence();
		CustomToast.generateToast(toastId, addScore);
		quesDialogCaller.updateSelfHelpCounter();

		quesDialogCaller.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		quesDialogCaller.setQuestionAnimation();
	}

	/** close the dialog and call out */
	public void closeDialogAndCall() {

		closeMediaPlayer();
		PreferenceControl.setTestResult(-1);

		insertSequence();

		quesDialogCaller.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		quesDialogCaller.setQuestionAnimation();
		return;
	}

	/** close the dialog but do not insert any things into the database */
	public void closeBoxNull() {

		closeMediaPlayer();
		quesDialogCaller.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		return;
	}

	/**
	 * get the context
	 * 
	 * @return context Context of the activity of the dialog
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * get the Drawable of the choice image
	 * 
	 * @return Drawable of the choice image
	 */
	public Drawable getChoiceDrawable() {
		return choiceDrawable;
	}

	/**
	 * get the Drawable of the selected choice image
	 * 
	 * @return Drawable of the selected choice image
	 */
	public Drawable getChoiceSelectedDrawable() {
		return choiceSelectedDrawable;
	}

	/**
	 * get the LinearLayout for the questions of the questionnaire
	 * 
	 * @return linearLayout contains the questions
	 */
	public LinearLayout getQuestionnaireLayout() {
		return questionLayout;
	}

	public ArrayList<Integer> getClickSequence() {
		return clickSequence;
	}

	public ArrayList<QuestionnaireContent> getQuestionSequence() {
		return contentSequence;
	}

	private int insertSequence() {
		int addScore = db.insertQuestionnaire(new Questionnaire(System.currentTimeMillis(), type, seqToString(), 0));
		return addScore;
	}

	private String seqToString() {
		int size = clickSequence.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			sb.append(clickSequence.get(i));
			if (i < size - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	public void setHelpMessage(String str) {
		help.setText(str);
	}

	public void setHelpMessage(int str_id) {
		help.setText(str_id);
	}

	public void setNextButton(String str, View.OnClickListener listener) {
		next.setText(str);
		next.setOnClickListener(listener);
	}

	public void setNextButton(int str_id, View.OnClickListener listener) {
		next.setText(str_id);
		next.setOnClickListener(listener);
	}

	public Typeface getTypeface() {
		return wordTypefaceBold;
	}

	public void cleanSelection() {
		int idx = contentSequence.size() - 1;
		if (idx >= 0)
			contentSequence.get(idx).cleanSelection();
	}

	public void showQuestionnaireLayout(boolean visible) {
		if (visible)
			questionParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		else
			questionParam.height = 0;
	}

	private class ExitListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			clickSequence.clear();
			contentSequence.clear();
			quesDialogCaller.enablePage(true);
			boxLayout.setVisibility(View.INVISIBLE);
			ClickLog.Log(ClickLogId.STATISTIC_QUESTION_EXIT);
		}

	}

	public int getType() {
		return type;
	}

	public void showCloseButton(boolean show) {
		if (show)
			closeButton.setVisibility(View.VISIBLE);
		else
			closeButton.setVisibility(View.INVISIBLE);

	}
}
