package com.ubicomp.ketdiary.data.structure;

public class CopingSkill {

	private TimeValue tv;
	private int skillType;
	private int skillSelect;
	private String recreation;
	private int score;

	public CopingSkill(long ts, int skillType, int skillSelect,
			String recreation, int score) {
		this.tv = TimeValue.generate(ts);
		this.skillType = skillType;
		this.skillSelect = skillSelect;
		this.recreation = recreation == null ? "" : recreation;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getSkillType() {
		return skillType;
	}

	public int getSkillSelect() {
		return skillSelect;
	}

	public String getRecreation() {
		return recreation;
	}

	public int getScore() {
		return score;
	}

}
