package com.kerimovscreations.lateandroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.models.ReminderOption
import java.util.*

class ReminderOptionRecyclerViewAdapter(
    private val mContext: Context,
    private val mData: ArrayList<ReminderOption>
) : RecyclerView.Adapter<ReminderOptionRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playIc: ImageView = itemView.findViewById(R.id.ic_play)
        val selector: SwitchCompat = itemView.findViewById(R.id.selector)
        val title: TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_reminder_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.title.text = data.title
        holder.selector.isChecked = data.isSelected
        val playIcon =
            if (data.isPlaying) R.drawable.ic_pause_white_24dp else R.drawable.ic_play_arrow_white_24dp
        holder.playIc.setImageDrawable(AppCompatResources.getDrawable(mContext, playIcon))
        holder.selector.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mData[position].isSelected = isChecked
        }
        holder.playIc.setOnClickListener {
            if (mListener != null) {
                mListener!!.onPlay(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    interface OnInteractionListener {
        fun onPlay(index: Int)
    }

    private var mListener: OnInteractionListener? = null
    fun setOnInteractionListener(listener: OnInteractionListener?) {
        mListener = listener
    }

}