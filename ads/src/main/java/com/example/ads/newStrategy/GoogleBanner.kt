package com.example.ads.newStrategy

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import timber.log.Timber
import java.util.ArrayDeque
import java.util.Arrays

class GoogleBanner(context: Context?) {
    private val adUnitId = "ca-app-pub-9507635869843997/7328269912"
    private var adUnits: ArrayList<ArrayList<Any>>? = null
    private val totalLevels = 4

    init {
        instantiateList()
        loadBannerAd(context)
    }

    private fun instantiateList() {
        adUnits = ArrayList()
        adUnits!!.add(0, ArrayList(Arrays.asList(adUnitId, ArrayDeque<AppOpenAd>())))
        adUnits!!.add(1, ArrayList(Arrays.asList(adUnitId, ArrayDeque<AppOpenAd>())))
        adUnits!!.add(2, ArrayList(Arrays.asList(adUnitId, ArrayDeque<AppOpenAd>())))
        adUnits!!.add(3, ArrayList(Arrays.asList(adUnitId, ArrayDeque<AppOpenAd>())))
        adUnits!!.add(4, ArrayList(Arrays.asList(adUnitId, ArrayDeque<AppOpenAd>())))
    }

    fun getDefaultAd(context: Context?): AdView? {
        Log.d(TAG, "getDefaultAd()")
        val levels = totalLevels
        for (i in levels downTo 0) {
            val list = adUnits!![i]
            val adunitid = list[0] as String
            val queue = list[1] as ArrayDeque<AdView>
            loadSpecificBannerAd(context, adunitid, queue)
            if (queue != null && !queue.isEmpty()) {
                Log.d(TAG, "getDefaultAd: $queue")
                val ad = queue.poll()
                Log.d(TAG, "getDefaultAd: $queue")
                return ad
            }
        }
        return null
    }

    @JvmOverloads
    fun loadBannerAd(context: Context?, level: Int = totalLevels) {
        if (level < 0) {
            return
        }
        if (adUnits!!.size < level) {
            Timber.tag("ERROR").e("Size is less than ad Units size")
        }
        val list = adUnits!![level]
        val adUnitId = list[0] as String
        val queue = list[1] as ArrayDeque<AdView>
        val ad = loadBannerAd(context, adUnitId)
        ad.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "onAdLoaded: $ad")
                queue.add(ad)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Timber.tag(TAG).e("onAdFailedToLoad(error: " + loadAdError.message + ")")
                loadBannerAd(context, level - 1)
            }
        }
    }

    fun loadSpecificBannerAd(context: Context?, adUnitId: String, queue: ArrayDeque<AdView>?) {
        val ad = loadBannerAd(context, adUnitId)
        ad.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "loadSpecificBannerAd: $ad")
                queue!!.add(ad)
            }
        }
    }

    private fun loadBannerAd(context: Context?, adUnitId: String): AdView {
        val adView = AdView(context)
        adView.adUnitId = adUnitId
        adView.adSize = AdSize.BANNER
        adView.loadAd(AdRequest.Builder().build())
        Log.d(TAG, "loadBannerAd: " + adView.parent)
        return adView
    }

    companion object {
        private const val TAG = "GoogleBanner"
        const val BANNER_TEST = "ca-app-pub-9507635869843997/7328269912"
        const val BANNER_ALL = "ca-app-pub-9507635869843997/7328269912"
    }
}