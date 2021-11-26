package com.agento.mmcleaner.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

open class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    private var mInterstitialAd: InterstitialAd? = null
    var isCheckOpen = false
    private lateinit var listenerUsageAccess: OnPermissionUsageListener

    public interface OnPermissionUsageListener{
        fun onPermissionAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onRestart() {
        super.onRestart()
        if(isCheckOpen && listenerUsageAccess!=null)
            listenerUsageAccess.onPermissionAction()
    }

    fun checkPermissionUsage(listener: OnPermissionUsageListener){
        listenerUsageAccess = listener
        val containerAccess = findViewById<LinearLayout>(R.id.container_access)
        val allowBtn = findViewById<AppCompatButton>(R.id.allow_btn)
        val disallowBtn = findViewById<AppCompatButton>(R.id.disallow_btn)
        containerAccess.visibility = View.VISIBLE
        allowBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            containerAccess.visibility = View.GONE
            //isCheckOpen = false
        }

        disallowBtn.setOnClickListener {
            containerAccess.visibility = View.GONE
            listener.onPermissionAction()
            isCheckOpen = false
        }
        isCheckOpen = true

    }

    fun hideCheck(){
        val containerAccess = findViewById<LinearLayout>(R.id.container_access)
        containerAccess.visibility = View.GONE
        isCheckOpen = false
    }


    open fun initAdsMain() {
           MobileAds.initialize(
                this
            ) { initializationStatus: InitializationStatus? -> }

            val adRequest = AdRequest.Builder().build()
            //new AdRequest.Builder().setTestDeviceIds(Arrays.asList("FA81CB082100B884FF4842AB874D0938"));
            InterstitialAd.load(
                this,
               "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd
                        showAds()
                        Log.i("TAG", "onAdLoaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.i("ERROR_ADS", loadAdError.message)
                        mInterstitialAd = null
                    }
                })

    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this@BaseActivity)
            mInterstitialAd!!.setFullScreenContentCallback(object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null
                    Log.d("TAG", "The ad was shown.")
                }
            })
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }


}