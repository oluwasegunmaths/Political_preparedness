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
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val dataSource: ElectionDao,
                         private val electionId: Int, private val division: Division) : ViewModel() {
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo
    private val _election = MutableLiveData<Election?>()
    val election: LiveData<Election?>
        get() = _election
    private val _ballotUrlString = MutableLiveData<String?>()
    val ballotUrlString: LiveData<String?>
        get() = _ballotUrlString
    private val _votingUrlString = MutableLiveData<String?>()
    val votingUrlString: LiveData<String?>
        get() = _votingUrlString
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _election.postValue(dataSource.get(electionId))
            }
        }
        queryAPI()
    }

    private fun queryAPI() {
        viewModelScope.launch {
            // Get the Deferred object for our Retrofit request
            if (isInternetAvailable()) {
                val getPropertiesDeferred = CivicsApi.retrofitService.getVoterInfoAsync(division.state, electionId)
                try {
                    // this will run on a thread managed by Retrofit
                    _voterInfo.value = getPropertiesDeferred.await()
                } catch (e: Exception) {
                }
            } else {
                _toastMessage.value = "No or poor network"

            }
        }

    }

    fun openBallotInformation() {
        _ballotUrlString.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl

    }

    fun openVotingLocations() {
        _ballotUrlString.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl

    }

    fun openedBallotInformation() {
        _ballotUrlString.value = null

    }

    fun openedVotingLocations() {
        _votingUrlString.value = null

    }

    fun followOrUnfollow() {
        viewModelScope.launch {

            if (_election.value == null) {
                _voterInfo.value?.let {
                    withContext(Dispatchers.IO) {
                        dataSource.insert(_voterInfo.value!!.election)
                    }
                    _election.value = _voterInfo.value!!.election
                }

            } else {
                withContext(Dispatchers.IO) {
                    dataSource.deleteElection(_election.value!!)
                }
                _election.value = null
            }
        }
    }

    fun shownToast() {
        _toastMessage.value = null
    }
}
