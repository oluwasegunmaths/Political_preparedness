package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert()
    fun insert(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllNights(): LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * from election_table WHERE id = :key")
    fun get(key: Int): Election?

    //TODO: Add delete query
    @Delete()
    fun deleteElection(election: Election)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    fun clear()

}