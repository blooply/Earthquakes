package com.example.earthquake

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.earthquake.databinding.ActivityEarthquakeListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager


class EarthquakeListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EarthquakeList"
    }

    private lateinit var binding: ActivityEarthquakeListBinding
    private lateinit var adapter: EarthquakeAdapter
    private lateinit var filteredData: List<Feature>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEarthquakeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                //
                //val featureCollection = response.body()

                //Log.d("EarthquakeList", "Count: ${featureCollection?.metadata?.count}")
                //Log.d("EarthquakeList", "First Quake: ${featureCollection?.features?.get(0)?.properties?.title}")


                val data = response.body()?.features ?: return

                filteredData = data.filter { (it.properties.mag ?: 0.0) >= 1.0}

                adapter = EarthquakeAdapter(filteredData)

                binding.recyclerViewEarthquakeList.adapter = adapter
                binding.recyclerViewEarthquakeList.layoutManager = LinearLayoutManager(this@EarthquakeListActivity)


                // check if the underlying array isn't null, and if it isn't,
                // put it into your recyclerView adapter
                // anything that expects you to have featureCollection data has to be here
            }

            override fun onFailure(
                call: Call<FeatureCollection?>,
                t: Throwable
            ) {
                Log.d("EarthquakeList", "onFailure: ${t.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_earthquake, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.

        when (item.itemId) {
            R.id.item_listOptions_sortRecent -> {
                binding.recyclerViewEarthquakeList.adapter = EarthquakeAdapter(filteredData.sortedByDescending { it.properties.time })
            }
            R.id.item_listOptions_sortMag -> {
                binding.recyclerViewEarthquakeList.adapter = EarthquakeAdapter(filteredData.sortedWith(compareByDescending<Feature> {it.properties.mag ?: 0.0}.thenByDescending {it.properties.time}))
            }
            R.id.item_listOptions_help -> {
                AlertDialog.Builder(this).setMessage("Purple: Significant (> 6.5)\nRed: Large (4.5 - 6.5)\nOrange: Moderate (2.5 - 4.5)\nBlue: Small (1.0 - 2.5)\n\nThe number represents the magnitude.").setPositiveButton("OK", null).show()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}