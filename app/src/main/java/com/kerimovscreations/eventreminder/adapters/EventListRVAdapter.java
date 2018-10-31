package com.kerimovscreations.eventreminder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kerimovscreations.eventreminder.R;
import com.kerimovscreations.eventreminder.models.Event;

import java.util.List;

public class EventListRVAdapter extends RecyclerView.Adapter<EventListRVAdapter.ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(EventListRVAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        View layout;

        ViewHolder(final View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.event_layout);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);

            layout.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onClick(position);
                    }
                }
            });

            layout.setOnLongClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onLongClick(position);
                    }
                }
                return true;
            });
        }
    }

    private List<Event> mList;
    private Context mContext;

    public EventListRVAdapter(Context context) {
        mContext = context;
    }

    public void setEvents(List<Event> events){
        mList = events;
        notifyDataSetChanged();
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.list_item_event, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Event bItem = mList.get(position);

        viewHolder.title.setText(bItem.getTitle());
        viewHolder.date.setText(bItem.getDate());
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else return 0;
    }
}