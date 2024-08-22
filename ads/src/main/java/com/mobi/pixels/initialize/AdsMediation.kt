package com.mobi.pixels.initialize

import android.app.Activity
import android.util.Log
import com.applovin.sdk.AppLovinPrivacySettings
import com.google.android.gms.ads.MobileAds
import com.ironsource.mediationsdk.IronSource
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.unity3d.ads.metadata.MetaData
import com.vungle.ads.VunglePrivacySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdsMediation {
    internal var IsAdsAllowed   = true
    fun initialize(ctx: Activity, isAdsAllowed:Boolean? = null){
        if (isAdsAllowed != null) {
            IsAdsAllowed  = isAdsAllowed
        }

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {

            AppLovinPrivacySettings.setHasUserConsent(true, ctx)
            AppLovinPrivacySettings.setIsAgeRestrictedUser(true, ctx)
            AppLovinPrivacySettings.setDoNotSell(true, ctx)

            VunglePrivacySettings.setGDPRStatus(true, "v1.0.0")
            VunglePrivacySettings.setCCPAStatus(true)

            var sdk = MBridgeSDKFactory.getMBridgeSDK()
            sdk.setConsentStatus(ctx, MBridgeConstans.IS_SWITCH_ON)
            sdk.setDoNotTrackStatus(false)

            val gdprMetaData = MetaData(ctx)
            gdprMetaData["gdpr.consent"] = true
            gdprMetaData.commit()

            val ccpaMetaData = MetaData(ctx)
            ccpaMetaData["privacy.consent"] = true
            ccpaMetaData.commit()

            IronSource.setConsent(true)
            IronSource.setMetaData("do_not_sell", "true")

            MobileAds.initialize(ctx) { initializationStatus ->
                val statusMap = initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d(
                        "fjnskd47fbhjd", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status!!.description, status.latency
                        )
                    )
                }
                // Start loading ads here...
            }
        }

    }
}