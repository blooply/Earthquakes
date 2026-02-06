package com.example.earthquakes


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class EarthquakeAdapter(var eqList: List<Feature>) :
    RecyclerView.Adapter<EarthquakeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMag: TextView
        val tvLocation: TextView
        val tvTime: TextView
        val layout: ConstraintLayout

        init {
            tvMag = view.findViewById(R.id.textView_earthquakeItem_magnitude)
            tvLocation = view.findViewById(R.id.textView_earthquakeItem_location)
            tvTime = view.findViewById(R.id.textView_earthquakeItem_time)
            layout = view.findViewById(R.id.layout_earthquakeItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_earthquake, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val earthquake = eqList[position]

        val mag = earthquake.properties.mag
        val magFormat = DecimalFormat("#.##").format(mag)

        holder.tvMag.text = magFormat
        holder.tvLocation.text = earthquake.properties.place

        val instant = Instant.ofEpochMilli(earthquake.properties.time)
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a").withZone(ZoneId.systemDefault())
        //holder.tvTime.text = Date(earthquake.properties.time).toString()
        holder.tvTime.text = formatter.format(instant)

        val context = holder.layout.context

        when {
            mag > 6.5 -> {
                holder.tvMag.setTextColor(context.getColor(R.color.purple))
                holder.tvMag.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.error_purple,0,0,0)
            }
            mag >= 4.6 -> {
                holder.tvMag.setTextColor(context.getColor(R.color.red))
                holder.tvMag.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.error_red,0,0,0)
            }
            mag >= 2.5 -> {
                holder.tvMag.setTextColor(context.getColor(R.color.orange))
                holder.tvMag.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            }
            else -> {
                holder.tvMag.setTextColor(context.getColor(R.color.blue))
            }
        }

        holder.layout.setOnClickListener {
            val intent = Intent(context, EarthquakeMapActivity::class.java)
            intent.putExtra("earthquake", earthquake)
            context.startActivity(intent)
        }


    }

    override fun getItemCount() = eqList.size
}