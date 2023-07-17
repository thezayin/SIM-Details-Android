package com.example.analytics.events

import android.os.Bundle
import com.example.analytics.utils.AnalyticsConstant
import com.example.analytics.utils.AnalyticsConstant.ENHANCE_FEATURE_SELECTION
import com.example.analytics.utils.AnalyticsConstant.IMAGE_SAVED
import com.example.analytics.utils.AnalyticsConstant.INAPP_PURCHASE
import com.example.analytics.utils.AnalyticsConstant.INTERSTITIAL_AD
import com.example.analytics.utils.AnalyticsConstant.REWARDED_AD

sealed class
AnalyticsEvent(
    val event: String? = null,
    val args: Bundle?
) {

    companion object {
        const val SCREEN_VIEW = "screenView"
    }

    class InAppPurchaseEvent(
        private val status: String? = null,
        private val productId: String? = null,
        private val origin: String,
        private val trailPeriod: String? = null
    ) : AnalyticsEvent(
        INAPP_PURCHASE,
        Bundle().apply {
            status?.let { putString("status", it) }
            productId?.let { putString("productId", it) }
            putString("origin", origin)
        }
    ) {
        fun toAppsFlyerPurchase(): AppsFlyerAnalyticsEvent.AfSubscribe? {
            return trailPeriod?.let {
                AppsFlyerAnalyticsEvent.AfSubscribe(
                    contentId = productId,
                    afOrigin = origin,
                    trailPeriod = it
                )
            }
        }
    }

    class SelectServer(
        private val server: String,
    ) : AnalyticsEvent(
        event = IMAGE_SAVED,
        args = Bundle().apply {
            putString("server", server)
        }
    )


    class AdDropDown(
        private val click: String,
        private val origin: String
    ) : AnalyticsEvent(
        event = ENHANCE_FEATURE_SELECTION,
        args = Bundle().apply {
            putString("clicked", click)
            putString("editingFeature", origin)
        }
    )

    class NavigationEvent(
        private val status: String,
        private val origin: String
    ) : AnalyticsEvent(
        event = ENHANCE_FEATURE_SELECTION,
        args = Bundle().apply {
            putString("status", status)
            putString("origin", origin)
        }
    )

    class RewardedAdEvent(
        private val status: String,
        private val origin: String
    ) : AnalyticsEvent(
        event = REWARDED_AD,
        args = Bundle().apply {
            putString("status", status)
            putString("origin", origin)
        }
    ) {
        fun toAppsFlyerAdRewardedEvent(): AppsFlyerAnalyticsEvent.AfAdViewEvent {
            return AppsFlyerAnalyticsEvent.AfAdViewEvent(
                adType = AnalyticsConstant.REWARDED,
                afAdOrigin = origin
            )
        }
    }


    class InterstitialAdEvent(
        private val status: String,
        private val origin: String
    ) : AnalyticsEvent(
        event = INTERSTITIAL_AD,
        args = Bundle().apply {
            putString("status", status)
            putString("origin", origin)
        }
    ) {
        fun toAppsFlyerAdInterstitialEvent(): AppsFlyerAnalyticsEvent.AfAdViewEvent {
            return AppsFlyerAnalyticsEvent.AfAdViewEvent(
                adType = AnalyticsConstant.INTERSTITIAL,
                afAdOrigin = origin
            )
        }
    }


}


