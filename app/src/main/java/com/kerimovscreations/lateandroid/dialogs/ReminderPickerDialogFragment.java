package com.kerimovscreations.lateandroid.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.recyclerview.widget.RecyclerView;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.adapters.ReminderOptionRecyclerViewAdapter;
import com.kerimovscreations.lateandroid.models.ReminderOption;
import com.kerimovscreations.lateandroid.tools.HelpFunctions;

import java.util.ArrayList;
import java.util.Objects;

public class ReminderPickerDialogFragment extends Dialog implements
        android.view.View.OnClickListener {

    private Context mContext;
    private int mMinutes;
    private MediaPlayer mMediaPlayer;
    private ArrayList<ReminderOption> mOptions;
    private ReminderOptionRecyclerViewAdapter mAdapter;
    private int mPlayingIndex = -1;

    public ReminderPickerDialogFragment(Context context, int minutes) {
        super(context);
        mContext = context;
        mMinutes = minutes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reminder_picker);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        mOptions = new ArrayList<>();

        mOptions.add(new ReminderOption(mContext.getString(R.string.mins_0), 0));

        if (mMinutes > 5) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_5), 5));
        }
        if (mMinutes > 10) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_10), 10));
        }
        if (mMinutes > 15) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_15), 15));
        }
        if (mMinutes > 20) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_20), 20));
        }
        if (mMinutes > 30) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_30), 30));
        }
        if (mMinutes > 60) {
            mOptions.add(new ReminderOption(mContext.getString(R.string.mins_60), 60));
        }

        RecyclerView recyclerView = findViewById(R.id.rv_options);
        mAdapter = new ReminderOptionRecyclerViewAdapter(mContext, mOptions);
        mAdapter.setOnInteractionListener(this::playSound);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.stopSound();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (mListener != null) {
                    mListener.onSubmit(mOptions);
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void playSound(int index) {
        stopSound();

        if (mPlayingIndex >= 0) {
            mOptions.get(mPlayingIndex).setPlaying(false);
            mAdapter.notifyItemChanged(mPlayingIndex);

            if (mPlayingIndex == index) {
                return;
            }
        }

        if (mOptions.get(index).isPlaying()) {
            mOptions.get(index).setPlaying(false);
            mAdapter.notifyItemChanged(index);
            return;
        } else {
            mPlayingIndex = index;
            mOptions.get(index).setPlaying(true);
            mAdapter.notifyItemChanged(index);
        }

        int resourceId = HelpFunctions.shared.getNotificationData(getContext(), mOptions.get(index).getValue()).getInt("SOUND_ID", R.raw.en_male_mins_0_left);

        mMediaPlayer = MediaPlayer.create(mContext, resourceId);
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stopSound();
            mOptions.get(index).setPlaying(false);
            mAdapter.notifyItemChanged(index);
            mPlayingIndex = -1;
        });
        mMediaPlayer.start();
    }

    public interface OnInteractionListener {
        void onSubmit(ArrayList<ReminderOption> options);
    }

    private OnInteractionListener mListener;

    public void setOnInteractionListener(OnInteractionListener listener) {
        mListener = listener;
    }
}
