package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {
    init {
        Log.i("aaaaaaa", "init")

    }

//    override fun submitList(list: MutableList<Election>?) {
//        Log.i("aaaaaaa","submit")
//
//        super.submitList(list)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        Log.i("aaaaaaa", "create")
        return ElectionViewHolder(ListItemElectionBinding.inflate(LayoutInflater.from(parent.context)))

//        return ElectionViewHolder.from(parent)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        Log.i("aaaaaaa", "bindView")

        val election = getItem(position)
        holder.bind(clickListener, election)
    }


    //TODO: Add companion object to inflate ViewHolder (from)

}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(var binding: ListItemElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
//    companion object{
//        fun from( parent: ViewGroup): ElectionViewHolder {
//            val layoutInflater = LayoutInflater.from(parent.context)
//            val binding = ListItemElectionBinding.inflate(layoutInflater, parent, false)
//            return ElectionViewHolder(binding)
//
//        }
//    }

    fun bind(clickListener: ElectionListener, election: Election) {
        Log.i("aaaaaaa", "bind")

        binding.election = election
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id

    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem

    }
}

//TODO: Create ElectionListener
class ElectionListener(val clickListener: (id: Int, division: Division) -> Unit) {
    fun onClick(election: Election) = clickListener(election.id, election.division)
}