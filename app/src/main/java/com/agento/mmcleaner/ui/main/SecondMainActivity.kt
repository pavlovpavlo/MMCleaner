package com.agento.mmcleaner.ui.main

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.MainActivity
import com.agento.mmcleaner.ui.clean.first_clean.FirstCleanActivity
import com.agento.mmcleaner.ui.clean.first_clean.FirstScanActivity
import com.agento.mmcleaner.ui.clean.second_clean.SecondCleanActivity
import com.agento.mmcleaner.ui.clean.third_clean.ThirdCleanActivity
import com.agento.mmcleaner.ui.setting.SettingActivity
import com.agento.mmcleaner.ui.thanks.ThanksActivity
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.UtilPermissions
import com.agento.mmcleaner.util.shared.LocalSharedUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.material.snackbar.Snackbar

class SecondMainActivity : BaseActivity(R.layout.activity_second_main) {

    private lateinit var firstTab: ConstraintLayout
    private lateinit var secondTab: ConstraintLayout
    private lateinit var thirdTab: ConstraintLayout
    private lateinit var quitDialog: View
    private lateinit var exitOptim: View
    private lateinit var quitText: TextView
    private lateinit var quitExit: TextView
    private lateinit var quitClear: AppCompatButton
    private lateinit var firstMainDescription: TextView
    private lateinit var scanBtn: ImageButton
    private lateinit var scanBtnBorder: ImageView
    private var countOptimized = 0
    private var intentOptimization: Intent? = null
    var doubleBackToExitPressedOnce: Boolean = false
    lateinit var loaderAnimation: Animation
    private lateinit var mAdView: AdView

