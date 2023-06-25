package com.bluelock.simdetails.ui.setting

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.installreferrer.BuildConfig
import com.bluelock.simdetails.R
import com.bluelock.simdetails.databinding.FragmentSettingBinding
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingBinding =
        FragmentSettingBinding::inflate

    @Inject
    lateinit var googleManager: GoogleManager

    private var nativeAd: NativeAd? = null

    @Inject
    @GoogleAnalytics
    lateinit var analytics: Analytics

    @Inject
    lateinit var remoteConfig: RemoteConfig

    override fun onCreatedView() {
        observer()
        showNativeAd()
    }

    private fun observer() {
        lifecycleScope.launch {
            binding.apply {
                if (remoteConfig.showTitle) {
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.INVISIBLE
                }

                btnBack.setOnClickListener {
                    showInterstitialAd {
                        showInterstitialAd {
                            findNavController().navigateUp()
                        }
                    }
                }

                lTerm.setOnClickListener {
                    showInterstitialAd {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://bluelocksolutions.blogspot.com/2023/06/terms-and-conditions-for-pak-sim-details.html")
                        )
                        startActivity(intent)
                    }
                }
                lPrivacy.setOnClickListener {
                    showInterstitialAd {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://bluelocksolutions.blogspot.com/2023/06/privacy-policy-for-pak-sim-details-app.html")
                        )
                        startActivity(intent)
                    }
                }
                lContact.setOnClickListener {
                    showInterstitialAd {
                        val emailIntent = Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse("mailto:blue.lock.testing@gmail.com")
                        )
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FB Reel Downloader")
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "your message here")
                        startActivity(Intent.createChooser(emailIntent, "Chooser Title"))
                    }
                }
                lShare.setOnClickListener {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                            ${shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID}
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: java.lang.Exception) {
                        Log.d("jeje_e", e.toString())
                    }

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