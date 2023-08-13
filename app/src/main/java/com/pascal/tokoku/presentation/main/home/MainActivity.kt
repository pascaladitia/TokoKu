package com.pascal.tokoku.presentation.main.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.PrefLogin
import com.pascal.tokoku.databinding.ActivityMainBinding
import com.pascal.tokoku.presentation.login.LoginActivity
import com.pascal.tokoku.presentation.main.mapStore.MapsStoreActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var prefLogin: PrefLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initView()
        getPermission()
        permissionLocation()
    }

    private fun initView() {
        prefLogin = PrefLogin()
        prefLogin?.initPref(this)

        binding.apply {
            btnKunjungan.setOnClickListener {
                startActivity(Intent(this@MainActivity, MapsStoreActivity::class.java))
            }

            btnLogout.setOnClickListener {
                prefLogin?.clearPref()

                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getLocationMode(): Int {
        try {
            return Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun permissionLocation() {
        if (getLocationMode() == 3) {
            Log.e("check location", getLocationMode().toString())
        } else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Setting GPS")
            alertDialog.setMessage("Mohon Set GPS ke Mode High Accuracy")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("OK") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            alertDialog.show()
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                    ), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                    ), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        permissionLocation()
                    }
                } else {
                    dialogPermission()
                }
                return
            }
        }
    }

    private fun dialogPermission() {
        AlertDialog.Builder(this).apply {
            setTitle("Permission Required!")
            setMessage("Kami butuh permission untuk menjalankan aplikasi, allow semua permission!")
            setIcon(R.drawable.loader_test)
            setCancelable(false)

            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface?.dismiss()
                finish()
            }
        }.show()
    }

}