package com.example.finderapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finderapp.core.ResponseResult
import com.example.finderapp.data.model.BusinessesList
import com.example.finderapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _businessList = MutableLiveData<ResponseResult<BusinessesList>>()
    val businessesList: LiveData<ResponseResult<BusinessesList>> = _businessList

    fun getBusinessByKeyword(latitude: Double, longitude: Double, term: String) {
        viewModelScope.launch {
            try {
                _businessList.value = ResponseResult.Loading()
                val resultList = withContext(Dispatchers.IO) {
                    repository.getBusinessByKeyword(latitude, longitude, term)
                }
                _businessList.value = ResponseResult.Success(resultList)
            } catch (e: Exception) {
                _businessList.value = ResponseResult.Error(e.toString())
            }
        }
    }

}