package com.example.finderapp.ui.businessmap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.finderapp.R
import com.example.finderapp.core.ResponseResult
import com.example.finderapp.core.actionOpenMaps
import com.example.finderapp.core.secondsToFormat
import com.example.finderapp.core.toDistanceFormat
import com.example.finderapp.data.model.Summary
import com.example.finderapp.databinding.FragmentBusinessMapBinding
import com.example.finderapp.presentation.BusinessMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessMapFragment : Fragment(R.layout.fragment_business_map), OnMapReadyCallback {

    private lateinit var binding: FragmentBusinessMapBinding
    private val args by navArgs<BusinessMapFragmentArgs>()
    private lateinit var googleMap: GoogleMap
    private val viewModel by viewModels<BusinessMapViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private var finishLocation: LatLng? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBusinessMapBinding.bind(view)
        initComponents()
    }

    private fun initComponents(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        myCurrentLocation()

        if(args.business.coordinates.latitude!= null && args.business.coordinates.longitude != null){
            finishLocation = LatLng(args.business.coordinates.latitude!! ,args.business.coordinates.longitude!!)
        }

        viewModel.route.observe(viewLifecycleOwner){responseRoute->
            when(responseRoute){
                is ResponseResult.Loading -> binding.progressBar.isVisible = true

                is ResponseResult.Success -> {
                    binding.progressBar.isVisible = false
                    responseRoute.data?.features?.first()?.geometry?.coordinates?.let {
                        drawPolyline(it)
                        createMarkers()
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

        binding.btnOpenMaps.setOnClickListener {
            finishLocation?.let {
                requireContext().actionOpenMaps(it)
            }
        }
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

    private fun myCurrentLocation(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = fusedLocationClient.lastLocation
            location.addOnSuccessListener {location->
                if(location != null){
                    doRequestRoute(location)
                    currentLocation = LatLng(location.latitude, location.longitude)
                }else{
                    Toast.makeText(requireContext(), getText(R.string.error_location), Toast.LENGTH_SHORT).show()
                }
            }
        } else{
            Toast.makeText(requireContext(), getText(R.string.txt_required_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private fun doRequestRoute(location: Location) {
        val currentLocation = "${location.longitude},${location.latitude}"
        val finishLocation = "${args.business.coordinates.longitude},${args.business.coordinates.latitude}"

//        println(currentLocation)
//        println(finishLocation)

        if(currentLocation.isNotEmpty() && finishLocation.isNotEmpty()){
            viewModel.getRouteByLocation(currentLocation, finishLocation)
        }
    }

    private fun drawPolyline(pointList: List<List<Double>>){
        val polylineOptions = PolylineOptions()
       if(::googleMap.isInitialized){
           pointList.forEach {
               //latitude = 1 longitude = 0
               polylineOptions.add(LatLng(it[1], it[0]))
           }
           //size
           polylineOptions.width(18.0f).color(ContextCompat.getColor(requireContext(), R.color.black))
           googleMap.addPolyline(polylineOptions)
       }
    }

    private fun showInformationRoute(summary: Summary) {
        binding.txtDistance.text = summary.distance?.toDistanceFormat(2) ?: "0.0km"
        println(summary.duration)

        val resultDuration = summary.duration?.toInt()?.secondsToFormat() ?: run{"0.0s"}
        binding.txtDuration.text = resultDuration
    }


    private fun createMarkers(){
        currentLocation?.let {

            val currentMarker = googleMap.addMarker(MarkerOptions()
                .position(currentLocation!!).
                title(getString(R.string.map_my_location)).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_marker)))

            val finishMarker = googleMap.addMarker(
                MarkerOptions().position(finishLocation!!).title(args.business.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_finish_marker))
            )

            finishMarker?.showInfoWindow()
            animateCamera(currentMarker, finishMarker)
        }
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
}