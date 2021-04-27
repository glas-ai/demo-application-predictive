package com.autoencoder.glasdemoapp.main.headingInformation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.HeadingInformationItemBinding
import com.autoencoder.glasdemoapp.models.HeadingInformationItem

class HeadingInformationAdapter :
    ListAdapter<HeadingInformationItem, HeadingInformationItemViewHolder>(
        object : DiffUtil.ItemCallback<HeadingInformationItem>() {
            override fun areItemsTheSame(
                oldItem: HeadingInformationItem,
                newItem: HeadingInformationItem
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: HeadingInformationItem,
                newItem: HeadingInformationItem
            ) = oldItem.headingNo == newItem.headingNo
        }
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = HeadingInformationItemViewHolder(
        HeadingInformationItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
    )

    override fun onBindViewHolder(holder: HeadingInformationItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.setVariable(BR.viewModel, item)
        holder.binding.executePendingBindings()
    }
}

class HeadingInformationItemViewHolder(val binding: HeadingInformationItemBinding) :
    RecyclerView.ViewHolder(binding.root)