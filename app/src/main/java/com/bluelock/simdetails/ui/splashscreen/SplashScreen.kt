package com.bluelock.simdetails.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.bluelock.simdetails.databinding.SplashscreenBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.MainActivity
import com.bluelock.simdetails.utils.isConnected
import com.example.ads.GoogleManager
import com.example.ads.newStrategy.types.GoogleInterstitialType
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: SplashscreenBinding

    var progressStatus = 0

    @Inject
    lateinit var googleManager: GoogleManager

    @Inject
    lateinit var remoteConfig: RemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.apply {

            progressStatus = progressBar.progress

            lifecycleScope.launch {
                while (true) {
                    delay(400)

                    if (progressStatus < 100) {
                        progressBar.progress = progressStatus
                        progressStatus += 10

                    } else {
                        if (remoteConfig.showAppOpenAd) {
                            if (getAppOpenAd()) {
                                Log.d("jejesplash", "done")
                            } else {
                                showInterstitialAd {
                                    navigateToNextScreen()
                                }
                            }
                        } else {
                            showInterstitialAd {
                                navigateToNextScreen()
                            }
                        }
                        break
                    }
                }
            }
        }
    }



    private fun navigateToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun getAppOpenAd(): Boolean {

        if (!this.isConnected()) return false

        val ad = googleManager.createAppOpenAd() ?: return false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                navigateToNextScreen()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                navigateToNextScreen()
            }
        }
        ad.show(this)
        return true

        return false
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
                ad.show(this)
            }
        } else {
            callback.invoke()
        }
    }
}