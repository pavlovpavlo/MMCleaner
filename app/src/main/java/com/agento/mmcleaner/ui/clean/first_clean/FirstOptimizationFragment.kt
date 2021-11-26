package com.agento.mmcleaner.ui.clean.first_clean

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.*
import com.agento.mmcleaner.scan_util.callback.IScanCallback
import com.agento.mmcleaner.scan_util.model.JunkGroup
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.ui.BaseFragment
import com.agento.mmcleaner.ui.clean.first_clean.adapters.JunkStepsAdapter
import com.agento.mmcleaner.ui.clean.first_clean.adapters.OnChangeStepCheckedListener
import com.agento.mmcleaner.ui.clean.second_clean.SecondOptimizationEndActivity
import com.agento.mmcleaner.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class FirstOptimizationFragment : BaseFragment(R.layout.fragment_first_optimization) {

    lateinit var loader: ImageView
    lateinit var titleText: TextView
    private lateinit var unncessary: TextView
    private lateinit var unncessaryType: TextView
    lateinit var clearBtn: AppCompatButton
    lateinit var list: RecyclerView
    lateinit var thisView: View
    lateinit var adapter: JunkStepsAdapter
    lateinit var loaderAnimation: Animation
    var processes: MutableList<JunkGroup> = mutableListOf()
    var isCacheScanning = false
    var isAdvertisingScanning = false
    var isTemporaryScanning = false
    var isApkScanning = false
    var allSize = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisView = view
        allSize = requireArguments().getDouble("unncessary")
        initViews()
    }

    private fun initViews() {
        loader = thisView.findViewById(R.id.loader_optimization)
        list = thisView.findViewById(R.id.process_recycler)
        titleText = thisView.findViewById(R.id.title_text)
        clearBtn = thisView.findViewById(R.id.clear_btn)
        unncessary = thisView.findViewById(R.id.unncessary)
        unncessaryType = thisView.findViewById(R.id.unncessary_type)

        val unncessaryCount = UtilPhoneInfo.toNormalFormat(allSize)
        unncessary.text = unncessaryCount.substring(0, unncessaryCount.indexOf(" "))
        unncessaryType.text = unncessaryCount.substring(unncessaryCount.indexOf(" ") + 1)
        clearBtn.setOnClickListener { deleteAllItems() }

        startAnimation()
        initList()

        if (!UtilPermissions.isPermissionDenied(requireActivity() as AppCompatActivity, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                imitateList()
            }else{
                scanCacheApp()
                scanBadFiles()
            }
        } else {
            imitateList()
        }
    }

    private fun imitateList() {
        val randomInstallApps = UStats.getRandomJunkApps(requireContext())
        MainScope().launch {
            delay(700)
            generateEmptyAPK()
            delay(900)
            generateEmptyTemporary()
            delay(1500)
            generateEmptyCache(randomInstallApps as ArrayList<JunkInfo>)
            delay(1200)
            generateAdvertising(randomInstallApps)
            checkScanning()
        }
    }

    private fun generateEmptyCache(apps: ArrayList<JunkInfo>) {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_CACHE
        cacheGroup.mChildren = apps
        for (info in cacheGroup.mChildren) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)
        isCacheScanning = true
    }

    private fun generateEmptyTemporary() {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_TEMPORARY_FILES
        cacheGroup.mChildren = arrayListOf()
        processes.add(cacheGroup)
        adapter.setData(processes)
        isTemporaryScanning = true
    }

    private fun generateEmptyAPK() {
        val cacheGroup =
            JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_APK
        cacheGroup.mChildren = arrayListOf()
        processes.add(cacheGroup)
        adapter.setData(processes)
        isApkScanning = true
    }

    private fun initList() {
        list.layoutManager = LinearLayoutManager(requireContext())
        adapter = JunkStepsAdapter(processes, object : OnChangeStepCheckedListener {
            override fun onChange(positionProgram: Int, positionStep: Int) {
                processes[positionStep].mChildren[positionProgram].isCheck =
                    !processes[positionStep].mChildren[positionProgram].isCheck
            }

            override fun onChangeStep(positionStep: Int) {
                processes[positionStep].isCheck = !processes[positionStep].isCheck

                for (i in 0 until processes[positionStep].mChildren.size)
                    processes[positionStep].mChildren[i].isCheck =
                        processes[positionStep].isCheck

                list.post { adapter.setData(processes) }
            }

        })
        list.adapter = adapter
    }

    private fun startAnimation() {
        loaderAnimation =
            AnimationUtils.loadAnimation(context, R.anim.animation_loader)
        loader.animation = loaderAnimation
    }

    private fun stopAnimation() {
        loader.clearAnimation()
        loaderAnimation.cancel()
        loaderAnimation.reset()
    }

    private fun checkScanning() {
        if (isAdvertisingScanning && isApkScanning && isCacheScanning && isTemporaryScanning) {
            clearBtn.visibility = View.VISIBLE
            stopAnimation()
            titleText.text = "Done..."
            loader.visibility = View.INVISIBLE
        }
    }

    private fun scanCacheApp() {
        val sysCacheScanTask = SysCacheScanTask(object :
            IScanCallback {
            override fun onBegin() {

            }

            override fun onProgress(info: JunkInfo?) {

            }

            override fun onFinish(children: ArrayList<JunkInfo>) {
                MainScope().launch {
                    val cacheGroup =
                        JunkGroup()
                    cacheGroup.mType = JunkGroup.GROUP_CACHE
                    cacheGroup.mChildren.addAll(
                        UStats.filter(
                            children[0].mChildren,
                            requireContext()
                        )
                    )
                    cacheGroup.mChildren.sort()
                    cacheGroup.mChildren.reverse()
                    for (info in cacheGroup.mChildren) {
                        cacheGroup.mSize += info.mSize
                    }
                    processes.add(cacheGroup)
                    adapter.setData(processes)
                    generateAdvertising(cacheGroup.mChildren)
                    isCacheScanning = true
                    checkScanning()
                }

            }
        })
        sysCacheScanTask.execute()
    }

    private fun generateAdvertising(apps: ArrayList<JunkInfo>) {
        var advertisingList = mutableListOf<JunkInfo>()
        for (i in 0..(0..apps.size / 2).random()) {
            val program = apps.random()
            program.mSize = program.mSize / 4
            advertisingList.add(program)
        }

        advertisingList = ArrayList(HashSet(advertisingList))

        val cacheGroup = JunkGroup()
        cacheGroup.mType = JunkGroup.GROUP_ADVERTISING
        cacheGroup.mChildren.addAll(advertisingList)
        cacheGroup.mChildren.sort()
        cacheGroup.mChildren.reverse()
        for (info in advertisingList) {
            cacheGroup.mSize += info.mSize
        }
        processes.add(cacheGroup)
        adapter.setData(processes)

        isAdvertisingScanning = true
    }

    private fun scanBadFiles() {
        val overallScanTask = OverallScanTask(object :
            IScanCallback {
            override fun onBegin() {
            }

            override fun onProgress(info: JunkInfo?) {
            }

            override fun onFinish(children: ArrayList<JunkInfo>) {

                MainScope().launch {
                    var map: MutableMap<Int, JunkInfo> = mutableMapOf()
                    map[JunkGroup.GROUP_APK] = JunkInfo()
                    map[JunkGroup.GROUP_TEMPORARY_FILES] = JunkInfo()

                    for (info in children) {
                        val path: String = info.mChildren.get(0).mPath
                        var groupFlag = 0
                        isApkScanning = true
                        isTemporaryScanning = true
                        if (path.endsWith(".apk")) {
                            groupFlag = JunkGroup.GROUP_APK
                        } else if (path.endsWith(".log") || path.endsWith(".tmp") || path.endsWith(".temp")) {
                            groupFlag = JunkGroup.GROUP_TEMPORARY_FILES
                        }

                        map[groupFlag] = info

                        checkScanning()
                    }

                    for ((key, value) in map) {
                        val cacheGroup =
                            JunkGroup()
                        cacheGroup.mType = key
                        cacheGroup.mChildren.addAll(
                            UStats.filter(
                                value.mChildren,
                                requireContext()
                            )
                        )
                        cacheGroup.mSize = value.mSize
                        processes.add(cacheGroup)
                        adapter.setData(processes)
                    }
                }
            }
        })
        overallScanTask.execute()
    }

    private fun deleteItem(rowView: View, position: Int) {
        val anim = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.slide_out_right
        )
        anim.duration = 500
        rowView.startAnimation(anim)
        Handler().postDelayed(Runnable {
            processes.removeAt(position) //Remove the current content from the array
            adapter.notifyDataSetChanged() //Refresh list
        }, anim.duration)
    }

    var mStopHandler = false

    private fun deleteAllItems() {
        titleText.text = "Cleaning ..."
        MainScope().launch(context = Dispatchers.Main) {
            if (!UtilPermissions.isPermissionDenied(
                    requireActivity() as AppCompatActivity,
                    false
                ) && Build.VERSION.SDK_INT < Build.VERSION_CODES.R
            ) {
                clearJunk()
            }
            for (i in 0 until processes.size) {

                try {
                    val v: View = list.findViewHolderForAdapterPosition(
                        (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    )!!.itemView
                    deleteItem(
                        v,
                        (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    )
                    if (i != (processes.size - 1))
                        delay(1050)
                }catch (e:Exception){}

            }
            optimizationComplete()
        }
    }

    private fun clearJunk() {
        val allProcess : MutableList<JunkGroup> = mutableListOf()
        allProcess.addAll(processes)
        val clearThread = Thread {

            for (i in 0 until allProcess.size) {
                if(allProcess[i].isCheck) {
                    if (allProcess[i].mType == JunkGroup.GROUP_APK) {
                        val junks =
                            allProcess[i].mChildren.filter { junkInfo -> junkInfo.isCheck } as ArrayList<JunkInfo>

                        CleanUtil().freeAllAppsCache(junks)
                    }
                    if (allProcess[i].mType == JunkGroup.GROUP_TEMPORARY_FILES || allProcess[i].mType == JunkGroup.GROUP_APK) {
                        val junks =
                            allProcess[i].mChildren.filter { junkInfo -> junkInfo.isCheck } as ArrayList<JunkInfo>

                        CleanUtil().freeJunkInfos(junks)
                    }
                }
            }
        }
        clearThread.start()
    }

    private fun optimizationComplete() {
        startAds();
        val intent = Intent(requireContext(), FirstOptimizationEndActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        requireActivity().finish()
    }

}