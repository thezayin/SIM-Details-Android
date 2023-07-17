package com.example.ads.newStrategy

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import java.util.Arrays
import java.util.Stack

class GoogleInterstitialReward(context: Context?) {
    private val totalLevels = 4
    private var adUnits: ArrayList<ArrayList<Any>>? = null
    private val adUnitId = "\tca-app-pub-3940256099942544/5354046379"

    init {
        instantiateRewardList()
        loadInitialInterstitialsReward(context)
    }

    private fun instantiateRewardList() {
        adUnits = ArrayList()
        adUnits!!.add(0, ArrayList(Arrays.asList(adUnitId, Stack<RewardedInterstitialAd>())))
        adUnits!!.add(1, ArrayList(Arrays.asList(adUnitId, Stack<RewardedInterstitialAd>())))
        adUnits!!.add(2, ArrayList(Arrays.asList(adUnitId, Stack<RewardedInterstitialAd>())))
        adUnits!!.add(3, ArrayList(Arrays.asList(adUnitId, Stack<RewardedInterstitialAd>())))
        adUnits!!.add(4, ArrayList(Arrays.asList(adUnitId, Stack<RewardedInterstitialAd>())))
    }

    fun loadInitialInterstitialsReward(context: Context?) {
        InterstitialRewardAdLoad(context, totalLevels)
    }

    fun getMediumAd(activity: Context?): RewardedInterstitialAd? {
        return getInterstitialRewardAd(activity, 1)
    }

    fun getHighFloorAd(activity: Context?): RewardedInterstitialAd? {
        return getInterstitialRewardAd(activity, 2)
    }

    fun getDefaultAd(activity: Context?): RewardedInterstitialAd? {
        Log.d("jeje_def", "defaultinterstial")
        return getInterstitialRewardAd(activity, 0)
    }

    fun getInterstitialRewardAd(activity: Context?, maxLevel: Int): RewardedInterstitialAd? {
        for (i in totalLevels downTo 0) {
            if (maxLevel > i) {
                break
            }
            val list = adUnits!![i]
            val adunitid = list[0] as String
            val stack = list[1] as Stack<RewardedInterstitialAd>
            InterstitialAdLoadSpecific(activity, adunitid, stack)
            if (stack != null && !stack.isEmpty()) {
                return stack.pop()
            }
        }
        return null
    }

    fun InterstitialRewardAdLoad(activity: Context?, level: Int) {
        if (level < 0) {
            return
        }
        if (adUnits!!.size < level) {
            return
        }
        val list = adUnits!![level]
        val adunitid = list[0] as String
        val stack = list[1] as Stack<RewardedInterstitialAd>
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            activity,
            adunitid,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    InterstitialRewardAdLoad(activity, level - 1)
                }

                override fun onAdLoaded(interstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    stack.push(interstitialAd)
                }
            })
    }

    fun InterstitialAdLoadSpecific(
        activity: Context?,
        adUnitId: String?,
        stack: Stack<RewardedInterstitialAd>?
    ) {
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            activity,
            adUnitId,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                }

                override fun onAdLoaded(interstitialAd: RewardedInterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    stack!!.push(interstitialAd)
                }
            })
    }
}