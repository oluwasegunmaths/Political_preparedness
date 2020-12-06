package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.MainActivity.Companion.isInternetAvailable
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {


    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives
    val line1 = MutableLiveData<String?>()
    val line2 = MutableLiveData<String?>()
    val city = MutableLiveData<String?>()
    val zip = MutableLiveData<String?>()
    val address = MutableLiveData<Address?>()
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage


    fun setAddress(address: Address) {
        this.address.value = address
        queryApi(address.state)
    }

    private fun queryApi(queryAddress: String) {
        viewModelScope.launch {
            // Get the Deferred object for our Retrofit request
            if (isInternetAvailable()) {
                val getRepresentativesDeferred = CivicsApi.retrofitService.getRepresentativesAsync(queryAddress)
                try {
                    val (offices, officials) = getRepresentativesDeferred.await()
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                } catch (e: Exception) {

                }
            } else {
                _toastMessage.value = "No or poor network"
            }
        }

    }

    fun searchBasedOnAddress(s: String) {
        queryApi(s)

    }

    fun setViewsFromAddress(address: Address) {
        line1.value = address.line1
        line2.value = address.line2
        city.value = address.city
        zip.value = address.zip
        this.address.value = address

    }

    fun shownToast() {
        _toastMessage.value = null
    }
}
