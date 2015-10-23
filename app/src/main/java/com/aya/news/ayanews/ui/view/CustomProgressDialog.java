package com.aya.news.ayanews.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.aya.news.ayanews.R;

/**
 * Created by Single on 2015/10/23.
 */
public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context );
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
    }

    public static CustomProgressDialog show(Context context){
        CustomProgressDialog progressDialog = new CustomProgressDialog(context);
        progressDialog.show();
        return progressDialog;
    }
}
