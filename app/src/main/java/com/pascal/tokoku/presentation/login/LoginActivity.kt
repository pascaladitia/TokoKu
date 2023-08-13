package com.pascal.tokoku.presentation.login

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.common.utils.PrefLogin
import com.pascal.tokoku.common.utils.showAlert
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.network.model.ResponseLogin
import com.pascal.tokoku.databinding.ActivityLoginBinding
import com.pascal.tokoku.presentation.main.home.MainActivity
import com.pascal.tokoku.presentation.main.viewModel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private var prefLogin: PrefLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        onView()
        onClick()
        getLocationMode()
        getPermission()
        observeData()
    }

    private fun onView() {
        prefLogin = PrefLogin()
        prefLogin?.initPref(this)
        
    }

    private fun onClick() {
        with(binding) {
            btnLogin.setOnClickListener {
                validation()
            }
        }

    }

    private fun validation() {
        with(binding) {
            if (etUsername.text.isEmpty()) {
                etUsername.error = Constant.FORM_EMPTY
            } else if (etPassword.text.isEmpty()) {
                etPassword.error = Constant.FORM_EMPTY
            } else {
                actionLogin()
            }

        }
    }

    private fun actionLogin() {
        val builder: MultipartBody.Builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        builder
            .addFormDataPart("username", binding.etUsername.text.toString())
            .addFormDataPart("password", binding.etPassword.text.toString())
            .build()
        viewModel.login(builder.build())
    }

    private fun observeData() {
        lifecycleScope.apply {
            launchWhenStarted {
                viewModel.loginResult.collect {
                    when (it) {
                        is Resource.Empty -> {}
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showRespone(it.data)
                            Log.d("TAG", "observeData: ${it.data}")
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showAlert(Constant.APP_NAME, it.message.toString())
                            Log.d("TAG", "observeData: ${it.exception} or ${it.message}")
                        }
                    }
                }
            }

            storeViewModel.insertStoreResult.asLiveData().observe(this@LoginActivity) {
                if (it is Resource.Success) {
                    Log.d("TAG", "success: ${it.data}")
                } else if (it is Resource.Error) {
                    Log.d("TAG", "observeData: ${it.exception}")
                }
            }
        }
    }

    private fun showRespone(data: ResponseLogin?) {
        if (data?.status == Constant.SUCCESS) {
            if (binding.checkBox.isChecked) {
                saveLogin(data, true)
            } else {
                saveLogin(data, false)
            }
        } else {
            showAlert(Constant.APP_NAME, data?.message.toString())
        }
    }

    private fun saveLogin(it: ResponseLogin, rememberMe: Boolean) {
        prefLogin?.saveLogin(binding.etUsername.text.toString(), rememberMe)

        storeViewModel.deleteStore()
        it.stores?.forEach {
            storeViewModel.insertStore(it)
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
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
            alertDialog.setPositiveButton("OK") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            alertDialog.show()
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(this@LoginActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@LoginActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@LoginActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                    ), 1)
            } else {
                ActivityCompat.requestPermissions(this@LoginActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                    ), 1)
            }
        }
    }

    private fun showLoading(isVisible: Boolean) {
        binding.apply {
            if (isVisible) {

            } else {

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
                    if ((ContextCompat.checkSelfPermission(this@LoginActivity,
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