package com.example.bskl_kotlin.fragment.contactus.adapter

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bskl_kotlin.fragment.contactus.model.ContactUsListModel
import com.example.bskl_kotlin.R

internal class ContactusAdapter(private var context: Context,
    private var aboutuslist: List<ContactUsListModel>
) : RecyclerView.Adapter<ContactusAdapter.MyviewHolder>() {


    internal inner class MyviewHolder(view: View) : RecyclerView.ViewHolder(view) {
       var name:TextView = view.findViewById(R.id.contactName)
        var phone_no:TextView = view.findViewById<TextView?>(R.id.cotactNumber)
        var email_contact:TextView = view.findViewById<TextView?>(R.id.cotactEmail)
        var cotactNumber:TextView = view.findViewById<TextView?>(R.id.cotactNumber)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactusAdapter.MyviewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_adapter_contact_us, parent, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkpermission()
        }
        return MyviewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return aboutuslist.size
    }

    override fun onBindViewHolder(holder: ContactusAdapter.MyviewHolder, position: Int) {
        val data = aboutuslist[position]
        holder.name.setText(data.name)
        if (!data.phone.equals("")) {
            holder.phone_no.setText(data.phone)
        } else {
            holder.phone_no.visibility = View.GONE
        }
        holder.email_contact.setText(data.email)
        if (!data.email.equals("")) {
            holder.email_contact.visibility = View.VISIBLE

        } else {
            holder.email_contact.visibility = View.GONE
        }

        holder.phone_no.setOnClickListener {

            val dialIntent = Intent(Intent.ACTION_CALL)
            dialIntent.data = Uri.parse("tel:" + data.phone)
            context.startActivity(dialIntent)

        }
        holder.email_contact.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SEND_MULTIPLE
            )
            val deliveryAddress =
                arrayOf(holder.email_contact.text.toString())
            emailIntent.putExtra(Intent.EXTRA_EMAIL, deliveryAddress)
            emailIntent.type = "text/plain"
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val pm: PackageManager = it.context.packageManager
            val activityList = pm.queryIntentActivities(
                emailIntent, 0
            )
            for (app in activityList) {
                if (app.activityInfo.name.contains("com.google.android.gm")) {
                    val activity = app.activityInfo
                    val name = ComponentName(
                        activity.applicationInfo.packageName, activity.name
                    )
                    emailIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    emailIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    emailIntent.component = name
                    it.context.startActivity(emailIntent)
                    break
                }
            }
        }

    }


    private fun checkpermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.CALL_PHONE
                ),
                123
            )
        }
    }

}
