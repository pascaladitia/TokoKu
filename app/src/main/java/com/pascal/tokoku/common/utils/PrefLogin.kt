package com.pascal.tokoku.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class PrefLogin {

    private var pref: SharedPreferences? = null

    fun initPref(context: Context) {
        pref = context.getSharedPreferences(Constant.LOGIN, AppCompatActivity.MODE_PRIVATE)
    }

    fun saveLogin(username: String, rememberMe: Boolean) {
        val edit = pref?.edit()
        edit?.clear()
        edit?.putString("username", username)
        edit?.putBoolean("rememberMe", rememberMe)
        edit?.apply()
    }

    fun getUsername(): String {
        return pref?.getString("username", "").toString()
    }

    fun rememberMe(): Boolean? {
        return pref?.getBoolean("rememberMe", false)
    }

    fun clearPref() {
        val edit = pref?.edit()
        edit?.clear()
        edit?.apply()
    }
}