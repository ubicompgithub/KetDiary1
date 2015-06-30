package com.ubicomp.ketdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.ubicomp.ketdiary.R;

public class MyDialog extends Dialog{

    public MyDialog(Context context) {
        super(context, android.R.style.Theme_Light);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_diary_detail);
    }

}