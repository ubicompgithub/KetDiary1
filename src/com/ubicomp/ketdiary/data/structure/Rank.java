package com.ubicomp.ketdiary.data.structure;

public class Rank {

	private String uid;
	private int score = 0;
	private int test = 0;
	private int advice = 0;
	private int manage = 0;
	private int story = 0;

	private int adviceQuestionnaire = 0;
	private int adviceEmotionDiy = 0;
	private int manageVoice = 0;
	private int manageEmotion = 0;
	private int manageAdditional = 0;
	private int storyRead = 0;
	private int storyTest = 0;
	private int storyFb = 0;

	public Rank(String uid, int score) {
		this.uid = uid;
		this.score = score;

	}

	public Rank(String uid, int score, int test, int advice, int manage,
			int story, int[] additionals) {
		this.uid = uid;
		this.score = score;
		this.test = test;
		this.advice = advice;
		this.manage = manage;
		this.story = story;
		this.adviceQuestionnaire = additionals[0];
		this.adviceEmotionDiy = additionals[1];
		this.manageVoice = additionals[2];
		this.manageEmotion = additionals[3];
		this.manageAdditional = additionals[4];
		this.storyRead = additionals[5];
		this.storyTest = additionals[6];
		this.storyFb = additionals[7];
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(uid + " " + score + "\n");
		sb.append("test<" + test + ">\n");
		sb.append("advice<" + advice + "," + adviceQuestionnaire + ","
				+ adviceEmotionDiy + ">\n");
		sb.append("manage<" + manage + "," + manageVoice + ',' + manageEmotion
				+ "," + manageAdditional + ">\n");
		sb.append("story<" + story + "," + storyRead + "," + storyTest + ","
				+ storyFb + ">\n");
		return sb.toString();
	}

	public String getUid() {
		return uid;
	}

	public int getScore() {
		return score;
	}

	public int getTest() {
		return test;
	}

	public int getAdvice() {
		return advice;
	}

	public int getManage() {
		return manage;
	}

	public int getStory() {
		return story;
	}

	public int getAdviceQuestionnaire() {
		return adviceQuestionnaire;
	}

	public int getAdviceEmotionDiy() {
		return adviceEmotionDiy;
	}

	public int getManageVoice() {
		return manageVoice;
	}

	public int getManageEmotion() {
		return manageEmotion;
	}

	public int getManageAdditional() {
		return manageAdditional;
	}

	public int getStoryRead() {
		return storyRead;
	}

	public int getStoryTest() {
		return storyTest;
	}

	public int getStoryFb() {
		return storyFb;
	}

}
