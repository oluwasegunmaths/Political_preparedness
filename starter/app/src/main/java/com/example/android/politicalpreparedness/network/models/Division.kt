package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)

@Parcelize
data class Division(
        val id: String,
        val country: String,
        val state: String
) : Parcelable