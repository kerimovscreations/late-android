package com.kerimovscreations.eventreminder.dialogs;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;

public class PickerDialogFragment extends TimeDurationPickerDialogFragment {

    OnResult mListener;

    public interface OnResult {
        void onResult(long duration);
    }

    public void setOnResultListener(OnResult listener) {
        mListener = listener;
    }

    @Override
    protected long getInitialDuration() {
        return 20 * 60 * 1000;
    }


    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.HH_MM;
    }


    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        if (mListener != null) {
            mListener.onResult(duration);
        }
    }
}
