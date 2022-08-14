package com.mohit.newsdo.util.countryPicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohit.newsdo.R
import com.mukesh.countrypicker.Country
import kotlinx.android.synthetic.main.country_item_layout.view.*

class CountryPickerAdapter(private val countryList:ArrayList<Country>) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    private var onCountrySelectedListener :((Country) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.country_item_layout,parent,false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val country = countryList[position]
        holder.itemView.apply {
            iv_country_flag.setImageResource(country.flag)
            tv_country_name.text = country.name
            tv_country_name.tag = country.code
            setOnClickListener {
               onCountrySelectedListener?.let {
                   it(country)
               }
            }
        }
    }

    fun setOnCountrySelectedListener(listener:(Country) -> Unit ){
        onCountrySelectedListener =listener
    }

    override fun getItemCount(): Int  = countryList.size
}