package com.example.earthquakes

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class EarthquakeMapActivity : AppCompatActivity() {
    private lateinit var map : MapView

    companion object {
        const val EXTRA_EARTHQUAKE = "earthquake"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_earthquake_map)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val earthquake = intent.getParcelableExtra<Feature>(EXTRA_EARTHQUAKE)
        if (earthquake == null) {
            finish()
            return
        }

        val tvTitle = findViewById<TextView>(R.id.textView_earthquake_title)
        val tvInfo = findViewById<TextView>(R.id.textView_earthquake_info)
        val tvUrl = findViewById<TextView>(R.id.textView_earthquake_url)

        val mag = DecimalFormat("#.#").format(earthquake.properties.mag)

        val instant = Instant.ofEpochMilli(earthquake.properties.time)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a").withZone(ZoneId.systemDefault())
        val date = formatter.format(instant)

        tvTitle.text = "Details"

        tvInfo.text = "Magnitude $mag  -  ${earthquake.properties.place}\n${date}"

        tvUrl.text = "${earthquake.properties.url}"

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        val point = GeoPoint(earthquake.geometry.coordinates[1], earthquake.geometry.coordinates[0])

        map.controller.setZoom(7.0)
        map.controller.setCenter(point)

        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        marker.title = "Magnitude $mag"
        marker.snippet = "${earthquake.properties.place}\n${date}"

        map.overlays.add(marker)
        map.invalidate()
    }


    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}