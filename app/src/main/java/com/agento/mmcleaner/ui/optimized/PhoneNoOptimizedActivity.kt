package com.agento.mmcleaner.ui.optimized

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.MainActivity
import com.agento.mmcleaner.ui.clean.first_clean.FirstCleanActivity
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity
import com.agento.mmcleaner.ui.clean.second_clean.SecondCleanActivity
import com.agento.mmcleaner.ui.clean.third_clean.ThirdCleanActivity
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.agento.mmcleaner.ui.setting.SettingActivity
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.agento.mmcleaner.util.shared.SharedData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import java.util.*

class PhoneNoOptimizedActivity : AppCompatActivity() {

    private lateinit var firstTab: LinearLayout
    private lateinit var secondTab: LinearLayout
    private lateinit var thirdTab: LinearLayout
    private var intentOptimization : Intent? = null
    private lateinit var mAdView: AdView

    override fun onBackPressed() {
        startActivity(Intent(this, SecondMainActivity::class.java))
        finishAffinity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_no_optimized)

        initViews()
        initAds()
    }


    private fun initAds(){
        mAdView = findViewById(R.id.adView)

        initializeBannerAd("ca-app-pub-3940256099942544~6300978111")

        loadBannerAd()
    }

    private fun initializeBannerAd(appUnitId: String) {

        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }
       // MobileAds.initialize(this, appUnitId)

    }

    private fun loadBannerAd() {

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun initViews(){
        firstTab = findViewById(R.id.first_clean_tab)
        secondTab = findViewById(R.id.second_clean_tab)
        thirdTab = findViewById(R.id.third_clean_tab)
        
        checkTabs()
    }

    private fun checkTabs(){
        if(LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_FIRST)){
            activeTabs(firstTab)
        }else{
            dismissTabs(firstTab)
            if(intentOptimization == null){
                intentOptimization = Intent(this, FirstScanActivity::class.java)
            }
            firstTab.setOnClickListener {
                startActivity(Intent(this, FirstScanActivity::class.java))
            }
        }

        if(LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_SECOND)){
            activeTabs(secondTab)
        }else{
            dismissTabs(secondTab)
            if(intentOptimization == null){
                intentOptimization = Intent(this, SecondCleanActivity::class.java)
            }
            secondTab.setOnClickListener {
                startActivity(Intent(this, SecondCleanActivity::class.java))
            }
        }

        if(LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_THIRD)){
            activeTabs(thirdTab)
        }else{
            dismissTabs(thirdTab)
            if(intentOptimization == null){
                intentOptimization = Intent(this, ThirdCleanActivity::class.java)
            }
            thirdTab.setOnClickListener {
                startActivity(Intent(this, ThirdCleanActivity::class.java))
            }
        }
    }

    private fun activeTabs(tab: LinearLayout){
        tab.setBackgroundResource(R.drawable.ic_phone_nooptim_active)
        (tab.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.color_28BB63))
        (tab.getChildAt(1) as ImageView).setImageResource(R.drawable.ic_checkbox_on)
    }

    private fun dismissTabs(tab: LinearLayout){
        tab.setBackgroundResource(R.drawable.ic_phone_nooptim_noactive)
        (tab.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.white))
        (tab.getChildAt(1) as ImageView).setImageResource(R.drawable.ic_checkbox_off)
    }
    
}