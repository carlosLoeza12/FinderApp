package com.example.finderapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finderapp.core.ResponseResult
import com.example.finderapp.data.model.Business
import com.example.finderapp.data.model.Reviews
import com.example.finderapp.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BusinessDetailViewModel @Inject constructor(private val repository: BusinessRepository): ViewModel() {

    private val _business = MutableLiveData<ResponseResult<Business>>()
    val business: LiveData<ResponseResult<Business>> = _business

    private val _reviews = MutableLiveData<ResponseResult<Reviews>>()
    val reviews: LiveData<ResponseResult<Reviews>> = _reviews

    fun getBusinessById(id: String){
        viewModelScope.launch {
            try {
                _business.value = ResponseResult.Loading()
                val resultBusiness = withContext(Dispatchers.IO){repository.getBusinessById(id)}
                _business.value = ResponseResult.Success(resultBusiness)
            } catch (e: Exception) {
                _business.value = ResponseResult.Error(e.toString())
            }
        }
    }

    fun getReviewsByBusiness(id: String) {
        viewModelScope.launch {
            try {
                _reviews.value = ResponseResult.Loading()
                val result = withContext(Dispatchers.IO){repository.getReviewsByBusiness(id)}
                _reviews.value = ResponseResult.Success(result)
            } catch (e: Exception) {
                _reviews.value = ResponseResult.Error(e.toString())
            }
        }

    }

}