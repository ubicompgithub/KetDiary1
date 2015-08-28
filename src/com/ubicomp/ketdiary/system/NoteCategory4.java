package com.ubicomp.ketdiary.system;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class NoteCategory4 {
	
	
	@SuppressLint("UseSparseArrays")
	public Map <Integer, String> negative = new TreeMap< Integer, String >();
	public Map <Integer, String> notgood = new TreeMap< Integer, String >();
	public Map <Integer, String> positive = new TreeMap< Integer, String >();
	public Map <Integer, String> selftest = new TreeMap< Integer, String >();
	public Map <Integer, String> temptation = new TreeMap< Integer, String >();
	public Map <Integer, String> conflict = new TreeMap< Integer, String >();
	public Map <Integer, String> social = new TreeMap< Integer, String >();
	public Map <Integer, String> play = new TreeMap< Integer, String >();
	
	public Map <String, Integer> myNewHashMap = new HashMap<String, Integer>();
	public HashMap <Integer, String> dictionary = new HashMap< Integer, String >();
	//public HashMap <Integer, String>[] array = new HashMap[8]; 
		
		
	/** create instance*/
	
	public NoteCategory4(){
		
		noteSetting();
	}
	

	public void noteSetting(){
		negative.put(100, "沮喪");
		negative.put(101, "走投無路");
		negative.put(102, "對自己失望");
		negative.put(103, "無聊");
		negative.put(104, "寂寞");
		negative.put(105, "緊張,焦慮");
		negative.put(106, "感到罪惡");
		negative.put(107, "壓力大,想逃避");
		negative.put(108, "對某事感到生氣");
		negative.put(109, "不知道自己該怎麼辦");
		
		notgood.put(200, "生病,噁心,疲倦");
		notgood.put(201, "失眠");
		notgood.put(202, "想保持清醒,活力");
		notgood.put(203, "想要減重");
		notgood.put(204, "頭痛或其他地方痛");
		notgood.put(205, "為了健康");
		notgood.put(206, "吃了醫生開的藥");
		
		positive.put(300, "高興");
		positive.put(301, "感到自在且放鬆");
		positive.put(302, "興奮");
		positive.put(303, "對目前生活感到滿意");
		positive.put(304, "想起以前發生的好事");
		positive.put(305, "不想再花錢在買K上");
		
		selftest.put(400, "想要證明毒品對自己不是問題");
		selftest.put(401, "試試偶爾吸毒但不會上癮");
		selftest.put(402, "試試和曾吸毒的朋友出去但不吸毒");
		selftest.put(403, "試試在大家都吸毒的場合但不吸毒");
		
		temptation.put(500, "在常用K或買K的地方");
		temptation.put(501, "突然看到K");
		temptation.put(502, "看到想用K的東西");
		temptation.put(503, "聽到別人在談論用K經驗");
		temptation.put(504, "喝酒且想用K");
		temptation.put(505, "想到用K後的舒服感");
		temptation.put(506, "沒來由地想用K");
		temptation.put(507, "已習慣用K,不用K會不對勁");
		temptation.put(508, "找事情做");
		temptation.put(509, "培養其他興趣、專長");
		temptation.put(510, "想避開K");
		temptation.put(511, "找替代品");
		temptation.put(512, "深呼吸");
		temptation.put(513, "運動");
		
		conflict.put(600, "在他人面前感到緊張或不自在");
		conflict.put(601, "難以向他人表達情感");
		conflict.put(602, "不被別人喜歡");
		conflict.put(603, "別人不公平對待");
		conflict.put(604, "他人給過大壓力");
		conflict.put(605, "在職場/學校和別人處得不好");
		conflict.put(606, "家裡有人吵架");
		conflict.put(607, "感覺需要勇氣才能面對他人");
		conflict.put(608, "覺得被他人掌控");
		
		social.put(700, "朋友邀你吸K, 覺得不好拒絕");
		social.put(701, "朋友一直建議到某些地方吸Ｋ");
		social.put(702, "周遭的人在吸Ｋ，想融入他們");
		social.put(703, "為了感情");
		social.put(704, "為了人際關係");
		
		play.put(800, "與老朋友共度美好時光");
		play.put(801, "跟朋友們同樂");
		play.put(802, "希望對性行為有幫助");
		play.put(803, "與動物相處");
		
		for(Map.Entry<Integer, String> entry : negative.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : notgood.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : positive.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : selftest.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : temptation.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : conflict.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : social.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		for(Map.Entry<Integer, String> entry : play.entrySet()){
		    myNewHashMap.put(entry.getValue(), entry.getKey());
		}
		
		for(Map.Entry<String, Integer> entry : myNewHashMap.entrySet()){
		    dictionary.put(entry.getValue(), entry.getKey());
		}

	}
	
	
	
}
