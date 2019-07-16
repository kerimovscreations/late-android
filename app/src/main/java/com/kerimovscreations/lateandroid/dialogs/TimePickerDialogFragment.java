package com.kerimovscreations.lateandroid.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.kerimovscreations.lateandroid.R;

import java.util.Objects;

public class TimePickerDialogFragment extends Dialog implements
        android.view.View.OnClickListener {

    private Context mContext;

    private Spinner mHourSpinner;
    private Spinner mMinuteSpinner;

    private int mHours = 0;
    private int mMinutes = 0;

    public TimePickerDialogFragment(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_time_picker);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mHourSpinner = findViewById(R.id.spinner_hour);
        ArrayAdapter<CharSequence> hoursAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.hours_array, android.R.layout.simple_spinner_item);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHours = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mHourSpinner.setAdapter(hoursAdapter);

        mMinuteSpinner = findViewById(R.id.spinner_minute);
        ArrayAdapter<CharSequence> minutesAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.minutes_array, android.R.layout.simple_spinner_item);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMinuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mMinutes = 0;
                        break;
                    case 1:
                        mMinutes = 5;
                        break;
                    case 2:
                        mMinutes = 10;
                        break;
                    case 3:
                        mMinutes = 15;
                        break;
                    case 4:
                        mMinutes = 20;
                        break;
                    case 5:
                        mMinutes = 30;
                        break;
                    case 6:
                        mMinutes = 40;
                        break;
                    case 7:
                        mMinutes = 50;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMinuteSpinner.setAdapter(minutesAdapter);

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    /* Click handlers */

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_submit){
            if(mListener != null){
                mListener.onSubmit(mHours * 60 + mMinutes);
                dismiss();
            }
        }
    }

    public interface OnInteractionListener {
        void onSubmit(int minutes);
    }

    private OnInteractionListener mListener;

    public void setOnInteractionListener(OnInteractionListener listener) {
        mListener = listener;
    }
}