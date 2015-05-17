package com.ubicomp.ketdiary.db;

import java.util.Vector;

public class NoteCatagory {
	
	/** create instance*/
	public static NoteCatagory inst = new NoteCatagory();
	private NoteCatagory(){
		note = new _note();	
	}
	
	/**
	 * data of pair<String, int>
	 * @author mudream
	 *
	 */
	public class data{
		String str;
		int id;
		public data(String _str, int _id){
			str = _str;
			id = _id;
		}
	}
	
	/**
	 * Vector<String> -> String[]
	 * @param inp
	 * @return
	 */
	public String[] VectorDataToStringArr(Vector<data> inp){
		String[] ret_val = new String[inp.size()];
		for(int lx = 0;lx < inp.size();lx++){
			ret_val[lx] = inp.get(lx).str;
		}
		return ret_val;
	}
	
	public class _note{
		
		/** Data of Catagory -> items*/
		public Vector<data> negative = new Vector<data>();
		public Vector<data> notgood = new Vector<data>();
		public Vector<data> positive = new Vector<data>();
		public Vector<data> selftest = new Vector<data>();
		public Vector<data> temptation = new Vector<data>();
		public Vector<data> conflict = new Vector<data>();
		public Vector<data> social = new Vector<data>();
		public Vector<data> play = new Vector<data>();
		
		/**
		 * Setup data of NoteCatagory
		 */
		public _note(){
			negative.add(new data("沮喪", 0));
			negative.add(new data("走投無路", 1));
			negative.add(new data("讓自己失望", 2));
			negative.add(new data("無聊", 3 ));
			negative.add(new data("寂寞", 4 ));
			negative.add(new data("對某事感到緊張, 焦慮", 5 ));
			negative.add(new data("對某事感到罪惡感", 6 ));
			negative.add(new data("壓力大, 想逃避", 7 ));
			negative.add(new data("對已經發生的事感到生氣", 8 ));
			negative.add(new data("困惑於自己該怎麼做", 9 ));
			
			notgood.add(new data("感到生病, 噁心, 疲倦", 10 ));
			notgood.add(new data("失眠", 11 ));
			notgood.add(new data("想要保持清醒, 保持活力", 12 ));
			notgood.add(new data("想要減重", 13 ));
			notgood.add(new data("頭痛或其他地方痛", 14 ));
			
			positive.add(new data("高興", 15 ));
			positive.add(new data("感到自在且放鬆", 16 ));
			positive.add(new data("對某事感到興奮", 17 ));
			positive.add(new data("對生活感到滿意", 18 ));
			positive.add(new data("想起某些曾經發生的好事", 19 ));
			
			selftest.add(new data("想知道自己是否能適量用藥", 20 ));
			selftest.add(new data("想要對自己證明藥物對自己不是問題", 21 ));
			selftest.add(new data("想知道自己能否偶爾用藥但不會上癮", 22 ));
			selftest.add(new data("想知道和常用藥的朋友出去會不會也一起用藥", 23 ));
			selftest.add(new data("想知道自己能否處在大家都在用藥的地方但不用要", 24 ));

			temptation.add(new data("處在我常用藥或常買毒品的地方", 25 ));
			temptation.add(new data("意外發現放毒品的地方或看到會讓我想用藥的東西", 26 ));
			temptation.add(new data("喝酒且想用藥", 27 ));
			temptation.add(new data("聽到別人在談論用藥經驗", 28 ));
			temptation.add(new data("想到用藥之後的感覺有多舒服", 29 ));
			
			conflict.add(new data("在他人面前感到緊張或不自在", 30 ));
			conflict.add(new data("難以向他人表達情感", 31 ));
			conflict.add(new data("被別人拒絕或感覺不被喜歡", 32 ));
			conflict.add(new data("別人不公平對待或打亂計畫", 33 ));
			conflict.add(new data("家人給太大的壓力或覺得自己無法達到他們期許", 34 ));
			conflict.add(new data("在職場/學校和別人處得不太好", 35 ));
			conflict.add(new data("當家裡有人吵架", 36 ));
			conflict.add(new data("課業/工作壓力大或沒達到他人期望", 37 ));
			conflict.add(new data("感覺需要勇氣才能面對某人", 38 ));
			conflict.add(new data("覺得被某人掌控, 需要空間", 39 ));
			
			social.add(new data("去朋友家, 朋友邀約用藥, 拒絕好像不太好", 40 ));
			social.add(new data("跟朋友出去, 且朋友一直建議到某些地方用藥", 41 ));
			social.add(new data("在同一處的其他人在用藥且他們希望我加入", 42 ));
			social.add(new data("被邀請用藥而且覺得很難拒絕", 43 ));
			social.add(new data("在每個人都用藥的地方", 44 )); 
			
			play.add(new data("遇到老朋友且想要共度美好時光", 45 ));
			play.add(new data("跟親密好友再一起且想要感覺更親近", 46 ));
			play.add(new data("跟朋友們在一起且想助興", 47 ));
			play.add(new data("想要跟好友慶祝", 48 ));
			play.add(new data("想要助性", 49 ));

		}
	}
	
	public _note note;
}
