package com.bluelock.simdetails.ui.selectserver

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bluelock.simdetails.data.adapters.CustomAdapter
import com.bluelock.simdetails.data.model.Title
import com.bluelock.simdetails.databinding.FragmentServerSelectionBinding
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
import com.example.analytics.qualifiers.GoogleAnalytics
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServerSelectionFragment : BaseFragment<FragmentServerSelectionBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentServerSelectionBinding =
        FragmentServerSelectionBinding::inflate

    private val viewModel by viewModels<ServerViewModel>()

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
    val server1: String = urls.server1
    val server2: String = urls.server2
    val server3: String = urls.server3
    val server4: String = urls.server4

    override fun onCreatedView() {
        clickedListeners()
        setUpRV()
        showNativeAd()
        showDropDown()
        if (checkForInternet(requireContext())) {
            checkForInternet(requireContext())
        } else {
            showBottomSheetDialog()
        }
    }

    override fun onDestroyed() {
        showInterstitialAd {  }
    }


    private fun clickedListeners() {

        binding.apply {
            icBack.setOnClickListener {
                showInterstitialAd {
                    findNavController().navigateUp()
                }
            }
            recyclerview.addOnItemTouchListener(
                RecyclerviewClickListener(requireActivity(), recyclerview,
                    object : RecyclerviewClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            showInterstitialAd {
                                if (position == 0) {
                                    findNavController().navigate(
                                        ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                            server1
                                        )
                                    )
                                }
                                if (position == 1) {
                                    findNavController().navigate(
                                        ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                            server2
                                        )
                                    )
                                }
                                if (position == 2) {
                                    findNavController().navigate(
                                        ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                            server3
                                        )
                                    )
                                }
                                if (position == 3) {
                                    findNavController().navigate(
                                        ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                            server4
                                        )
                                    )
                                }
                            }
                        }

                        override fun onItemLongClick(view: View?, position: Int) {
                            Toast.makeText(requireActivity(), "Please Click", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            )
        }
    }

    private fun setUpRV() {

        val data = ArrayList<Title>()
        data.add(
            Title("Server 1")
        )
        data.add(
            Title("Server 2")
        )
        data.add(
            Title("Server 3")
        )
        data.add(
            Title("Server 4")
        )


        val adapter = CustomAdapter(data)
        binding.recyclerview.adapter = adapter
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
        val nativeAdCheck = googleManager.createNativeFull()
        val nativeAd = googleManager.createNativeFull()
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
            }
            binding.btnDropUp.visibility = View.INVISIBLE

        }

    }

}