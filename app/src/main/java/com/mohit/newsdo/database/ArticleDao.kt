package com.mohit.newsdo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mohit.newsdo.model.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(article: Article):Long

    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAllArticles():LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE title= :q")
    suspend fun getArticleByTitle( q: String): Article

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticle()


}