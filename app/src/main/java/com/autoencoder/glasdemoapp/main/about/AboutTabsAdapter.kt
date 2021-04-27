package com.autoencoder.glasdemoapp.main.about

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.autoencoder.glasdemoapp.main.about.glas.AboutGlasFragment
import com.autoencoder.glasdemoapp.main.about.qt.QTLicenseFragment

class AboutTabsAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount() = AboutTabs.values().size

    override fun createFragment(position: Int): Fragment {
        return when (AboutTabs.values()[position]) {
            AboutTabs.ABOUT_GLAS -> AboutGlasFragment()
            AboutTabs.QT_LICENSE -> QTLicenseFragment()
        }
    }
}