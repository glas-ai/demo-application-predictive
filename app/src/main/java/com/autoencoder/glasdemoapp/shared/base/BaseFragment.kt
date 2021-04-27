package com.autoencoder.glasdemoapp.shared.base

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

abstract class BaseFragment<BINDING: ViewDataBinding>(
    private val withSlideTransition: Boolean = false
) : Fragment() {

    protected var binding: BINDING? = null

    protected abstract val viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = if (withSlideTransition) Slide(Gravity.END) else Fade()
        exitTransition = if (withSlideTransition) Slide(Gravity.START) else Fade()
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeBaseCommands()
    }

    private fun observeBaseCommands() {
        viewModel.baseCmd.observe(viewLifecycleOwner) { command ->
            when (command) {
                is BaseCommand.ShowToastById ->
                    Toast.makeText(context ?: return@observe, command.messageId, Toast.LENGTH_SHORT)
                        .show()
                is BaseCommand.ShowToast ->
                    Toast.makeText(context ?: return@observe, command.message, Toast.LENGTH_SHORT)
                        .show()
                is BaseCommand.Navigate -> {
                    findNavController().navigate(command.direction)
                }
                is BaseCommand.NavigateUp -> {
                    onBack()
                    findNavController().navigateUp()
                }
            }
        }
    }

    abstract fun setupViews()

    open fun onBack() {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}