package com.autoencoder.glasdemoapp.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.databinding.MainActivityBinding
import glas.ai.sdk.GlasAI
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainActivityViewModel>()
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.setVariable(BR.viewModel, viewModel)
        }
        setContentView(binding.root)

        if (GlasAI.instance().isReady) {
            GlasAI.instance().dataIO().queryAvailableDataTypes();
        }
    }
}
