package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    lateinit var viewModel: ElectionsViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_election, container, false)
        val dataSource = ElectionDatabase.getInstance(requireActivity().application).electionDao

        val viewModelFactory = ElectionsViewModelFactory(dataSource)

        viewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        val adapterSavedElections = ElectionListAdapter(ElectionListener { id, division ->
            if (division.state.isNotEmpty()) {
                viewModel.navigateToVoterInfo(id, division)
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_state), Toast.LENGTH_SHORT).show()
            }

        })
        binding.recyclerSavedElections.adapter = adapterSavedElections
        binding.recyclerSavedElections.layoutManager = LinearLayoutManager(requireContext())

        viewModel.savedElections.observe(viewLifecycleOwner, {
            it?.let {
                adapterSavedElections.submitList(it)
            }
        })
        binding.recyclerAllElections.adapter = ElectionListAdapter(ElectionListener { id, division ->
            if (division.state.isNotEmpty()) {
                viewModel.navigateToVoterInfo(id, division)
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_state), Toast.LENGTH_SHORT).show()
            }
        })
        binding.recyclerAllElections.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allElections.observe(viewLifecycleOwner, {

            it?.let {

                val adapter = binding.recyclerAllElections.adapter as ElectionListAdapter
                adapter.submitList(it)
            }
        })
        viewModel.navigateToVotingInfo.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.first, it.second))
                viewModel.navigatedToVotingInfo()
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