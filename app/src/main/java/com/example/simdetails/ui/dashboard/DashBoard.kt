package com.example.simdetails.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.simdetails.R
import com.example.simdetails.data.adapters.CustomAdapter
import com.example.simdetails.data.model.Title
import com.example.simdetails.databinding.FragmentDashBoardBinding
import com.example.simdetails.ui.base.BaseFragment
import com.example.simdetails.utils.RecyclerviewClickListener
import com.example.simdetails.utils.Urls
import com.example.simdetails.utils.checkForInternet
import com.example.simdetails.utils.showBottomSheetDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class DashBoard : BaseFragment<FragmentDashBoardBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDashBoardBinding =
        FragmentDashBoardBinding::inflate

    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private val urls = Urls()
    val locationInfo = urls.locationInfo
    val cincInfo = urls.cincInfo

    override fun onCreatedView() {

        MobileAds.initialize(this@DashBoard.requireContext()) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        loadInterstitial()
        setUpRV()
        clickedListeners()
        if (checkForInternet(requireContext())) {
            checkForInternet(requireContext())
        } else {
            showBottomSheetDialog()
        }
    }

    private fun clickedListeners() {
        binding.apply {
            recyclerview.addOnItemTouchListener(
                RecyclerviewClickListener(requireActivity(), recyclerview,
                    object : RecyclerviewClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            if (position == 0) {
                                showInterstitial()
                                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToServerSelectionFragment())
                            }
                            if (position == 1) {
                                showInterstitial()
                                findNavController().navigate(
                                    DashBoardDirections.actionFragmentDashBoardToBrowserFragment(
                                        locationInfo
                                    )
                                )
                            }
                            if (position == 2) {
                                showInterstitial()
                                findNavController().navigate(
                                    DashBoardDirections.actionFragmentDashBoardToBrowserFragment(
                                        cincInfo
                                    )
                                )
                            }
                            if (position == 3) {
                                showInterstitial()
                                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToFragmentDiscalimer())
                            }
                            if (position == 4) {
                                showInterstitial()
                                findNavController().navigate(DashBoardDirections.actionFragmentDashBoardToFragmentContactInfo())

                            }

                        }

                        override fun onItemLongClick(view: View?, position: Int) {
                            showInterstitial()
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
            Title("Sim Details")
        )
        data.add(
            Title("Location Information")
        )
        data.add(
            Title("Cnic Information")
        )
        data.add(
            Title("Disclaimer")
        )
        data.add(
            Title("Contact Us")
        )

        val adapter = CustomAdapter(data)
        binding.recyclerview.adapter = adapter
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this@DashBoard.requireActivity())
        } else {
            Log.d("TAG", "ad wasn't ready")
        }
    }

    fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this@DashBoard.requireContext(),
            getString(R.string.ID_INTERSTITIAL),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    mInterstitialAd = p0
                }
            })
    }
}

