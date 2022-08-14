package com.mohit.newsdo.util.countryPicker

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mohit.newsdo.R
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker

class CustomCountryPicker(val context: Context) {
  private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var adapter: CountryPickerAdapter
   private val builder = CountryPicker.Builder().with(context).build()

    private val COUNTRIES = listOf<String>(
        "ae" , "ar", "at" ,"au", "be" ,"bg", "br", "ca", "ch", "cn", "co", "cu", "cz", "de", "eg", "fr",
        "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr","lt" , "lv", "ma", "mx", "my",
        "ng", "nl", "no", "nz", "ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th",
        "tr", "tw", "ua", "us", "ve", "za")

    fun attach(bottomSheet: LinearLayout): CustomCountryPicker {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        setUpRecView(bottomSheet)
        return this
    }

     fun show(): CustomCountryPicker {
         bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
         return this
     }

    fun dismiss(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun getCountryByCode(code:String):String = builder.getCountryByISO(code).name


     private fun setUpRecView(bottomSheet: LinearLayout){
         adapter = CountryPickerAdapter(getCountryList())
        val recView = bottomSheet.findViewById<RecyclerView>(R.id.country_picker_rec_view)
        recView.layoutManager = LinearLayoutManager(context)
        recView.adapter = adapter

    }


    private fun getCountryList() : ArrayList<Country>{
        val list = ArrayList<Country>()

        for(code in COUNTRIES){
            val iso =  builder.getCountryByISO(code)
            list.add(iso)
        }
        return list
    }
}