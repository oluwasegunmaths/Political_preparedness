package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val dataSource: ElectionDao) : ViewModel() {
    private val _allElections = MutableLiveData<List<Election>>()

    val allElections: LiveData<List<Election>>
        get() = _allElections
    private val _navigateToVotingInfo = MutableLiveData<Pair<Int, Division>>()

    val navigateToVotingInfo: LiveData<Pair<Int, Division>>
        get() = _navigateToVotingInfo

    val savedElections = dataSource.getAllNights()
//            MutableLiveData<List<Election>>()

    //    val savedElections: LiveData<List<Election>>
//        get() = _savedElections
    init {
        queryAPI()
    }

    private fun queryAPI() {
        viewModelScope.launch {
            Log.i("aaaaaaa", "1")
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred = CivicsApi.retrofitService.getelectionsAsync()
            try {
                Log.i("aaaaaaa", "2")

                // this will run on a thread managed by Retrofit
                val listResult = getPropertiesDeferred.await()
                if (listResult.elections.size == 0) {
                    Log.i("aaaaaaa", "3")

                }
                _allElections.value = listResult.elections
            } catch (e: Exception) {
                e.message?.let {
                    Log.i("aaaaaaa", e.message!!)

                }

            }
        }

    }


    fun navigateToVoterInfo(electionId: Int, division: Division) {
        _navigateToVotingInfo.value = Pair(electionId, division)
    }

    fun navigatedToVotingInfo() {
        _navigateToVotingInfo.value = null
    }


    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}