package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.MainActivity.Companion.isInternetAvailable
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(dataSource: ElectionDao) : ViewModel() {
    private val _allElections = MutableLiveData<List<Election>>()

    val allElections: LiveData<List<Election>>
        get() = _allElections
    private val _navigateToVotingInfo = MutableLiveData<Pair<Int, Division>>()

    val navigateToVotingInfo: LiveData<Pair<Int, Division>>
        get() = _navigateToVotingInfo

    val savedElections = dataSource.getAllElections()
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    init {
        queryAPI()
    }

    private fun queryAPI() {
        viewModelScope.launch {
            // Get the Deferred object for our Retrofit request
            if (isInternetAvailable()) {
                val getPropertiesDeferred = CivicsApi.retrofitService.getelectionsAsync()
                try {
                    // this will run on a thread managed by Retrofit
                    val listResult = getPropertiesDeferred.await()
                    _allElections.value = listResult.elections
                } catch (e: Exception) {

                }
            } else {
                _toastMessage.value = "No or poor network"

            }
        }

    }


    fun navigateToVoterInfo(electionId: Int, division: Division) {
        _navigateToVotingInfo.value = Pair(electionId, division)
    }

    fun navigatedToVotingInfo() {
        _navigateToVotingInfo.value = null
    }

    fun shownToast() {
        _toastMessage.value = null
    }

}