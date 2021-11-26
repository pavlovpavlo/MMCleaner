package com.agento.mmcleaner.ui.clean.first_clean

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.agento.mmcleaner.R
import com.agento.mmcleaner.util.MemStat
import com.agento.mmcleaner.util.Util
import com.agento.mmcleaner.util.UtilPermissions
import com.agento.mmcleaner.util.UtilPhoneInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class FirstScanEndFragment : Fragment(R.layout.fragment_first_scan_end) {

    private lateinit var btnClear:AppCompatButton
    private lateinit var ramUsed:TextView
    private lateinit var cpuTemp:TextView
    private lateinit var batteryCharge:TextView
    private lateinit var freeMemory:TextView
    private lateinit var appBackground:TextView
    private lateinit var consumApp:TextView
    private lateinit var unncessary:TextView
    private lateinit var thisView: View
    private lateinit var mAdView: AdView
    var allSize = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        initViews()
        initListeners()
    }

    private fun initViews(){
        btnClear = thisView.findViewById(R.id.clear_btn)
        ramUsed = thisView.findViewById(R.id.ram_procent)
        cpuTemp = thisView.findViewById(R.id.cpu_temp)
        batteryCharge = thisView.findViewById(R.id.battery_charge)
        freeMemory = thisView.findViewById(R.id.free_memory)
        appBackground = thisView.findViewById(R.id.app_in_background)
        consumApp = thisView.findViewById(R.id.consuming_app)
        unncessary = thisView.findViewById(R.id.unncessary)

        val random = ThreadLocalRandom.current().nextDouble(1000.toDouble(), 2500.toDouble())

        allSize = random * 1024F * 1024F
        unncessary.text = UtilPhoneInfo.toNormalFormat(allSize,"#.#")
        ramUsed.text = "${(50..90).random()}%"
        cpuTemp.text = "${Util.cpuTemperature()} C"
        batteryCharge.text = "${Util.getBatteryPercentage(requireContext())}%"
        freeMemory.text = "${MemStat(requireContext()).procentMemory}%"
        appBackground.text = "${(5..30).random()} app"
        consumApp.text = "${(2..15).random()} app"
        initAds()
    }
    private fun initAds(){
        mAdView = thisView.findViewById(R.id.adView)

        initializeBannerAd("ca-app-pub-3940256099942544~6300978111")

        loadBannerAd()
    }

    private fun initializeBannerAd(appUnitId: String) {

        MobileAds.initialize(
            activity
        ) { initializationStatus: InitializationStatus? -> }

      //  MobileAds.initialize(requireContext(), appUnitId)

    }

    private fun loadBannerAd() {

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun initListeners(){
        btnClear.setOnClickListener {
            if (!UtilPermissions.isPermissionDenied(requireActivity() as AppCompatActivity, true)) {
                openNextStep()
            }
            btnClear.setOnClickListener {
                openNextStep()
            }
        }
    }

    private fun openNextStep(){
        val controller = NavHostFragment.findNavController(this@FirstScanEndFragment)
        val bundle = Bundle()
        bundle.putDouble("unncessary", allSize)
        controller.navigate(R.id.fragment_first_optimization, bundle, Util.generateNavOptions())
    }
}