package com.finder.finderapp.ui.businessmap

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.finder.finderapp.R
import com.finder.finderapp.core.ResponseResult
import com.finder.finderapp.core.hasLocationPermissions
import com.finder.finderapp.core.isGPSEnabled
import com.finder.finderapp.core.secondsToFormat
import com.finder.finderapp.core.showDialogTwoOptions
import com.finder.finderapp.core.toDistanceFormat
import com.finder.finderapp.data.model.Summary
import com.finder.finderapp.databinding.FragmentBusinessMapBinding
import com.finder.finderapp.presentation.BusinessMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessMapFragment : Fragment(R.layout.fragment_business_map), OnMapReadyCallback{

    private lateinit var binding: FragmentBusinessMapBinding
    private val args by navArgs<BusinessMapFragmentArgs>()
    private lateinit var googleMap: GoogleMap
    private val viewModel by viewModels<BusinessMapViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private lateinit var finishLocation: LatLng
    private lateinit var locationCallback: LocationCallback
    private var currentMarker: Marker? = null
    private var finishMarker: Marker? = null
    private var polyLine: Polyline? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBusinessMapBinding.bind(view)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initComponents()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        with(map.uiSettings) {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
        }
    }

    private fun initComponents(){

        initFlowForTracking()

        viewModel.route.observe(viewLifecycleOwner){responseRoute->
            when(responseRoute){
                is ResponseResult.Loading -> binding.progressBar.isVisible = true

                is ResponseResult.Success -> {
                    binding.progressBar.isVisible = false
                    responseRoute.data?.features?.first()?.geometry?.coordinates?.let {
                        drawPolyline(it)
                        currentLocation?.let { location->
                            updateMarkers(location)
                        }
                    }

                    responseRoute.data?.features?.first()?.properties?.summary?.let {
                        showInformationRoute(it)
                    }
                }

                is ResponseResult.Error ->{
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnInitRoute.setOnClickListener {
            it.visibility = View.GONE
            initFlowForTracking()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialogTwoOptions(getString(R.string.stop_route), true, requireContext()){
                    NavHostFragment.findNavController(this@BusinessMapFragment).navigateUp()
                }
            }
        }
        )
    }

    private fun initFlowForTracking(){
        val finishLat  = args.business.coordinates.latitude
        val finishLng  = args.business.coordinates.longitude

        //init flow for the tracking
        if (finishLat != null && finishLng != null) {
            finishLocation = LatLng(finishLat, finishLng)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            //validate permissions
            if (hasLocationPermissions(requireContext())) {
                if (isGPSEnabled(requireActivity())) {
                    //init location request
                    initRequestLocation()
                }else{
                    Toast.makeText(requireContext(), getText(R.string.txt_gps_required), Toast.LENGTH_SHORT).show()
                    binding.btnInitRoute.isVisible = true
                }
            } else {
                binding.btnInitRoute.isVisible = true
                Toast.makeText(requireContext(), getText(R.string.txt_location_required), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initRequestLocation(){
        try {
            val requestLocation = createSettingsRequestLocation()
            initLocationCallBack()
            //init locations update
            fusedLocationClient.requestLocationUpdates(requestLocation, locationCallback, Looper.getMainLooper())
        } catch (e: Exception) {
            println("error to start request location")
        }
    }

    private fun createSettingsRequestLocation() =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 7000).apply {
            //minimum distance to get locations updates
            setMinUpdateDistanceMeters(30.0f)
            //get updates for type permissions (fine location)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setMinUpdateIntervalMillis(7000)
        }.build()


    private fun initLocationCallBack(){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    println("update locations")
                    currentLocation = LatLng(it.latitude, it.longitude)
                    doRequestRoute(it)
                }
            }
        }
    }

    private fun createMarker(position: LatLng, title: String, icon: Int): Marker? {
       return googleMap.addMarker(
           MarkerOptions()
               .position(position)
               .title(title)
               .icon(BitmapDescriptorFactory.fromResource(icon)))
    }

    private fun doRequestRoute(currentLocation: Location) {

        val formatCurrentLocation = "${currentLocation.longitude},${currentLocation.latitude}"
        val formatFinishLocation = "${finishLocation.longitude},${finishLocation.latitude}"

        if(formatCurrentLocation.isNotEmpty() && formatFinishLocation.isNotEmpty()){
            viewModel.getRouteByLocation(formatCurrentLocation, formatFinishLocation)
        }
    }

    private fun drawPolyline(pointList: List<List<Double>>){
        if(::googleMap.isInitialized){
            val polylineOptions = PolylineOptions()
            pointList.forEach {
                //latitude = 1 longitude = 0
                polylineOptions.add(LatLng(it[1], it[0]))
            }
            polyLine?.remove()
            polylineOptions.width(18.0f).color(ContextCompat.getColor(requireContext(), R.color.black))
            polyLine = googleMap.addPolyline(polylineOptions)
        }
    }

    private fun showInformationRoute(summary: Summary) {
        binding.txtDistance.text = summary.distance?.toDistanceFormat(2) ?: "0.0km"

        val resultDuration = summary.duration?.toInt()?.secondsToFormat() ?: run{"0.0s"}
        binding.txtDuration.text = resultDuration
    }

    private fun animateCamera(currentMarker: Marker?, finishMarker: Marker?) {
        //Create Markers List
        val markersList: MutableList<Marker> = mutableListOf()
        markersList.add(0, currentMarker!!)
        markersList.add(1, finishMarker!!)

        //get the latLngbuilder from the marker list
        val boundsBuilder = LatLngBounds.Builder()
        for (marker in markersList) {
            boundsBuilder.include(marker.position)
        }

        //Create bounds here
        val bounds: LatLngBounds = boundsBuilder.build()

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
        // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 18f), 2000, null)
    }

    private fun updateMarkers(currentLocation: LatLng) {

        if(::googleMap.isInitialized){
            //Create finish marker
            if(finishMarker == null){
                finishMarker = createMarker(finishLocation, args.business.name ?: "", R.drawable.ic_finish_marker)
                finishMarker?.showInfoWindow()
            }

            if (currentMarker == null) {
                currentMarker = createMarker(
                    LatLng(currentLocation.latitude, currentLocation.longitude),
                    getString(R.string.map_my_location),
                    R.drawable.ic_user_marker
                )
            } else {
                currentMarker?.position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }

            animateCamera(currentMarker, finishMarker)
        }

    }
}