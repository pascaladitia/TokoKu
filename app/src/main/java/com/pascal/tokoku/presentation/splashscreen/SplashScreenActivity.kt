package com.pascal.tokoku.presentation.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.PrefLogin
import com.pascal.tokoku.presentation.login.LoginActivity
import com.pascal.tokoku.presentation.main.home.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var prefLogin: PrefLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        runBlocking {
            lifecycleScope.launch {
                delay(2000)
                actionIntent()
                finish()
            }
        }
    }

    private fun actionIntent() {
        prefLogin = PrefLogin()
        prefLogin.initPref(this)

        if (prefLogin.rememberMe() == true) {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
        }
    }
}