    override fun onBackPressed() {
        if(isCheckOpen){
            hideCheck()
        }else {
            if (countOptimized == 3) {
                if (doubleBackToExitPressedOnce) {
                    startActivity(Intent(this, ThanksActivity::class.java))
                    finishAffinity()
                    return
                }
                doubleBackToExitPressedOnce = true
                Snackbar.make(findViewById(R.id.parent), "Click again to exit", Snackbar.LENGTH_SHORT).show()
                Handler().postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2500)
            } else {
                quitDialog.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
    }

    private fun initViews() {
        val settingBtn: ImageView = findViewById(R.id.setting_btn)
        firstTab = findViewById(R.id.first_clean_tab)
        secondTab = findViewById(R.id.second_clean_tab)
        thirdTab = findViewById(R.id.third_clean_tab)
        exitOptim = findViewById(R.id.exit_optim)
        quitDialog = findViewById(R.id.quite)
        quitText = findViewById(R.id.quit_text)
        quitExit = findViewById(R.id.exit_btn)
        quitClear = findViewById(R.id.clear_btn)
        firstMainDescription = findViewById(R.id.first_main_description)
        scanBtn = findViewById(R.id.scan_btn)
        scanBtnBorder = findViewById(R.id.scan_btn_border)

        settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        quitExit.setOnClickListener {
            quitDialog.visibility = View.GONE
            finishAffinity()
        }

        quitClear.setOnClickListener {
            quitDialog.visibility = View.GONE
            startActivity(intentOptimization)
        }
        checkTabs()
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
      //  MobileAds.initialize(this, appUnitId)

    }

    private fun loadBannerAd() {

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun checkTabs() {
        if (LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_FIRST)) {
            activeTabs(firstTab)
            countOptimized++
            firstTab.setOnClickListener {
                Snackbar.make(findViewById(R.id.parent), "This operation has already been performed", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            dismissTabs(firstTab)
            if (intentOptimization == null) {
                quitText.text = "Clear cache and temporary files to improve phone performance!"
                quitClear.text = "Clear"
                intentOptimization = Intent(this, FirstScanActivity::class.java)
            }
            firstTab.setOnClickListener {
                if (!UtilPermissions.isPermissionDenied(this, true)) {
                    startActivity(Intent(this, FirstScanActivity::class.java))
                }
                firstTab.setOnClickListener {
                    startActivity(Intent(this, FirstScanActivity::class.java))
                }
            }
        }

        if (LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_SECOND)) {
            activeTabs(secondTab)
            countOptimized++
            secondTab.setOnClickListener {
                Snackbar.make(findViewById(R.id.parent), "This operation has already been performed", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            dismissTabs(secondTab)
            if (intentOptimization == null) {
                quitText.text = "Free up memory and speed up your phone!"
                quitClear.text = "Speed up"
                intentOptimization = Intent(this, SecondCleanActivity::class.java)
            }
            secondTab.setOnClickListener {
                if (UStats.getUsageStatsList(this, false).isEmpty()) {
                    checkPermissionUsage(object: OnPermissionUsageListener{
                        override fun onPermissionAction() {
                            startActivity(Intent(this@SecondMainActivity, SecondCleanActivity::class.java))
                        }

                    })
                } else {
                    startActivity(Intent(this, SecondCleanActivity::class.java))
                }
                secondTab.setOnClickListener {
                    startActivity(Intent(this, SecondCleanActivity::class.java))
                }
            }
        }

        if (LocalSharedUtil.isStepOptimized(this, LocalSharedUtil.SHARED_THIRD)) {
            activeTabs(thirdTab)
            countOptimized++
            thirdTab.setOnClickListener {
                Snackbar.make(findViewById(R.id.parent), "This operation has already been performed", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            dismissTabs(thirdTab)
            if (intentOptimization == null) {
                quitText.text =
                    "Hibernate apps that consume a lot of power and optimize battery consumption!"
                quitClear.text = "Optimization"
                intentOptimization = Intent(this, ThirdCleanActivity::class.java)
            }
            thirdTab.setOnClickListener {
                if (UStats.getUsageStatsList(this, false).isEmpty()) {
                    checkPermissionUsage(object: OnPermissionUsageListener{
                        override fun onPermissionAction() {
                            startActivity(Intent(this@SecondMainActivity, ThirdCleanActivity::class.java))
                        }

                    })
                } else {
                    startActivity(Intent(this, ThirdCleanActivity::class.java))
                }
                thirdTab.setOnClickListener {
                    startActivity(Intent(this, ThirdCleanActivity::class.java))
                }
            }
        }

        if (countOptimized == 3) {
            firstMainDescription.text = "Your phone has been optimized!"
            scanBtn.setImageResource(R.drawable.ic_scan_pasive)
            scanBtnBorder.visibility = View.GONE
        } else {
            firstMainDescription.text = "${3 - countOptimized} of 3 operation failed!"
            scanBtn.setImageResource(R.drawable.ic_scan_main)
            scanBtn.setOnClickListener {
                if (intentOptimization != null) {
                    startActivity(intentOptimization)
                }
            }
            scanBtnBorder.visibility = View.VISIBLE
            loaderAnimation =
                AnimationUtils.loadAnimation(this, R.anim.animation_button_circle)
            scanBtnBorder.animation = loaderAnimation
        }
    }

    private fun activeTabs(tab: ConstraintLayout) {
        tab.setBackgroundResource(R.drawable.ic_tab_main_active)
        (tab.getChildAt(1) as TextView).setTextColor(resources.getColor(R.color.color_333A44))
        (tab.getChildAt(0) as ImageView).setColorFilter(Color.parseColor("#C4C4C4"))
        (tab.getChildAt(2) as ImageView).visibility = View.VISIBLE
    }

    private fun dismissTabs(tab: ConstraintLayout) {
        tab.setBackgroundResource(R.drawable.ic_tab_main_noactive)
        (tab.getChildAt(1) as TextView).setTextColor(resources.getColor(R.color.white))
        (tab.getChildAt(0) as ImageView).setColorFilter(resources.getColor(R.color.white))
        (tab.getChildAt(2) as ImageView).visibility = View.INVISIBLE
    }
}