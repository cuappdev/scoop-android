package com.cornellappdev.scoop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.scoop.data.repositories.LoginRepository
import com.cornellappdev.scoop.data.repositories.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val rideRepository: RideRepository,
) : ViewModel() {

    val rideFlow = rideRepository.rideFlow

    fun getRides() {
        viewModelScope.launch {
            rideRepository.getAllRides()
        }
    }

}