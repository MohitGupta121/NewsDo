package com.mohit.newsdo.database

import androidx.room.TypeConverter
import com.mohit.newsdo.model.Source

class Converters {

    @TypeConverter
    fun fromSource(s:Source):String = s.name

    @TypeConverter
    fun toSource(name:String):Source = Source(name,name)
}