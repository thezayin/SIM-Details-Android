package com.bluelock.simdetails.ui.webview

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bluelock.simdetails.databinding.FragmentBrowserBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.base.BaseFragment
import com.example.ads.GoogleManager
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
import javax.inject.Inject

@AndroidEntryPoint
class BrowserFragment : BaseFragment<FragmentBrowserBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBrowserBinding =
        FragmentBrowserBinding::inflate

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


    private val args: BrowserFragmentArgs by navArgs()
    lateinit var progress: ProgressDialog

    override fun onCreatedView() {
        showNativeAd()
        binding.apply {
            icBack.setOnClickListener {
                showInterstitialAd {
                    findNavController().navigateUp()
                }
            }
            webView.webViewClient = WebViewClient()
            val url: String = args.url
            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = false
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.setSupportZoom(true)
            progress = ProgressDialog.show(activity, "please waite a moment", "Loading...", true)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    progress.dismiss()
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
        if (remoteConfig.nativeAd) {
            nativeAd = googleManager.createNativeAdSmall()
            nativeAd?.let {
                val nativeAdLayoutBinding = NativeAdBannerLayoutBinding.inflate(layoutInflater)
                nativeAdLayoutBinding.nativeAdView.loadNativeAd(ad = it)
                binding.nativeView.removeAllViews()
                binding.nativeView.addView(nativeAdLayoutBinding.root)
                binding.nativeView.visibility = View.VISIBLE
            }
        }
    }

}