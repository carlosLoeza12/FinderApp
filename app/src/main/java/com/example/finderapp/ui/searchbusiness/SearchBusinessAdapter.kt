package com.example.finderapp.ui.searchbusiness

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finderapp.R
import com.example.finderapp.core.loadImage
import com.example.finderapp.core.toDistanceFormat
import com.example.finderapp.core.toFormatAddress
import com.example.finderapp.core.toFormatCategories
import com.example.finderapp.core.validateBusiness
import com.example.finderapp.data.model.Business
import com.example.finderapp.databinding.BusinessItemBinding

class SearchBusinessAdapter(
    private var businessList: List<Business>,
    private val itemClickListener: OnBusinessClickListener
) : RecyclerView.Adapter<SearchBusinessAdapter.ViewHolder>() {

    interface OnBusinessClickListener{
        fun onBusinessClick(business: Business)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBusinessAdapter.ViewHolder {
        val itemBinding = BusinessItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(itemBinding)
        itemBinding.root.setOnClickListener {
            val position = holder.bindingAdapterPosition.takeIf {
                it != -1
            } ?: return@setOnClickListener
            itemClickListener.onBusinessClick(businessList[position])
        }
        return holder
    }

    override fun onBindViewHolder(holder: SearchBusinessAdapter.ViewHolder, position: Int) {
      holder.bind(businessList[position])
    }

    override fun getItemCount(): Int {
       return businessList.size
    }

    inner class ViewHolder(private val itemBinding: BusinessItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(business: Business){
            itemBinding.imgBusiness.loadImage(business.image_url ?: "", false)
            itemBinding.txtBusinessName.text = business.name
            itemBinding.txtBusinessAddress.text = business.location.display_address?.toFormatAddress()
            itemBinding.txtBusinessCategory.text = business.categories?.toFormatCategories()
            itemBinding.txtRating.text = business.rating.toString()
            itemBinding.imgOpen.load(business.is_closed?.validateBusiness() ?: R.drawable.ic_red_circle)
            itemBinding.txtDistance.text = business.distance?.toDistanceFormat(2) ?: "0.0 kms"
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBusinessList(list: List<Business>){
        this.businessList = list
        notifyDataSetChanged()
    }
}