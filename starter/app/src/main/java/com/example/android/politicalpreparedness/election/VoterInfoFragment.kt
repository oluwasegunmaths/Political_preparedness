package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding


class VoterInfoFragment : Fragment() {
    lateinit var binding: FragmentVoterInfoBinding
    lateinit var viewModel: VoterInfoViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_voter_info, container, false)
        val dataSource = ElectionDatabase.getInstance(requireActivity().application).electionDao
        val electionid = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision


        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionid, division)

        viewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this
        viewModel.election.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.button.text = getString(R.string.follow)

            } else {
                binding.button.text = getString(R.string.unfollow)

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
        viewModel.toastMessage.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.shownToast()
            }
        })
        return binding.root
    }
}