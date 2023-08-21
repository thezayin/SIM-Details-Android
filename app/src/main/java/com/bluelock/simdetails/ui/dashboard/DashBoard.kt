package com.bluelock.simdetails.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bluelock.simdetails.databinding.FragmentDashBoardBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.base.BaseFragment
import com.bluelock.simdetails.utils.Urls
import com.bluelock.simdetails.utils.checkForInternet
import com.bluelock.simdetails.utils.showBottomSheetDialog
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
class DashBoard : BaseFragment<FragmentDashBoardBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDashBoardBinding =
        FragmentDashBoardBinding::inflate

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


    private val urls = Urls()
    private val cincInfo = urls.cincInfo

    override fun onCreatedView() {
        clickedListeners()
        if (remoteConfig.nativeAd) {
            showRecursiveAd()
            showNativeAd()
        }

        if (checkForInternet(requireContext())) {
            checkForInternet(requireContext())
        } else {
            showBottomSheetDialog()
        }

    }

    override fun onDestroyed() {
        //Ok
    }

    private fun clickedListeners() {
        binding.apply {
            btnSetting.setOnClickListener {
                showInterstitialAd { }
                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToSettingFragment())

            }
            btnCheck.setOnClickListener {
                showInterstitialAd { }
                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToServerSelectionFragment())
            }
            btnId.setOnClickListener {
                showInterstitialAd { }
                findNavController().navigate(
                    DashBoardDirections.actionFragmentDashBoardToBrowserFragment(cincInfo)
                )
            }
            btnDescliner.setOnClickListener {
                showInterstitialAd { }
                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToFragmentDiscalimer())
            }

        }
    }

    private fun showRecursiveAd() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (this.isActive) {
                    showDropDown()
                    showNativeAd()
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
        nativeAd = googleManager.createNativeAdSmall()
        nativeAd?.let {
            val nativeAdLayoutBinding = NativeAdBannerLayoutBinding.inflate(layoutInflater)
            nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
            binding.nativeView.removeAllViews()
            binding.nativeView.addView(nativeAdLayoutBinding.root)
            binding.nativeView.visibility = View.VISIBLE
        }
    }

    private fun showDropDown() {
        val nativeAdCheck = googleManager.createNativeAdForLanguage()
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

