package com.finder.finderapp.ui.businessdetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finder.finderapp.core.loadImage
import com.finder.finderapp.core.toDateFormat
import com.finder.finderapp.data.model.Review
import com.finder.finderapp.databinding.ReviewItemBinding

class BusinessDetailAdapter(private var reviewList: List<Review>): RecyclerView.Adapter<BusinessDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessDetailAdapter.ViewHolder {
        val itemBinding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return  ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BusinessDetailAdapter.ViewHolder, position: Int) {
       holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateReviewList(reviewList: List<Review>){
        this.reviewList = reviewList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ReviewItemBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun bind(review: Review){
            itemBinding.imgProfile.loadImage(review.user?.image_url ?: "", true)
            itemBinding.txtRating.text = review.rating.toString()
            itemBinding.txtName.text = review.user?.name
            itemBinding.txtReviewDescription.text = review.text
            itemBinding.txtDate.text = if(!review.time_created.isNullOrEmpty()) review.time_created.toDateFormat() else ""
        }
    }
}