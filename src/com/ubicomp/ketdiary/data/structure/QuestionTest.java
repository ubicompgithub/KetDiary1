package com.ubicomp.ketdiary.data.structure;

public class QuestionTest {

	private TimeValue tv;
	private int questionType;
	private boolean isCorrect;
	private String selection;
	private int select;
	private int score;

	public QuestionTest(long ts, int questionType, boolean isCorrect,
			String selection, int select, int score) {
		this.tv = TimeValue.generate(ts);
		this.questionType = questionType;
		this.isCorrect = isCorrect;
		this.selection = selection == null ? "" : selection;
		this.select = select;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getQuestionType() {
		return questionType;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public String getSelection() {
		return selection;
	}

	public int getSelect() {
		return select;
	}

	public int getScore() {
		return score;
	}

}
