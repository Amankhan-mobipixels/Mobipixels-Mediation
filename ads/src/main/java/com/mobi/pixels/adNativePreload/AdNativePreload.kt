package com.mobi.pixels.adNativePreload

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.mobi.pixels.initialize.AdsMediation
import com.mobi.pixels.isOnline

object AdNativePreload {
    private lateinit var ctx: Activity
    private lateinit var id: String
    internal var preloadAd: NativeAd? = null
    private var isPreviousAdLoading = false

    fun load(
        context: Activity,
        nativeId: String,
        loadListener: AdNativePreloadListeners? = null
    ) {
        ctx = context
        id = nativeId

        if (!isOnline(ctx)) {
            loadListener?.onAdFailedToLoad("Internet connection not detected. Please check your connection and try again.")
            return
        }
        if (!AdsMediation.IsAdsAllowed) {
            loadListener?.onAdFailedToLoad("Ads disabled by developer")
            return
        }
        if (preloadAd != null) {
            loadListener?.onAdLoaded()
            return
        }

        if (!isPreviousAdLoading) {
            isPreviousAdLoading = true
            val adLoader = AdLoader.Builder(ctx, id)
                .forNativeAd { nativeAd -> preloadAd = nativeAd }
                .withAdListener(object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        loadListener?.onAdLoaded()
                        isPreviousAdLoading = false
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        loadListener?.onAdFailedToLoad(loadAdError.message)
                        isPreviousAdLoading = false
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        preloadAd = null
                    }
                })
                .build()

            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        } else loadListener?.onPreviousAdLoading()


    }
}
