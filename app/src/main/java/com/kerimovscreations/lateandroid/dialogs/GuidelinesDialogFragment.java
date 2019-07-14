package com.kerimovscreations.lateandroid.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.kerimovscreations.lateandroid.R;

import java.util.Objects;

public class GuidelinesDialogFragment extends Dialog implements
        android.view.View.OnClickListener {

    public GuidelinesDialogFragment(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_guidelines);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    /* Click handlers */

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
