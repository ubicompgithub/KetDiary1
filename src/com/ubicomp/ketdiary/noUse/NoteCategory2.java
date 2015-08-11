package com.ubicomp.ketdiary.noUse;

import java.util.HashMap;
import java.util.Vector;

public class NoteCategory2 {
	
	public static HashMap < Integer, String > dictionary;
	
	
	public NoteCategory2(){
		noteSetting();
	}
	
	private void noteSetting(){
		
		dictionary = new HashMap< Integer, String >();
		
		dictionary.put(0, "沮喪");
		dictionary.put(100, "沮喪");
		dictionary.put(101, "走投無路");
		dictionary.put(102, "對自己失望");
		dictionary.put(103, "無聊");
		dictionary.put(104, "寂寞");
		dictionary.put(105, "緊張,焦慮");
		dictionary.put(106, "感到罪惡");
		dictionary.put(107, "壓力大,想逃避");
		dictionary.put(108, "對某事感到生氣");
		dictionary.put(109, "不知道自己該怎麼辦");
		dictionary.put(200, "生病,噁心,疲倦");
		dictionary.put(201, "失眠");
		dictionary.put(202, "想保持清醒,活力");
		dictionary.put(203, "想要減重");
		dictionary.put(204, "頭痛或其他地方重");
		dictionary.put(300, "高興");
		dictionary.put(301, "感到自在且放鬆");
		dictionary.put(302, "興奮");
		dictionary.put(303, "對目前生活感到滿意");
		dictionary.put(304, "想起以前發生的好事");
		dictionary.put(400, "想要證明毒品對自己不是問題");
		dictionary.put(401, "試試偶爾吸毒但不會上癮");
		dictionary.put(402, "試試和曾吸毒的朋友出去但不吸毒");
		dictionary.put(403, "試試在大家都吸毒的場合但不吸毒");
		dictionary.put(500, "在常用K或買K的地方");
		dictionary.put(501, "突然看到K");
		dictionary.put(502, "看到想用K的東西");
		dictionary.put(503, "聽到別人在談論用K經驗");
		dictionary.put(504, "喝酒且想用K");
		dictionary.put(505, "想到用K後的舒服感");
		dictionary.put(506, "沒來由地想用K");
		dictionary.put(600, "在他人面前感到緊張或不自在");
		dictionary.put(601, "難以向他人表達情感");
		dictionary.put(602, "不被別人喜歡");
		dictionary.put(603, "別人不公平對待");
		dictionary.put(604, "他人給過大壓力");
		dictionary.put(605, "在職場/學校和別人處得不好");
		dictionary.put(606, "家裡有人吵架");
		dictionary.put(607, "感覺需要勇氣才能面對他人");
		dictionary.put(608, "覺得被他人掌控");
		dictionary.put(700, "朋友邀你吸K, 覺得不好拒絕");
		dictionary.put(701, "朋友一直建議到某些地方吸Ｋ");
		dictionary.put(702, "周遭的人在吸Ｋ，想融入他們");
		dictionary.put(800, "與老朋友共度美好時光");
		dictionary.put(801, "跟朋友們同樂且想助興");
		dictionary.put(802, "想要助性");
		
	}
	
	public String getItems(int key){
		return dictionary.get(key);
		
	}
}
