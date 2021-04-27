package com.autoencoder.glasdemoapp.main.userSchedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.UserDailyScheduleItemBinding
import com.autoencoder.glasdemoapp.models.UserDailyScheduleItem

class UserDailyScheduleAdapter :
    ListAdapter<UserDailyScheduleItem, UserDailyScheduleItemViewHolder>(
        object : DiffUtil.ItemCallback<UserDailyScheduleItem>() {
            override fun areItemsTheSame(
                oldItem: UserDailyScheduleItem,
                newItem: UserDailyScheduleItem
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: UserDailyScheduleItem,
                newItem: UserDailyScheduleItem
            ) = (oldItem.day == newItem.day) && (oldItem.isArrival == newItem.isArrival)
        }
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = UserDailyScheduleItemViewHolder(
        UserDailyScheduleItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
    )

    override fun onBindViewHolder(holder: UserDailyScheduleItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.setVariable(BR.viewModel, item)
        holder.binding.executePendingBindings()
    }
}

class UserDailyScheduleItemViewHolder(val binding: UserDailyScheduleItemBinding) :
    RecyclerView.ViewHolder(binding.root)