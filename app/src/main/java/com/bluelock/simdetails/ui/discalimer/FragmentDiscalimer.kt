package com.bluelock.simdetails.ui.discalimer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bluelock.simdetails.databinding.FragmentDiscalimerBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.base.BaseFragment
import com.bluelock.simdetails.utils.isConnected
import com.example.ads.GoogleManager
import com.example.ads.databinding.MediumNativeAdLayoutBinding
import com.example.ads.databinding.NativeAdBannerLayoutBinding
import com.example.ads.newStrategy.types.GoogleInterstitialType
import com.example.ads.ui.binding.loadNativeAd
import com.example.analytics.dependencies.Analytics
import com.example.analytics.qualifiers.GoogleAnalytics
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentDiscalimer : BaseFragment<FragmentDiscalimerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDiscalimerBinding =
        FragmentDiscalimerBinding::inflate


    private var nativeAd: NativeAd? = null

    @Inject
    lateinit var googleManager: GoogleManager

    @Inject
    @GoogleAnalytics
    lateinit var analytics: Analytics

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var manager: GoogleManager


    override fun onCreatedView() {

        if (remoteConfig.nativeAd) {
            showNativeAd()
            showRecursiveAds()
        }

        binding.icBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onDestroyed() {
    }

    private fun showRecursiveAds() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (this.isActive) {
                    showNativeAd()
                    showDropDown()
                    showInterstitialAd { }
                    delay(20000L)
                }
            }
        }
    }

    private fun showInterstitialAd(callback: () -> Unit) {
        if (remoteConfig.showInterstitial) {
            val ad: InterstitialAd? =
                googleManager.createInterstitialAd(GoogleInterstitialType.MEDIUM)

            if (ad == null) {
                callback.invoke()
                return
            } else {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        callback.invoke()
                    }

                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        super.onAdFailedToShowFullScreenContent(error)
                        callback.invoke()
                    }
                }
                ad.show(requireActivity())
            }
        } else {
            callback.invoke()
        }
    }

    private fun showNativeAd() {
        if (!requireContext().isConnected()) return
        nativeAd = googleManager.createNativeAdForLanguage()

        nativeAd?.let {
            val nativeAdLayoutBinding = MediumNativeAdLayoutBinding.inflate(layoutInflater)
            nativeAdLayoutBinding.nativeAdView.loadNativeAd(nativeAd)
            nativeAdLayoutBinding.nativeAdView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            binding.nativeView.addView(nativeAdLayoutBinding.root)

        }
    }

    private fun showDropDown() {
        val nativeAdCheck = googleManager.createNativeFull()
        nativeAdCheck?.let {
            binding.apply {
                dropLayout.bringToFront()
                nativeViewDrop.bringToFront()
            }
            val nativeAdLayoutBinding = MediumNativeAdLayoutBinding.inflate(layoutInflater)
            nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
            binding.nativeViewDrop.removeAllViews()
            binding.nativeViewDrop.addView(nativeAdLayoutBinding.root)
            binding.nativeViewDrop.visibility = View.VISIBLE
            binding.dropLayout.visibility = View.VISIBLE

            binding.btnDropDown.setOnClickListener {
                binding.dropLayout.visibility = View.GONE
            }
            binding.btnDropUp.visibility = View.INVISIBLE

        }

    }

}