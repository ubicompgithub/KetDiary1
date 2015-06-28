package com.ubicomp.ketdiary.data.structure;

public class QuestionTest {

	private TimeValue tv;
	private int questionType;
	private int isCorrect;
	private String selection;
	private int choose;
	private int score;

	public QuestionTest(long ts, int questionType, int isCorrect,
			String selection, int choose, int score) {
		this.tv = TimeValue.generate(ts);
		this.questionType = questionType;
		this.isCorrect = isCorrect;
		this.selection = selection == null ? "" : selection;
		this.choose = choose;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getQuestionType() {
		return questionType;
	}

	public int getisCorrect() {
		return isCorrect;
	}

	public String getSelection() {
		return selection;
	}

	public int getChoose() {
		return choose;
	}

	public int getScore() {
		return score;
	}

}
