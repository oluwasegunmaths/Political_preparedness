package com.example.android.politicalpreparedness.network.jsonadapter

import android.annotation.SuppressLint
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ElectionAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter, "")
                .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }

    @SuppressLint("SimpleDateFormat")
    @FromJson
    fun dateFromJson(dateString: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(dateString)
    }

    @SuppressLint("SimpleDateFormat")
    @ToJson
    fun dateToJson(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(date)
    }
}