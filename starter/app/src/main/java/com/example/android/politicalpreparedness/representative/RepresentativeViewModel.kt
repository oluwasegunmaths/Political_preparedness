package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    fun setAddress(address: Address) {
        this.address.value = address
        queryApi(address.state)
    }

    private fun queryApi(queryAddress: String) {
        viewModelScope.launch {
            Log.i("aaaaaaa", "1")
            // Get the Deferred object for our Retrofit request
            val getRepresentativesDeferred = CivicsApi.retrofitService.getRepresentativesAsync(queryAddress)
            try {
                Log.i("aaaaaaa", "2")

                val (offices, officials) = getRepresentativesDeferred.await()
                _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
            } catch (e: Exception) {
                e.message?.let {
                    Log.i("aaaaaaa", e.message!!)

                }

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

    }

    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
