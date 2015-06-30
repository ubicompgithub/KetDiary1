package com.ubicomp.ketdiary.ui;

import android.app.ProgressDialog;
import android.content.Context;

import com.ubicomp.ketdiary.R;

public class LoadingDialogControl {

	private static ProgressDialog dialog = null;

	public static void show(Context context, int type) {

		if (dialog == null) {
			dialog = new ProgressDialog(context);
		} else if (dialog.getContext() == null
				|| !dialog.getContext().equals(context)) {
			try {
				dialog.cancel();
			} catch (Exception e) {
			}
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		dialog.setCancelable(false);
		if (!dialog.isShowing()) {
			try {
				dialog.show();
				if (type == 0)
					dialog.setContentView(R.layout.dialog_loading);
				else if (type == 1)
					dialog.setContentView(R.layout.dialog_upload);
			} catch (Exception e) {
			}
		}
	}

	public static void dismiss() {
		if (dialog == null)
			return;
		dialog.cancel();
		if (dialog.isShowing())
			dialog.dismiss();
	}

	public static void show(Context context) {
		show(context, 0);
	}
}
