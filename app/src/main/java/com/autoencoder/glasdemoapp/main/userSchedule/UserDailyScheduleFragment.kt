package com.autoencoder.glasdemoapp.main.userSchedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.UserDailyScheduleFragmentBinding
import com.autoencoder.glasdemoapp.main.userSchedule.adapter.UserDailyScheduleAdapter
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDailyScheduleFragment: BaseFragment<UserDailyScheduleFragmentBinding>(true) {

    override val viewModel by viewModel<UserDailyScheduleViewModel>()

    private val adapter = UserDailyScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = UserDailyScheduleFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun setupViews() {
        binding?.schedule?.adapter = adapter
        setupObservers()
        viewModel.getUserDailySchedule()
    }

    private fun setupObservers() {
        viewModel.scheduleItem.observe(viewLifecycleOwner, adapter::submitList)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }
}