package com.finder.finderapp.ui.searchbusiness

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finder.finderapp.R
import com.finder.finderapp.core.ResponseResult
import com.finder.finderapp.core.hasLocationPermissions
import com.finder.finderapp.core.showDialogTwoOptions
import com.finder.finderapp.core.isGPSEnabled
import com.finder.finderapp.core.requestGPS
import com.finder.finderapp.core.validatePermissions
import com.finder.finderapp.data.model.Business
import com.finder.finderapp.data.model.Permissions
import com.finder.finderapp.databinding.FragmentSearchBussinesBinding
import com.finder.finderapp.presentation.BusinessViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchBusinessFragment : Fragment(R.layout.fragment_search_bussines), SearchBusinessAdapter.OnBusinessClickListener {

    private lateinit var binding: FragmentSearchBussinesBinding
    private val viewModel by viewModels<BusinessViewModel>()
    private lateinit var searchBusinessAdapter: SearchBusinessAdapter
    private lateinit var menuHost: MenuHost
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted ->
        if(isGranted){
            if(isGPSEnabled(requireActivity())){
                println("gps activated")
            }else{
                showDialogTwoOptions(getString(R.string.txt_gps_required_request), true, requireContext()){
                    requestGPS(requireContext())
                }
            }
        }else{
            showDialogTwoOptions(getString(R.string.txt_location_required_request), true, requireContext()){
                requestPermissionLauncher()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBussinesBinding.bind(view)
        initComponents()
    }

    private fun initComponents(){

        //for user location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //menu
        menuHost = requireActivity()

        showSearchMenu()

        //request permissions
        requestPermission()

        searchBusinessAdapter = SearchBusinessAdapter(emptyList(), this)
        binding.recyclerBusiness.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchBusinessAdapter
        }

        viewModel.businessesList.observe(viewLifecycleOwner){ responseResult ->
            when(responseResult){
                is ResponseResult.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.linearNoResults.isVisible = false
                }

                is ResponseResult.Success -> {
                    binding.progressBar.isVisible = false
                    responseResult.data?.let { data ->
                        if (!data.businesses.isNullOrEmpty()) {
                            binding.linearNoResults.isVisible = false
                            searchBusinessAdapter.updateBusinessList(data.businesses)
                        } else {
                            binding.linearNoResults.isVisible = true
                            searchBusinessAdapter.updateBusinessList(emptyList())
                        }
                    }
                }

                is ResponseResult.Error -> {
                    binding.progressBar.isVisible = false
                    binding.linearNoResults.isVisible = true
                    searchBusinessAdapter.updateBusinessList(emptyList())
                }
            }
        }

    }

    private fun requestPermissionLauncher(){
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onBusinessClick(business: Business) {
        val currentFragment = findNavController().currentDestination?.id ?: -1
        val destinyFragment = R.id.searchBusinessFragment
        if(currentFragment != -1 && currentFragment == destinyFragment){
            val action = SearchBusinessFragmentDirections.actionSearchBusinessFragmentToBusinessDetailFragment(business.id)
            findNavController().navigate(action)
            println(business.id)
        }
    }

    private fun showSearchMenu(){
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchItem = menu.findItem(R.id.itemSearch)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            doSearchByLocation(query)
                        }
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    @SuppressLint("MissingPermission")
    private fun doSearchByLocation(query: String){
       if (hasLocationPermissions(requireContext())) {

           fusedLocationClient.lastLocation.addOnSuccessListener {
               if(it == null){
                   if (isGPSEnabled(requireActivity())) {
                       fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                           .addOnSuccessListener { location ->
                               location?.let {
                                   println("currentLocation")
                                   viewModel.getBusinessByKeyword(location.latitude, location.longitude, query)
                               }
                           }
                   }else{
                       Toast.makeText(requireContext(), getText(R.string.error_location), Toast.LENGTH_SHORT).show()
                   }
               }else{
                   println("lastlocation")
                   viewModel.getBusinessByKeyword(it.latitude, it.longitude, query)
               }
           }

       } else{
           requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
       }
    }

    private fun requestPermission(){
        val resultPermission = validatePermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if(resultPermission == Permissions.REFUSED || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            if(isGPSEnabled(requireActivity())){
                println("gps activated")
            }else{
                showDialogTwoOptions(getString(R.string.txt_gps_required_request), true, requireContext()){
                    requestGPS(requireContext())
                }
            }
        }
    }
}