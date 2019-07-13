package com.kerimovscreations.lateandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.models.ReminderOption;

import java.util.ArrayList;

public class ReminderOptionRecyclerViewAdapter extends RecyclerView.Adapter<ReminderOptionRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ReminderOption> mData;
    private Context mContext;

    public ReminderOptionRecyclerViewAdapter(Context context, ArrayList<ReminderOption> data) {
        mContext = context;
        mData = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView playIc;
        private SwitchCompat selector;
        private TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            playIc = itemView.findViewById(R.id.ic_play);
            selector = itemView.findViewById(R.id.selector);
            title = itemView.findViewById(R.id.title);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_reminder_option, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderOption data = mData.get(position);

        holder.title.setText(data.getTitle());
        holder.selector.setChecked(data.isSelected());
        holder.playIc.setImageDrawable(mContext.getDrawable(data.isPlaying() ?
                R.drawable.ic_stop_white_24dp : R.drawable.ic_play_arrow_white_24dp));

        holder.selector.setOnCheckedChangeListener((buttonView, isChecked) -> mData.get(position).setSelected(isChecked));
        holder.playIc.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onPlay(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnInteractionListener {
        void onPlay(int index);
    }

    private OnInteractionListener mListener;

    public void setOnInteractionListener(OnInteractionListener listener) {
        mListener = listener;
    }
}
