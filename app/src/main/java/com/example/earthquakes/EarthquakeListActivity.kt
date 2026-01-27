package com.example.earthquakes

import RetrofitHelper
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarthquakeListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EarthquakeList"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_earthquake_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val earthquakeService = RetrofitHelper.getInstance().create(EarthquakeService::class.java)
        val earthquakeCall = earthquakeService.getEarthquakeDataPastDay()
        earthquakeCall.enqueue(object: Callback<FeatureCollection> {
            override fun onResponse(
                call: Call<FeatureCollection?>,
                response: Response<FeatureCollection?>
            ) {
                val featureCollection = response.body()
                Log.d(TAG, "onResponse: $featureCollection")
                // check if the underlying array isn't null, then put it into your recyclerview adapter
                // anything that expects you to have featureCollection data has to be here
            }

            override fun onFailure(
                call: Call<FeatureCollection?>,
                t: Throwable
            ) {
                // log your failure error message
                Log.d(TAG, "onFailure: ${t.message}")
            }
        } )

        // if you try to use featureCollection data out here, it might not exist
    }
}