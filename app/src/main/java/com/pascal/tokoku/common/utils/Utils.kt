package com.pascal.tokoku.common.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import com.pascal.tokoku.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showAlert(title: String, msg: String) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(msg)
        setIcon(R.drawable.ic_warning)
        setCancelable(false)

        setPositiveButton("Ok") { dialogInterface, i ->
            dialogInterface?.dismiss()
        }
    }.show()
}

fun getCurrentDate():String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy")
    return sdf.format(Date())
}

fun validation(txt: String): String {
    var result = ""
    if (txt != "null" && txt != null && txt != "") {
        result = txt
    } else {
        result = "-"
    }
    return result
}

fun isVisibility(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

fun formatTime(time: String): String {
    return if (time != "" && time != "-" && time != null) {
        val sdf = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.ENGLISH
        )
        val parsedDate: Date = sdf.parse(time)
        val date = SimpleDateFormat("dd-MM-yyyy")
        val result = date.format(parsedDate).toString()
        return result
    } else {
        return time
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

