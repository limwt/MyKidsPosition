package com.wt.kids.mykidsposition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import com.wt.kids.mykidsposition.data.response.ResponseItemsData
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.receiver.SmsReceiver
import com.wt.kids.mykidsposition.service.JeffService
import com.wt.kids.mykidsposition.utils.AppSignatureHelper
import com.wt.kids.mykidsposition.utils.DataStoreUtils
import com.wt.kids.mykidsposition.utils.LocationUtils
import com.wt.kids.mykidsposition.utils.Logger
import com.wt.kids.mykidsposition.view.adapter.PlaceListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    private val logTag = this::class.java.simpleName
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startService()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startService()
            } else -> {
                // No location access granted.
            }
        }
    }


    private val viewModel: MainViewModel by viewModels()
    private val gSon = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private val savedPlaceList = mutableListOf<ResponseItemsData>()
    private val smsReceiver = SmsReceiver()

    @Inject lateinit var placeListAdapter: PlaceListAdapter
    @Inject lateinit var logger: Logger
    @Inject lateinit var locationUtils: LocationUtils
    @Inject lateinit var dataStoreUtils: DataStoreUtils

    @Inject lateinit var appSignatureHelper: AppSignatureHelper

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var mapView: MapView
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetContainer: ConstraintLayout
    private lateinit var searchPlaceButton: FloatingActionButton
    private lateinit var editTextView: EditText
    private lateinit var searchButton: ImageView
    private lateinit var bottomSheetTitleText: TextView
    private lateinit var searchEditTextContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.logD(logTag, "onCreate")
        setContentView(R.layout.activity_main)
        initViews(savedInstanceState)
        createSmsRetrieverClient()
        observingViewModel()

        if (verifyPermissions(this)) {
            startService()
        } else {
            requestLocationPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroySmsRetrieverClient()
    }

    private fun initViews(savedInstanceState: Bundle?) {
        logger.logD(logTag, "initViews : ${appSignatureHelper.appSignatures}")
        mapView = findViewById(R.id.mapView)
        recyclerView = findViewById(R.id.recyclerView)
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        searchPlaceButton = findViewById(R.id.searchPlaceButton)
        searchPlaceButton.setOnClickListener {
            editTextView.setText("")
            searchEditTextContainer.visibility = View.VISIBLE
        }
        editTextView = findViewById(R.id.editTextView)
        editTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                logger.logD(logTag, "beforeTextChanged : $text")
            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                logger.logD(logTag, "onTextChanged : $text")
            }

            override fun afterTextChanged(e: Editable) {
            }
        })
        editTextView.setOnEditorActionListener { _, action, keyEvent ->
            var handled = false
            logger.logD(logTag, "Editor Action $action, $keyEvent")

            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                goToSearch()
                handled = true
            }

            if (action == EditorInfo.IME_ACTION_DONE) {
                handled = true
            }

            handled
        }

        searchButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            goToSearch()
        }

        bottomSheetContainer.visibility = View.GONE
        fusedLocationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        // onCreate 연결
        mapView.onCreate(savedInstanceState)

        recyclerView.adapter = placeListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 검색된 장소들 중 선택 시 이동...
        placeListAdapter.setOnItemClickListener(object : PlaceListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, data: ResponseItemsData) {
                if (view.id == R.id.deleteButton) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (savedPlaceList.contains(data)) {
                            savedPlaceList.remove(data)
                        }

                        dataStoreUtils.setPlace(gSon.toJson(savedPlaceList))

                        // 지도 위치 이동
                        locationUtils.getLocationData()?.let {
                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
                            naverMap.moveCamera(cameraUpdate)
                        }
                        // bottom sheet 내리기...
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                } else {
                    val tm = Tm128(data.mapx.toDouble(), data.mapy.toDouble())
                    val cameraUpdate = CameraUpdate.scrollTo(tm.toLatLng())
                    naverMap.moveCamera(cameraUpdate)
                    // bottom sheet 내리기...
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })

        bottomSheetTitleText = findViewById(R.id.bottomSheetTitleTextView)
        searchEditTextContainer = findViewById(R.id.searchEditTextContainer)

        // 저장된 위치를 bottomSheet 에 표시
        CoroutineScope(Dispatchers.Main).launch {
            dataStoreUtils.getPlace().collectLatest { item ->
                if (item.isNotEmpty()) {
                    val typeToken = object : TypeToken<List<ResponseItemsData>>() {}.type
                    val result = gSon.fromJson<List<ResponseItemsData>>(item, typeToken)
                    savedPlaceList.clear()
                    savedPlaceList.addAll(result)
                    logger.logD(logTag, "getPlace : $savedPlaceList")

                    bottomSheetContainer.visibility = View.VISIBLE
                    bottomSheetTitleText.text = String.format(getString(R.string.str_saved_count), savedPlaceList.size)
                    placeListAdapter.setSheetType(PlaceListAdapter.SheetType.TYPE_SAVED)
                    placeListAdapter.submitList(savedPlaceList)
                    placeListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun createSmsRetrieverClient()  {
        logger.logD(logTag, "createSmsRetrieverClient")
        IntentFilter().apply {
            addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        }.also {
            registerReceiver(smsReceiver, it)
        }

        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever().apply {
            addOnSuccessListener {
                logger.logD(logTag, "Success")
            }
            addOnFailureListener {
                logger.logD(logTag, "Failure")
            }
        }
    }

    private fun destroySmsRetrieverClient() {
        logger.logD(logTag, "destroySmsRetrieverClient")
        unregisterReceiver(smsReceiver)

    }

    private fun observingViewModel() {
        viewModel.searchData.observe(this) {
            bottomSheetContainer.visibility = View.VISIBLE
            bottomSheetTitleText.text = String.format(getString(R.string.str_result_count), it.total)
            updateMarker(it.items)
            placeListAdapter.setSheetType(PlaceListAdapter.SheetType.TYPE_SEARCH)
            placeListAdapter.submitList(it.items)
            // bottom sheet 올리기...
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun goToSearch() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editTextView.windowToken, 0)
        searchEditTextContainer.visibility = View.GONE
        viewModel.searchPlace(editTextView.text.toString())
    }

    private fun startService() {
        logger.logD(logTag, "startService")
        val intent = Intent(this, JeffService::class.java)
        startForegroundService(intent)
        // 맵 가져오기 -> onMapReady
        mapView.getMapAsync(this)
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun verifyPermissions(context: Context): Boolean {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun updateMarker(items: List<ResponseItemsData>) {
        // 현재위치와 다른 장소를 안내할 경우 첫번째 위치로 이동...
        if (items.isNotEmpty()) {
            val tm = Tm128(items[0].mapx.toDouble(), items[0].mapy.toDouble())
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(tm.toLatLng(), 15.0).animate(CameraAnimation.Fly, 1000)
            naverMap.moveCamera(cameraUpdate)
        }

        items.forEachIndexed { index, place ->
            Marker().apply {
                val tm = Tm128(place.mapx.toDouble(), place.mapy.toDouble())
                position = tm.toLatLng()
                setOnClickListener {
                    if (it is Marker) {
                        //test
                        viewModel.searchAddress("${tm.toLatLng().longitude},${tm.toLatLng().latitude}")
                        savePlace(place)
                    }
                    true
                }
                map = naverMap
                tag = index + 1
                icon = MarkerIcons.BLACK
                iconTintColor = Color.RED
            }
        }
    }

    private fun savePlace(place: ResponseItemsData) {
        Toast.makeText(applicationContext, "위치를 등록했습니다.", Toast.LENGTH_SHORT).show()
        bottomSheetContainer.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            savedPlaceList.add(place)
            dataStoreUtils.setPlace(gSon.toJson(savedPlaceList))
        }

        moveToCurrentPosition()
    }

    private fun moveToCurrentPosition() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            // 지도 위치 이동
            locationUtils.getLocationData()?.let {
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
                naverMap.moveCamera(cameraUpdate)
            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        logger.logD(logTag, "onMapReady")
        naverMap = map

        naverMap.apply {
            maxZoom = 18.0
            minZoom = 10.0
            locationSource = fusedLocationSource
            moveToCurrentPosition()
        }.also {
            val locationButton = findViewById<LocationButtonView>(R.id.locationButton)
            locationButton.map = it
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onMapClick(p0: PointF, p1: LatLng) {
        editTextView.visibility = View.GONE
    }
}