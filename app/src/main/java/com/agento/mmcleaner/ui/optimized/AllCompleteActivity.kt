package com.agento.mmcleaner.ui.optimized

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus

class AllCompleteActivity : AppCompatActivity() {
    lateinit var toMainBtn: AppCompatButton
    lateinit var stars: Array<ImageView>
    private lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_complete)
        initViews()
    }

    private fun initViews(){
        stars = arrayOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
        toMainBtn = findViewById(R.id.to_main)

        for(i in stars.indices){
            stars[i].setOnClickListener {
                setStars(i+1)
                if (i == 4){
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                    }
                }else{
                    val selectorIntent = Intent(Intent.ACTION_SENDTO)
                    selectorIntent.data = Uri.parse("mailto:")

                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@agento.pro"))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My review for MM Cleaner app")
                    emailIntent.selector = selectorIntent

                    startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Send email..."
                        )
                    )
                }
            }
        }

        toMainBtn.setOnClickListener {
            finish()
        }
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

    private fun setStars(countSelectStars: Int){
        for(i in 0 until countSelectStars){
            stars[i].setImageResource(R.drawable.ic_star_active)
        }

        for(i in (countSelectStars) until stars.size){
            stars[i].setImageResource(R.drawable.ic_star_noactive)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}