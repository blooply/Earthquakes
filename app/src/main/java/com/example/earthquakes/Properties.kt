package com.example.earthquakes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Properties (
    val mag: Double,
    val title: String,
    val place: String,
    val time: Long,
    val url: String
) : Parcelable