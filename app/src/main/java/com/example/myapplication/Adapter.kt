package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class WeatherListAdapter(val weatherList: List<Wsss>)
    :RecyclerView.Adapter<WeatherListAdapter.ViewHolder>() {

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        var image:ImageView
        var place:TextView
        var temp:TextView
        var humi:TextView
        var rain:TextView

        init {
            image=view.findViewById(R.id.weatherImage)
            place=view.findViewById(R.id.place)
            temp=view.findViewById(R.id.T1H)
            humi=view.findViewById(R.id.REH)
            rain=view.findViewById(R.id.RN1)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val v=LayoutInflater.from(parent.context).inflate(R.layout.activity_main2,parent,false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.place.text=weatherList[position].place
        holder.image.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
        holder.temp.text=weatherList[position].x
        holder.humi.text=weatherList[position].y
    }

    override fun getItemCount(): Int {
        return  weatherList.size
    }

    }
