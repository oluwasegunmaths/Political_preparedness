package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert
    fun insert(election: Election)

    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * from election_table WHERE id = :key")
    fun get(key: Int): Election?

    @Delete
    fun deleteElection(election: Election)

    @Query("DELETE FROM election_table")
    fun clear()

}