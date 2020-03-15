package com.kerimovscreations.lateandroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.databinding.ListItemCustomSoundBinding
import com.kerimovscreations.lateandroid.models.ReminderOption

class CustomSoundRVAdapter(private val mData: ArrayList<ReminderOption>) :
        RecyclerView.Adapter<CustomSoundRVAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListItemCustomSoundBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReminderOption) {
            binding.title.text = item.title
            binding.editIc.visibility = if (item.soundFile.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            val actionResId = if (item.soundFile.isEmpty()) {
                R.drawable.ic_add_white_24dp
            } else if (item.isPlaying) {
                R.drawable.ic_pause_white_24dp
            } else {
                R.drawable.ic_play_arrow_white_24dp
            }

            binding.actionIc.setImageDrawable(ContextCompat.getDrawable(binding.root.context, actionResId))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemCustomSoundBinding>(LayoutInflater.from(parent.context), R.layout.list_item_custom_sound, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.bind(data)

        holder.binding.actionIc.setOnClickListener {
            mListener?.onAction(position)
        }

        holder.binding.root.setOnClickListener {
            mListener?.onAction(position)
        }

        holder.binding.editIc.setOnClickListener {
            mListener?.onEdit(position)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    interface OnInteractionListener {
        fun onAction(index: Int)
        fun onEdit(index: Int)
    }

    private var mListener: OnInteractionListener? = null

    fun setOnInteractionListener(listener: OnInteractionListener) {
        mListener = listener
    }

}