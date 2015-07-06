package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.statistic.coping.QuestionnaireDialog;
import com.ubicomp.ketdiary.statistic.coping.CallCheckOnClickListener;
import com.ubicomp.ketdiary.statistic.coping.CloseClickListener;
import com.ubicomp.ketdiary.statistic.coping.SelectedListener;
import com.ubicomp.ketdiary.system.PreferenceControl;

public class ConnectContent extends QuestionnaireContent {

	private int type;
	public static final int TYPE_FAMILY = 2, TYPE_SOCIAL = 3;
	
	public ConnectContent(QuestionnaireDialog msgBox,int type) {
		super(msgBox);
		this.type = type;
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		setHelp(R.string.call_to);
		msgBox.showQuestionnaireLayout(true);
		if (type == TYPE_FAMILY){
			String[] connectName = PreferenceControl.getConnectFamilyName();
			String[] connectPhone = PreferenceControl.getConnectFamilyPhone();
			
			int counter = 0;
			
			for (int i=0;i<3;++i){
				if (connectName[i].length()>0){
					setSelectItem(connectName[i],new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,connectName[i],connectPhone[i]),R.string.next));
					++counter;
				}
			}
			
			if (counter == 0){
				setHelp(R.string.connect_null);
				msgBox.showQuestionnaireLayout(false);
				msgBox.setNextButton(R.string.ok,new CloseClickListener(msgBox));
			}
		}else if(type == TYPE_SOCIAL){
			int[] idx = PreferenceControl.getConnectSocialHelpIdx();
			String n0 = ConnectSocialInfo.NAME[idx[0]];
			String n1 = ConnectSocialInfo.NAME[idx[1]];
			String n2 = ConnectSocialInfo.NAME[idx[2]];
			String p0 = ConnectSocialInfo.PHONE[idx[0]];
			String p1 = ConnectSocialInfo.PHONE[idx[1]];
			String p2 = ConnectSocialInfo.PHONE[idx[2]];
			setSelectItem(n0,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n0,p0),R.string.next));
			setSelectItem(n1,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n1,p1),R.string.next));
			setSelectItem(n2,new SelectedListener(msgBox,new CallCheckOnClickListener(msgBox,n2,p2),R.string.next));
		}
	}

}
