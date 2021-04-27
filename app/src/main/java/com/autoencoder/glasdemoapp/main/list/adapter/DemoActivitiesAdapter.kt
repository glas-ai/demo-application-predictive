package com.autoencoder.glasdemoapp.main.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.DemoActivityItemBinding
import com.autoencoder.glasdemoapp.models.DemoActivityItem
import org.jetbrains.anko.sdk27.coroutines.onClick

class DemoActivitiesAdapter(private val onActivityClick: (item: DemoActivityItem) -> Unit = {}) :
    ListAdapter<DemoActivityItem, DemoActivityItemViewHolder>(
        object : DiffUtil.ItemCallback<DemoActivityItem>() {
        override fun areItemsTheSame(oldItem: DemoActivityItem, newItem: DemoActivityItem) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DemoActivityItem, newItem: DemoActivityItem) =
            oldItem.service == newItem.service
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoActivityItemViewHolder =
        DemoActivityItemViewHolder(DemoActivityItemBinding.inflate(LayoutInflater.from(parent.context)))


    override fun onBindViewHolder(holder: DemoActivityItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.setVariable(BR.viewModel, item)
        bind(holder.binding, item)
        holder.binding.executePendingBindings()
    }

    private fun bind(binding: DemoActivityItemBinding, item: DemoActivityItem) {
        binding.layout.onClick {
            onActivityClick(item)
        }
    }
}

class DemoActivityItemViewHolder(val binding: DemoActivityItemBinding) :
    RecyclerView.ViewHolder(binding.root)