package com.agento.mmcleaner.ui.clean.second_clean

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import cn.septenary.ui.widget.GradientProgressBar
import com.agento.mmcleaner.R
import com.agento.mmcleaner.util.UStats
import com.agento.mmcleaner.util.Util
import com.agento.mmcleaner.util.UtilPhoneInfo


class SecondScanFragment : Fragment(R.layout.fragment_second_scan) {

    lateinit var thisView: View
    lateinit var procText: TextView
    lateinit var memData: TextView
    lateinit var bar: GradientProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        initViews()
    }

    private fun initViews(){
        bar = thisView.findViewById(R.id.bar2)
        procText = thisView.findViewById(R.id.proc_text)
        memData = thisView.findViewById(R.id.mem_data)

        val procentUse = (50..90).random()
        SecondScanEndFragment.procentUse = procentUse
        val totalRam = UtilPhoneInfo.getTotalRAM()
        val totalRamDigit = totalRam.substring(0, totalRam.indexOf(" ")).toLong()
        procText.text = "${procentUse}% employed"
        memData.text = "${(totalRamDigit.toDouble() *procentUse.toDouble())/100.0} GB / ${UtilPhoneInfo.getTotalRAM()}"

        bar.setProgress(procentUse, true)

        if (UStats.getUsageStatsList(requireContext(),true).isEmpty()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }else{
            bar.animate().rotation(1080f).setListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    openNextScreen()
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }

            }).setDuration(4000L).start()
        }
    }

    private fun openNextScreen(){
        val navBuilder = NavOptions.Builder()
        val controller = NavHostFragment.findNavController(this@SecondScanFragment)
        controller.navigate(R.id.fragment_second_scan_end, null, Util.generateNavOptions())
    }
}