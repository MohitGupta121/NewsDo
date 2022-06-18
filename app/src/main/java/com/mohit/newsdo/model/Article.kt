package com.mohit.newsdo.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
        tableName = "articles",
        indices = [Index(value = ["title", "publishedAt"],
        unique = true)]
)
data class Article(
        @PrimaryKey(autoGenerate = true)
        var id : Int? = null,
        var author: String,
        val content: String?,
        val description: String,
        var publishedAt: String,
        val source: Source,
        val title: String,
        val url: String,
        val urlToImage: String
)