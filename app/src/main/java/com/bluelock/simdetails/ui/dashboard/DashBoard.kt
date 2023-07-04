package com.bluelock.simdetails.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bluelock.simdetails.data.adapters.CustomAdapter
import com.bluelock.simdetails.data.model.Title
import com.bluelock.simdetails.databinding.FragmentDashBoardBinding
import com.bluelock.simdetails.remote.RemoteConfig
import com.bluelock.simdetails.ui.base.BaseFragment
import com.bluelock.simdetails.utils.RecyclerviewClickListener
import com.bluelock.simdetails.utils.Urls
import com.bluelock.simdetails.utils.checkForInternet
import com.bluelock.simdetails.utils.showBottomSheetDialog
import com.example.ads.GoogleManager
import com.example.ads.databinding.MediumNativeAdLayoutBinding
import com.example.ads.databinding.NativeAdBannerLayoutBinding
import com.example.ads.newStrategy.types.GoogleInterstitialType
import com.example.ads.ui.binding.loadNativeAd
import com.example.analytics.dependencies.Analytics
import com.example.analytics.events.AnalyticsEvent
import com.example.analytics.qualifiers.GoogleAnalytics
import com.example.analytics.utils.AnalyticsConstant
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import dagger.hilt.android.AndroidEntryPoint
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

    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private val urls = Urls()
    val locationInfo = urls.locationInfo
    val cincInfo = urls.cincInfo

    override fun onCreatedView() {
        setUpRV()
        clickedListeners()
        if (remoteConfig.showDropDownAd) {
            showDropDown()
        }
        showNativeAd()
        if (checkForInternet(requireContext())) {
            checkForInternet(requireContext())
        } else {
            showBottomSheetDialog()
        }


    }

    override fun onDestroyed() {
        showInterstitialAd { }
    }

    private fun clickedListeners() {
        binding.apply {
            btnSetting.setOnClickListener {
                analytics.logEvent(
                    AnalyticsEvent.NavigationEvent(
                        status = AnalyticsConstant.SETTING,
                        origin = AnalyticsConstant.DASHBOARD
                    )
                )
                showInterstitialAd {
                    findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToSettingFragment())
                }
            }


            recyclerview.addOnItemTouchListener(
                RecyclerviewClickListener(requireActivity(), recyclerview,
                    object : RecyclerviewClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            showInterstitialAd {
                                if (position == 0) {

                                    findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToServerSelectionFragment())

                                    analytics.logEvent(
                                        AnalyticsEvent.NavigationEvent(
                                            status = AnalyticsConstant.SERVER,
                                            origin = AnalyticsConstant.DASHBOARD
                                        )
                                    )
                                }
                                if (position == 1) {
                                    analytics.logEvent(
                                        AnalyticsEvent.NavigationEvent(
                                            status = AnalyticsConstant.CONTACT_INFO,
                                            origin = AnalyticsConstant.DASHBOARD
                                        )
                                    )

                                    findNavController().navigate(
                                        DashBoardDirections.actionFragmentDashBoardToBrowserFragment(
                                            cincInfo
                                        )
                                    )
                                }

                                if (position == 2) {

                                    analytics.logEvent(
                                        AnalyticsEvent.NavigationEvent(
                                            status = AnalyticsConstant.DISCLAIMER,
                                            origin = AnalyticsConstant.DASHBOARD
                                        )
                                    )
                                    findNavController().navigate(
                                        DashBoardDirections.actionFragmentDashBoardToFragmentDiscalimer()
                                    )
                                }
                                if (position == 3) {

                                    findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToFragmentDiscalimer())
                                }
                                if (position == 4) {

                                    findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToFragmentContactInfo())

                                }

                            }
                        }

                        override fun onItemLongClick(view: View?, position: Int) {
                            analytics.logEvent(
                                AnalyticsEvent.NavigationEvent(
                                    status = AnalyticsConstant.LONG_CLICK,
                                    origin = AnalyticsConstant.DASHBOARD
                                )
                            )

                            showInterstitialAd {
                                Toast.makeText(
                                    requireActivity(),
                                    "Please Click",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    })
            )
        }
    }

    private fun setUpRV() {

        val data = ArrayList<Title>()
        data.add(
            Title("Sim Details")
        )
        data.add(
            Title("Cnic Information")
        )
        data.add(
            Title("Disclaimer")
        )

        val adapter = CustomAdapter(data)
        binding.recyclerview.adapter = adapter
    }

    private fun showInterstitialRewardAd(callback: () -> Unit) {
        Log.d("jeje_inter", "intersetial_ad")
        if (remoteConfig.showInterstitial) {
            val ad: RewardedInterstitialAd? = googleManager.createInterstitialRewardAd()
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
            }
            ad.show(requireActivity(), null)
        } else {
            callback.invoke()
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

    private fun showDropDown() {
        val nativeAdCheck = googleManager.createNativeAdForLanguage()
        val nativeAd = googleManager.createNativeAdForLanguage()
        Log.d("ggg_nul", "nativeAd:${nativeAdCheck}")

        nativeAdCheck?.let {
            Log.d("ggg_lest", "nativeAdEx:${nativeAd}")
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
                analytics.logEvent(
                    AnalyticsEvent.AdDropDown(
                        click = AnalyticsConstant.DROP_DOWN_BTN_CLICKED,
                        origin = AnalyticsConstant.DASHBOARD
                    )
                )
            }
            binding.btnDropUp.visibility = View.INVISIBLE

        }

    }

}

