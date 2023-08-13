package com.pascal.tokoku.presentation.main.mapStore

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pascal.tokoku.R
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.common.utils.ViewAnimatorSlideUpDown
import com.pascal.tokoku.common.utils.showAlert
import com.pascal.tokoku.common.utils.showToast
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.databinding.ActivityMapsStoreBinding
import com.pascal.tokoku.presentation.adapter.AdapterStore
import com.pascal.tokoku.presentation.main.detailStore.DetailStoreActivity
import com.pascal.tokoku.presentation.main.viewModel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapsStoreActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsStoreBinding
    private val viewModel: StoreViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private var userCircle: Circle? = null
    private var userLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        attachObserve()
        animationBottom()
    }

    private fun animationBottom() {
        binding.apply {
            layoutStore.setOnClickListener {
                ViewAnimatorSlideUpDown.slideDown(layoutStore)
            }

            layoutBottom.setOnClickListener {
                ViewAnimatorSlideUpDown.slideUp(layoutStore)
            }
        }
    }

    private fun initView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.apply {
            back.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window?.decorView?.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun attachObserve() {
        lifecycleScope.apply {
            launchWhenStarted {
                viewModel.getStoreResult.collect {
                    when (it) {
                        is Resource.Empty -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            showResponse(it.data)
                            Log.d("TAG", "observeData: ${it.data}")
                        }

                        is Resource.Error -> {
                            showAlert(Constant.APP_NAME, it.message.toString())
                            Log.d("TAG", "observeData: ${it.exception} or ${it.message}")
                        }
                    }
                }
            }
        }
    }

    private fun showResponse(data: List<StoreEntity>?) {
        if (!data.isNullOrEmpty()) {
            addMarker(data)

            val adapter = AdapterStore(data, object : AdapterStore.OnClickListener {
                override fun detail(item: StoreEntity) {
                    if (item.isVisit == true) {
                        showAlert(Constant.APP_NAME, "Store sudah divisit!")
                    } else {
                        actionVisit(item)
                    }
                }
            })
            binding.rvStore.adapter = adapter
        } else {
            showAlert(Constant.APP_NAME, "Toko Kosong")
        }
    }

    private fun actionVisit(item: StoreEntity) {
        val storeLatLng = LatLng(item.latitude?.toDoubleOrNull() ?: 0.0, item.longitude?.toDoubleOrNull() ?: 0.0)

        if (isLocationInsideRadius(storeLatLng)) {
            val intent = Intent(this, DetailStoreActivity::class.java)
            intent.putExtra("item", item)
            startActivity(intent)
        } else {
            showAlert(Constant.APP_NAME, "Jarak Terlalu Jauh!")
        }
    }

    private fun addMarker(data: List<StoreEntity>) {
        data.forEach{
            val markerInRadius = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude?.toDoubleOrNull() ?: 0.0, it.longitude?.toDoubleOrNull() ?: 0.0))
                    .title(it.storeName)
                    .snippet(it.areaName)
            )

            // Calculate distances and set snippet
            if (userLocation != null) {
                val distanceIn = calculateDistance(userLocation!!, markerInRadius!!.position)
                markerInRadius.snippet = "Jarak: $distanceIn meter"

                it.distance = distanceIn.toString()
            } else {
                dialogPermission()
            }
        }
    }

    private fun dialogPermission() {
        AlertDialog.Builder(this).apply {
            setTitle(Constant.APP_NAME)
            setMessage("Lokasi tidak ditemukan harap muat ulang aplikasi!")
            setIcon(R.drawable.ic_warning)
            setCancelable(false)

            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface?.dismiss()
                finish()
            }
        }.show()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.isMyLocationEnabled = true

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            var location: Location? = null
            location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(
                    criteria,
                    false
                )!!
            )

            if (location != null) {
                userLocation = LatLng(location.latitude, location.longitude)
                val userLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))
                val cameraPosition = CameraPosition.Builder()
                    .target(userLatLng).zoom(12f).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                // Add circle with radius 100 meters
                userCircle?.remove()
                userCircle = mMap.addCircle(
                    CircleOptions()
                        .center(userLatLng)
                        .radius(100.0)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.parseColor("#220000FF"))
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true;
        mMap.setPadding(0, 250, 18, 0);
    }

    private fun isLocationInsideRadius(latLng: LatLng): Boolean {
        val circleCenter = userLocation
        val radius = userCircle?.radius

        if (circleCenter != null && radius != null) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                circleCenter.latitude, circleCenter.longitude,
                latLng.latitude, latLng.longitude, distance
            )
            return distance[0] <= radius
        }
        return false
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Int {
        val results = FloatArray(1)
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
        return results[0].roundToInt()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getStore()
    }
}

