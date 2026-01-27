package com.example.earthquakes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Geometry (
    val coordinates: List<Double>
) : Parcelable