package com.pascal.tokoku.presentation.main.finished

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.parcelable
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.databinding.ActivityFinishedBinding
import com.pascal.tokoku.presentation.main.mapStore.MapsStoreActivity

class FinishedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishedBinding
    private var itemExtra: StoreEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        itemExtra = intent.parcelable("item")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window?.decorView?.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            window.statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            if (itemExtra != null) {
                etName.text = itemExtra!!.storeName
                etStoreCode.text = itemExtra!!.storeCode
            }

            btnSelesai.setOnClickListener {
                startActivity(Intent(this@FinishedActivity, MapsStoreActivity::class.java))
                finish()
            }
        }

    }
}