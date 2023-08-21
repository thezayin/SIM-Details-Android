package com.example.ads.newStrategy

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Stack

class GoogleAppOpen(context: Context?) {
    private val totalLevels = 4
    private var adUnits: ArrayList<ArrayList<Any>>? = null
    private val openHigh = "ca-app-pub-9507635869843997/8513033073"
    private val openMedium = "ca-app-pub-9507635869843997/2079169437"
    private val openOne = "ca-app-pub-9507635869843997/8453006093"
    private val openTwo = "ca-app-pub-9507635869843997/4242298348"
    private val openThree = "ca-app-pub-9507635869843997/9303053338"

    init {
        instantiateList()
        loadAppopenStart(context)
    }

    private fun instantiateList() {
        adUnits = ArrayList()
        adUnits!!.add(0, ArrayList(listOf(openHigh, Stack<AppOpenAd>())))
        adUnits!!.add(1, ArrayList(listOf(openMedium, Stack<AppOpenAd>())))
        adUnits!!.add(2, ArrayList(listOf(openOne, Stack<AppOpenAd>())))
        adUnits!!.add(3, ArrayList(listOf(openTwo, Stack<AppOpenAd>())))
        adUnits!!.add(4, ArrayList(listOf(openThree, Stack<AppOpenAd>())))
    }

    fun loadAppopenStart(context: Context?) {
        AppOpenAdLoad(context, totalLevels)
    }

    fun getAd(activity: Context?): AppOpenAd? {
        for (i in totalLevels downTo 0) {
            val list = adUnits!![i]
            val adunitid = list[0] as String
            val stack = list[1] as Stack<AppOpenAd>
            AppOpenLoadSpecific(activity, adunitid, stack)
            if (stack != null && !stack.isEmpty()) {
                return stack.pop()
            }
        }
        return null
    }

    fun AppOpenAdLoad(activity: Context?, level: Int) {
        if (level < 0) {
            return
        }
        if (adUnits!!.size < level) {
            return
        }
        val list = adUnits!![level]
        val adunitid = list[0] as String
        val stack = list[1] as Stack<AppOpenAd>
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            activity,
            adunitid,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    stack.push(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    AppOpenAdLoad(activity, level - 1)
                }
            })
    }

    fun AppOpenLoadSpecific(activity: Context?, adUnitId: String?, stack: Stack<AppOpenAd>?) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            activity,
            adUnitId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    stack!!.push(ad)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
            })
    }
}