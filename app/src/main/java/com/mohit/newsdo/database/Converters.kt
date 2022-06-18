package com.mohitsharma.virtualnews.database

import androidx.room.TypeConverter
import com.mohitsharma.virtualnews.model.Source

class Converters {

    @TypeConverter
    fun fromSource(s:Source):String = s.name

    @TypeConverter
    fun toSource(name:String):Source = Source(name,name)
}