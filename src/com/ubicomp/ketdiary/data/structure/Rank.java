package com.ubicomp.ketdiary.data.structure;

public class Rank {

	private String uid;
	private int score = 0;
	private int test = 0;
	private int note = 0;
	private int question = 0;
	private int coping = 0;
	
	private int testTimes = 0;
	private int testPass = 0;
	private int normalQ = 0;
	private int randomQ = 0;


	public Rank(String uid, int score) {
		this.uid = uid;
		this.score = score;

	}

	public Rank(String uid, int score, int test, int note, int question,
			int coping, int[] additionals) {
		this.uid = uid;
		this.score = score;
		this.test = test;
		this.note = note;
		this.question = question;
		this.coping = coping;
		
		this.testTimes = additionals[0];
		this.testPass = additionals[1];
		this.normalQ = additionals[2];
		this.randomQ = additionals[3];
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(uid + " " + score + "\n");
		sb.append("test<" + test + ">\n");
		sb.append("note<" + note + "," + ">\n");
		sb.append("question<" + question + ">\n");
		sb.append("coping<" + coping + ">\n");
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

	public int getNote() {
		return note;
	}

	public int getQuestion() {
		return question;
	}

	public int getCoping() {
		return coping;
	}
	
	public int getTestTimes() {
		return testTimes;
	}
	
	public int getTestPass() {
		return testPass;
	}
	
	public int getNormalQ() {
		return normalQ;
	}
	
	public int getRandomQ() {
		return randomQ;
	}

}
