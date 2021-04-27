package com.autoencoder.glasdemoapp.main.about.qt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.QTLicenseFragmentBinding
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.nio.charset.Charset

class QTLicenseFragment : BaseFragment<QTLicenseFragmentBinding>() {

    override val viewModel by viewModel<QTLicenseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = QTLicenseFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun setupViews() {
        binding?.license?.text = String(
            resources.assets.open("qt_license.txt").readBytes(), Charset.defaultCharset()
        )
    }
}