package com.example.earthquakes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class FeatureCollection (
    val type: String,
    val metadata: Metadata,
    val features: List<Feature>
) : Parcelable