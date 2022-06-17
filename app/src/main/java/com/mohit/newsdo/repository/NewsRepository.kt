package com.mohit.newsdo.repository

import android.content.Context
import com.mohit.newsdo.api.RetrofitInstance

class NewsRepository (val context: Context){

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun getCategoryNews(countryCode: String, pageNumber: Int, category: String) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber,category = category)

    suspend fun searchNews(searchQuery:String,pageNumber:Int) =
            RetrofitInstance.api.searchNews(searchQuery,pageNumber)

}