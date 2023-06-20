package com.example.finderapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finderapp.core.ResponseResult
import com.example.finderapp.data.model.Route
import com.example.finderapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BusinessMapViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _route = MutableLiveData<ResponseResult<Route>>()
    val route: LiveData<ResponseResult<Route>> = _route

    fun getRouteByLocation(start: String, end: String){
        viewModelScope.launch {
            try {
                _route.value = ResponseResult.Loading()
                val resultRoute = withContext(Dispatchers.IO){repository.getRouteByLocation(start, end)}
                _route.value = ResponseResult.Success(resultRoute)
            }catch (e: Exception){
                _route.value = ResponseResult.Error(e.toString())
            }
        }
    }

}