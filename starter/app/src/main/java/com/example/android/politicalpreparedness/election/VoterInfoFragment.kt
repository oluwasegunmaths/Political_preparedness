package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding


class VoterInfoFragment : Fragment() {
    lateinit var binding: FragmentVoterInfoBinding
    lateinit var viewModel: VoterInfoViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_voter_info, container, false)
        val dataSource = ElectionDatabase.getInstance(requireActivity().application).electionDao
        val electionid = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision


        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionid, division)

        viewModel =
                ViewModelProviders.of(
                        this, viewModelFactory).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this
        viewModel.election.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.button.text = "Follow"

            } else {
                binding.button.text = "Unfollow"

            }
        })
        viewModel.votingUrlString.observe(viewLifecycleOwner, {
            it?.let {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(it)))
                viewModel.openedVotingLocations()
            }

        })
        viewModel.ballotUrlString.observe(viewLifecycleOwner, {
            it?.let {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(it)))
                viewModel.openedBallotInformation()
            }
        })
        return binding.root
    }

    //TODO: Create method to load URL intents

}