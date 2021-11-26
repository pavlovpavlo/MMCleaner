package com.agento.mmcleaner.ui.splash

import android.content.Intent
import android.view.animation.Animation
import android.widget.ProgressBar
import android.widget.TextView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.PrivacyPolicyActivity
import com.agento.mmcleaner.ui.main.FirstMainActivity
import com.agento.mmcleaner.ui.main.SecondMainActivity
import com.agento.mmcleaner.util.ProgressBarAnimation
import com.agento.mmcleaner.util.shared.LocalSharedUtil

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    override fun onResume() {
        super.onResume()
        initViews()
    }

    override fun onBackPressed() {
    }

    private fun initViews() {
        val progressView = findViewById<ProgressBar>(R.id.progress)
        val privacyPolicy = findViewById<TextView>(R.id.privacy_policy)



        privacyPolicy.setOnClickListener {
            startActivity(Intent(applicationContext, PrivacyPolicyActivity::class.java))
        }

        val anim = ProgressBarAnimation(progressView, 0f, 100f)
        anim.duration = 4000
        progressView.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                startNextActivity()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun startNextActivity() {
        initAdsMain()
        if (LocalSharedUtil.isFirstMainShared(this)){
            startActivity(Intent(applicationContext, SecondMainActivity::class.java))}
        else
            startActivity(Intent(applicationContext, FirstMainActivity::class.java))
        finish()
    }
}