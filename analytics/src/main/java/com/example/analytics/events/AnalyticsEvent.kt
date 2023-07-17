package com.example.analytics.events

import android.os.Bundle
import com.example.analytics.utils.AnalyticsConstant
import com.example.analytics.utils.AnalyticsConstant.APP_OPEN_AD
import com.example.analytics.utils.AnalyticsConstant.APP_USERS
import com.example.analytics.utils.AnalyticsConstant.BANNER_AD
import com.example.analytics.utils.AnalyticsConstant.BTN_DOWNLOAD
import com.example.analytics.utils.AnalyticsConstant.CLONE_STAMP_FEATURE
import com.example.analytics.utils.AnalyticsConstant.EDITING_FEATURE
import com.example.analytics.utils.AnalyticsConstant.EDITOR_BEFORE_AFTER
import com.example.analytics.utils.AnalyticsConstant.EDITOR_DESELECT
import com.example.analytics.utils.AnalyticsConstant.EDITOR_HOME_ICON
import com.example.analytics.utils.AnalyticsConstant.EDITOR_REDO
import com.example.analytics.utils.AnalyticsConstant.EDITOR_SAVE
import com.example.analytics.utils.AnalyticsConstant.EDITOR_SELECT
import com.example.analytics.utils.AnalyticsConstant.EDITOR_TAP
import com.example.analytics.utils.AnalyticsConstant.EDITOR_UNDO
import com.example.analytics.utils.AnalyticsConstant.ENHANCE_FEATURE_SELECTION
import com.example.analytics.utils.AnalyticsConstant.ENHANCE_TUTORIAL
import com.example.analytics.utils.AnalyticsConstant.GALLERY_CAMERA
import com.example.analytics.utils.AnalyticsConstant.GALLERY_IMAGE_SELECTION
import com.example.analytics.utils.AnalyticsConstant.HOME_EDIT
import com.example.analytics.utils.AnalyticsConstant.HOME_GET_FREE_TRIAL
import com.example.analytics.utils.AnalyticsConstant.HOME_PREMIUM_ICON
import com.example.analytics.utils.AnalyticsConstant.HOME_SETTINGS
import com.example.analytics.utils.AnalyticsConstant.IMAGE_SAVED
import com.example.analytics.utils.AnalyticsConstant.INAPP_PURCHASE
import com.example.analytics.utils.AnalyticsConstant.INAPP_YEARLY_BUTTON_EVENT
import com.example.analytics.utils.AnalyticsConstant.INTERSTITIAL_AD
import com.example.analytics.utils.AnalyticsConstant.LANGUAGE_SELECTED
import com.example.analytics.utils.AnalyticsConstant.LINK_TO_SEARCH
import com.example.analytics.utils.AnalyticsConstant.MEDIA_PERMISSION
import com.example.analytics.utils.AnalyticsConstant.RATING
import com.example.analytics.utils.AnalyticsConstant.RATING_CLOSE
import com.example.analytics.utils.AnalyticsConstant.RATING_PLAY_STORE
import com.example.analytics.utils.AnalyticsConstant.RATING_STAR
import com.example.analytics.utils.AnalyticsConstant.REMOVE_WM_PRO
import com.example.analytics.utils.AnalyticsConstant.REWARDED_AD
import com.example.analytics.utils.AnalyticsConstant.REW_AD_REMOVE_WM
import com.example.analytics.utils.AnalyticsConstant.REW_AD_SAVE_IN_HD
import com.example.analytics.utils.AnalyticsConstant.SAVE_BS_CLOSE
import com.example.analytics.utils.AnalyticsConstant.SAVE_BS_PDF
import com.example.analytics.utils.AnalyticsConstant.SAVE_BS_PNG
import com.example.analytics.utils.AnalyticsConstant.SAVE_BS_SHARE
import com.example.analytics.utils.AnalyticsConstant.SAVE_DIALOG_SAVE
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_7_DAYS_TRIAL
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_CONTACT_US
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_FACEBOOK
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_FEEDBACK
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_INSTAGRAM
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_MANAGE_SUBSCRIPTION
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_RATE_US
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_REMOVE_ADS
import com.example.analytics.utils.AnalyticsConstant.SETTINGS_TERMS_CONDITION

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
    ): AnalyticsEvent(
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


