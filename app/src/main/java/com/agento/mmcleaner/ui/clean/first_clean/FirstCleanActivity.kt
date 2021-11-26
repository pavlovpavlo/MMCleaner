package com.agento.mmcleaner.ui.clean.first_clean

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.agento.mmcleaner.R
import com.agento.mmcleaner.ui.BaseActivity
import com.agento.mmcleaner.ui.optimized.PhoneNoOptimizedActivity

class FirstCleanActivity : BaseActivity(R.layout.activity_first_clean) {

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_first_fragment)

        val prevFragId = navController.currentDestination!!.id
        when(prevFragId){
            R.id.fragment_first_optimization->{
            }

            R.id.fragment_first_scan_end->{
                startActivity(Intent(this, PhoneNoOptimizedActivity::class.java))
                finishAffinity()
            }

        }
    }

}