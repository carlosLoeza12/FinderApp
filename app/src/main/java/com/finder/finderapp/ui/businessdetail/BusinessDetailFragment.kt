package com.finder.finderapp.ui.businessdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.finder.finderapp.databinding.FragmentBusinessDetailBinding
import com.finder.finderapp.R
import com.finder.finderapp.core.ResponseResult
import com.finder.finderapp.core.showDialogTwoOptions
import com.finder.finderapp.core.toFormatAddress
import com.finder.finderapp.core.toFormatCategories
import com.finder.finderapp.core.toLoadCarousel
import com.finder.finderapp.data.model.Business
import com.finder.finderapp.presentation.BusinessDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

@AndroidEntryPoint
class BusinessDetailFragment : Fragment(R.layout.fragment_business_detail) {

    private lateinit var binding: FragmentBusinessDetailBinding
    private val viewModel by viewModels<BusinessDetailViewModel>()
    private val args by navArgs<BusinessDetailFragmentArgs>()
    private lateinit var businessDetailAdapter: BusinessDetailAdapter
    private var business: Business? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args.let {
            args.businessId?.let { businessId->
                viewModel.getBusinessById(businessId)
                viewModel.getReviewsByBusiness(businessId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBusinessDetailBinding.bind(view)
        initComponents()
    }

    private fun initComponents(){

        businessDetailAdapter = BusinessDetailAdapter(emptyList())
        binding.recyclerReviews.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = businessDetailAdapter
        }

        viewModel.business.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Loading -> binding.progressBar.isVisible = true

                is ResponseResult.Success -> {
                    binding.progressBar.isVisible = false
                    result.data?.let { businessResult->
                        setDataForBusiness(businessResult)
                        business = businessResult
                    }
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                }
            }
        }

        viewModel.reviews.observe(viewLifecycleOwner){ result->
            when(result){
                is ResponseResult.Loading -> println("loading reviews")
                is ResponseResult.Success -> {
                    result.data?.reviews?.let {
                        if (it.isNotEmpty()) {
                            businessDetailAdapter.updateReviewList(it)
                        } else {
                            binding.txtNoReviews.isVisible = true
                        }
                    }
                }
                is ResponseResult.Error -> {
                    binding.txtNoReviews.isVisible = true
                }
            }
        }

        binding.imgMaps.setOnClickListener {
            business?.let {
                showDialogTwoOptions(getString(R.string.txt_start_route, business?.name), true, requireContext()){
                    val action = BusinessDetailFragmentDirections.actionBusinessDetailFragmentToBusinessMapFragment(it)
                    findNavController().navigate(action)
                }
            }
        }

    }

    private fun setDataForBusiness(business: Business){
        binding.txtRating.text = business.rating.toString()
        binding.txtName.text = business.name
        binding.txtCategory.text = business.categories?.toFormatCategories()
        binding.txtAddress.text = business.location.display_address?.toFormatAddress()
        binding.txtPhone.text = business.display_phone

        business.photos?.let {
            binding.carousel.addData(it.toLoadCarousel())
        } ?: run {
            binding.carousel.addData(CarouselItem(R.drawable.not_found))
        }
    }

}