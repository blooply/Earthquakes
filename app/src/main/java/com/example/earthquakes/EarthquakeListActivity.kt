package com.example.earthquakes

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthquakes.databinding.ActivityEarthquakeListBinding


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
                val data = response.body()?.features ?: return

                filteredData = data.filter { (it.properties.mag ?: 0.0) >= 1.0}

                adapter = EarthquakeAdapter(filteredData)

                binding.recyclerViewEarthquakeList.adapter = adapter
                binding.recyclerViewEarthquakeList.layoutManager = LinearLayoutManager(this@EarthquakeListActivity)
            }

            override fun onFailure(call: Call<FeatureCollection?>, t: Throwable
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
        when (item.itemId) {

            R.id.item_listOptions_sortRecent -> {
                binding.recyclerViewEarthquakeList.adapter =
                    EarthquakeAdapter(filteredData.sortedByDescending { it.properties.time })
            }

            R.id.item_listOptions_sortMag -> {
                binding.recyclerViewEarthquakeList.adapter =
                    EarthquakeAdapter(filteredData.sortedWith(compareByDescending<Feature> {
                        it.properties.mag ?: 0.0
                    }.thenByDescending { it.properties.time }))
            }

            R.id.item_listOptions_help -> {
                val text =
                    "Magnitude Colors:\n\n" +
                    "≥6.5 — Purple\n" +
                    "4.5–6.5 — Red\n" +
                    "2.5–4.5 — Orange\n" +
                    "1.0–2.5 — Blue"

                val spannable = SpannableString(text)

                fun colorWord(word: String, colorRes: Int) {
                    val start = text.indexOf(word)
                    val end = start + word.length
                    spannable.setSpan(
                        ForegroundColorSpan(getColor(colorRes)),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                colorWord("Purple", R.color.purple)
                colorWord("Red", R.color.red)
                colorWord("Orange", R.color.orange)
                colorWord("Blue", R.color.blue)

                AlertDialog.Builder(this).setMessage(spannable).setPositiveButton("OK", null).show()
            }

            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}