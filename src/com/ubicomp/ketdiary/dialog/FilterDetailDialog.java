package com.ubicomp.ketdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.ubicomp.ketdiary.R;

public class FilterDetailDialog extends Dialog{

    public FilterDetailDialog(Context context) {
        super(context, android.R.style.Theme_Light);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter_detail);
    }

}