package com.pascal.tokoku.presentation.main.detailStore

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.common.utils.CurrentLocation
import com.pascal.tokoku.common.utils.FilePath
import com.pascal.tokoku.common.utils.getCurrentDate
import com.pascal.tokoku.common.utils.parcelable
import com.pascal.tokoku.common.utils.showAlert
import com.pascal.tokoku.common.utils.showToast
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.databinding.ActivityDetailStoreBinding
import com.pascal.tokoku.presentation.main.finished.FinishedActivity
import com.pascal.tokoku.presentation.main.viewModel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoreBinding
    private val viewModel: StoreViewModel by viewModels()
    private var imagePath: String = ""
    private var isTagging = false
    private var isLocation = false
    private var location: Location? = null
    private var itemExtra: StoreEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        attachObserve()
    }

    private fun initView() {
        permissionLocation()
        location = CurrentLocation().getLocationWithCheckNetworkAndGPS(this)
        itemExtra = intent.parcelable("item")

        binding.apply {
            btnCamera.setOnClickListener {
                getImage()
            }

            btnTagging.setOnClickListener {
                isTagging = true
                binding.checkTagging.setColorFilter(ContextCompat.getColor(this@DetailStoreActivity, R.color.green_dark), android.graphics.PorterDuff.Mode.SRC_IN)
                showToast(location?.latitude.toString())
            }

            btnLocation.setOnClickListener {
                isLocation = true
                binding.checkLocation.setColorFilter(ContextCompat.getColor(this@DetailStoreActivity, R.color.green_dark), android.graphics.PorterDuff.Mode.SRC_IN)
                showToast(itemExtra?.address.toString())
            }

            btnVisit.setOnClickListener {
                actionVisit()
            }

            btnNoVisit.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (itemExtra != null) {
            binding.apply {
                etName.text = itemExtra?.storeName
                etAddress.text = itemExtra?.address
                etTipeOutlet.text = itemExtra?.storeCode
                etSubTipeDisplay.text = itemExtra?.storeId
                etLastVisit.text = getCurrentDate()
            }
        }
    }

    private fun attachObserve() {
        lifecycleScope.apply {
            viewModel.insertStoreResult.asLiveData().observe(this@DetailStoreActivity) {
                if (it is Resource.Success) {
                    dialogSuccess()
                } else if (it is Resource.Error) {
                    Log.d("TAG", "observeData: ${it.exception}")
                }
            }
        }
    }

    private fun actionVisit() {
        if (imagePath == "") {
            showAlert(Constant.APP_NAME, "Harap ambil foto terlebih dahulu")
        } else if (!isTagging) {
            showAlert(Constant.APP_NAME, "Harap tagging gps terlebih dahulu")
        } else  if (!isLocation) {
            showAlert(Constant.APP_NAME, "Harap tagging lokasi terlebih dahulu")
        } else {
            itemExtra?.isVisit = true
            itemExtra?.let { viewModel.insertStore(it) }
        }
    }

    private fun getImage() {
        ImagePicker.with(this)
            .crop()
            .compress(500)
            .maxResultSize(400, 600)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val img = fileUri.let { FilePath.getPath(this, it) }

                imagePath = img.toString()
                binding.checkCamera.setColorFilter(ContextCompat.getColor(this, R.color.green_dark), android.graphics.PorterDuff.Mode.SRC_IN)


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showToast(ImagePicker.getError(data))
            }
        }

    private fun permissionLocation() {
        if (getLocationMode() != 3) {
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

    private fun getLocationMode(): Int {
        try {
            return Settings.Secure.getInt(this.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun dialogSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle(Constant.APP_NAME)
            setMessage("Visit Success")
            setIcon(R.drawable.ic_cheklist)
            setCancelable(false)

            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface?.dismiss()
                val intent = Intent(this@DetailStoreActivity, FinishedActivity::class.java)
                intent.putExtra("item", itemExtra)
                startActivity(intent)
                finish()
            }
        }.show()
    }
}