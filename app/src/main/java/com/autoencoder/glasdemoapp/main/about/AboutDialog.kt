package com.autoencoder.glasdemoapp.main.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.autoencoder.glasdemoapp.R
import com.google.android.material.tabs.TabLayoutMediator
import org.jetbrains.anko.sdk27.coroutines.onClick

class AboutDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.about_dialog, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.content_vp).apply {
            adapter = AboutTabsAdapter(this@AboutDialog)
        }
        TabLayoutMediator(view.findViewById(R.id.tabs) ?: return view, viewPager ?: return view) { tab, position ->
            tab.text = getString(AboutTabs.values()[position].title)
        }.attach()

        view.findViewById<TextView>(R.id.about_dismiss_button)?.onClick { dismiss() }

        return view
    }
}