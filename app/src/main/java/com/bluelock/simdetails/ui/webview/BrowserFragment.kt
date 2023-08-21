package com.bluelock.simdetails.ui.webview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bluelock.simdetails.R
import com.bluelock.simdetails.databinding.DialogLoadingBinding
import com.bluelock.simdetails.databinding.FragmentBrowserBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.base.BaseFragment
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BrowserFragment : BaseFragment<FragmentBrowserBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBrowserBinding =
        FragmentBrowserBinding::inflate

    private var nativeAd: NativeAd? = null
    private var nativeDialog: NativeAd? = null

    @Inject
    lateinit var googleManager: GoogleManager

    @Inject
    @GoogleAnalytics
    lateinit var analytics: Analytics

    @Inject
    lateinit var remoteConfig: RemoteConfig

    @Inject
    lateinit var manager: GoogleManager

    private lateinit var dialog: BottomSheetDialog
    private val args: BrowserFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreatedView() {

        if (remoteConfig.nativeAd) {
            showNativeAd()
            showRecursiveAds()
        }
        binding.apply {
            icBack.setOnClickListener {
                    findNavController().navigateUp()

            }
            webView.webViewClient = WebViewClient()
            val url: String = args.url
            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = false
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.setSupportZoom(true)
            dialog = BottomSheetDialog(
                requireContext(),
                R.style.SheetDialog
            ).also { dialog ->
                val binding = DialogLoadingBinding.inflate(layoutInflater)
                dialog.setContentView(binding.root)
                dialog.behavior.isDraggable = false
                dialog.setCanceledOnTouchOutside(false)
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                if (remoteConfig.nativeAd) {
                    nativeDialog = googleManager.createNativeFull()
                    nativeDialog?.let {
                        val nativeAdLayoutBinding =
                            MediumNativeAdLayoutBinding.inflate(layoutInflater)
                        nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
                        binding.nativeViewAdDialog.removeAllViews()
                        binding.nativeViewAdDialog.addView(nativeAdLayoutBinding.root)
                        binding.nativeViewAdDialog.visibility = View.VISIBLE
                    }
                }
            }
            dialog.show()



            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    dialog.dismiss()
                }

            }
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
                    delay(10000L)
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
        nativeAd = googleManager.createNativeFull()
        nativeAd?.let {
            val nativeAdLayoutBinding = NativeAdBannerLayoutBinding.inflate(layoutInflater)
            nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
            binding.nativeView.removeAllViews()
            binding.nativeView.addView(nativeAdLayoutBinding.root)
            binding.nativeView.visibility = View.VISIBLE
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