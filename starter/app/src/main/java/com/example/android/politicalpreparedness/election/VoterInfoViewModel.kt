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

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
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
            Log.i("aaaaaaa", "1")
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred = CivicsApi.retrofitService.getVoterInfoAsync(division.state, electionId)
            try {
                Log.i("aaaaaaa", "2")

                // this will run on a thread managed by Retrofit
                _voterInfo.value = getPropertiesDeferred.await()
//                if(listResult.elections.size==0){
//                    Log.i("aaaaaaa","3")
//
//                }
//                _allElections.value = listResult.elections
            } catch (e: Exception) {
                e.message?.let {
                    Log.i("aaaaaaa", e.message!!)

                }

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

}
