package com.agento.mmcleaner.ui.clean.second_clean.adapters

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.agento.mmcleaner.R
import com.agento.mmcleaner.scan_util.model.JunkInfo
import com.agento.mmcleaner.util.UtilPhoneInfo
import java.io.File
import java.util.*


class RunProgramsAdapter(
    private var mList: List<JunkInfo>,
    private val activity: AppCompatActivity,
    private val listener: OnChangeProgramCheckedListener
) :
    RecyclerView.Adapter<RunProgramsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_program, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val programData = mList[position]

        if (position == 0) {
            holder.programImage.setImageResource(R.drawable.ic_category)
            holder.programName.text = "Choose all"
            holder.programSize.text = "${mList.size - 1} applications"
        } else {
            val pm: PackageManager = activity.packageManager

            val appsSizeFile = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
                File(
                    pm.getApplicationInfo(
                        programData.mPackageName,
                        PackageManager.GET_META_DATA
                    ).publicSourceDir
                ).length()
            }else{
                val x = 10485760L
                val y = 41943040L
                val r = Random()
                val number = x + (r.nextDouble() * (y - x)).toLong()
                number
            }

            val icon: Drawable = pm.getApplicationIcon(programData.mPackageName)
            val name = programData.name

            holder.programImage.setImageDrawable(icon)
            holder.programName.text = name
            holder.programSize.text = UtilPhoneInfo.toNormalFormat(appsSizeFile.toDouble())
        }

        holder.checkbox.isChecked = programData.isCheck
        holder.checkbox.setOnClickListener {
            listener.onChange(position)
        }

    }


    fun setData(list: List<JunkInfo>) {
        mList = list
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val programName: TextView = itemView.findViewById(R.id.program_name)
        val programSize: TextView = itemView.findViewById(R.id.program_size)
        val checkbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
        val programImage: ImageView = itemView.findViewById(R.id.program_image)
    }
}