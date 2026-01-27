package com.example.earthquakes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Metadata (
    val title: String,
    val count: Int,
    val status: Int
) : Parcelable