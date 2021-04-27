package com.autoencoder.glasdemoapp.main.about.glas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.AboutGlassFragmentBinding
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutGlasFragment: BaseFragment<AboutGlassFragmentBinding>() {

    override val viewModel by viewModel<AboutGlasViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AboutGlassFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun setupViews() {
        return
    }
}