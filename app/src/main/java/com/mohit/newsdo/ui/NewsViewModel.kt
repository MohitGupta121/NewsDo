package com.mohit.newsdo.ui

import androidx.lifecycle.*
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.model.NewsResponse
import com.mohit.newsdo.repository.NewsRepository
import com.mohit.newsdo.util.Resources
import com.mohit.newsdo.util.TopBarState
import com.mohit.newsdo.util.filterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val breakingNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    private var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchTopBarState: MutableLiveData<TopBarState> = MutableLiveData()
    val savedTopBarState: MutableLiveData<TopBarState> = MutableLiveData()

    val categoryNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var categoryNewsPage = 1

    var savedNewsLiveData: LiveData<List<Article>>
    var currentNewsPosition = 0
    val currentCountryLiveData =  newsRepository.getDataStore().readCountryFromDataStore.asLiveData()

    var currentCountry ="in"

    init {

        savedNewsLiveData = getSavedNews()
        savedTopBarState.postValue(TopBarState.NormalState())
    }

    fun deleteAllArticle() = viewModelScope.launch {
        newsRepository.deleteAllArticles()
    }

    fun deleteSelected(list: MutableList<Article>) {
        for (article in list) {
            deleteArticle(article)
        }
    }


    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.saveArticle(article)
    }
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteAricle(article)
    }

    suspend fun isArticleSaved(article: Article): Boolean {
        var a: Article? = null
        val job = viewModelScope.launch(Dispatchers.IO) {
            a = newsRepository.isArticleSaved(article)
        }
        job.join()
        return a != null
    }

    fun getNewsByCategory(category:String,countryCode: String=currentCountry) = viewModelScope.launch {
        categoryNews.postValue(Resources.Loading())
        try {
            val response = newsRepository.getCategoryNews(countryCode,categoryNewsPage, category = category)
            categoryNews.postValue(handleCategoryNewsResponse(response))
        }catch (e:Exception){
            e.printStackTrace()
            categoryNews.postValue(Resources.Error("Error"))
        }

    }

    fun getBreakingNews(countryCode: String = currentCountry) = viewModelScope.launch {
        breakingNews.postValue(Resources.Loading())
        try {
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }catch (e:Exception){
            e.printStackTrace()
            breakingNews.postValue(Resources.Error("Error"))
        }

    }

    fun getSearchNews(query: String) = viewModelScope.launch {
        searchNews.postValue(Resources.Loading())
        try {
            val response = newsRepository.searchNews(query, searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(response))
        }catch (e:Exception){
            e.printStackTrace()
            searchNews.postValue(Resources.Error("Error"))
        }

    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                val newsResponse = breakingNewsResponse ?: resultResponse
                return Resources.Success(newsResponse.filterResponse())
            }
        }
        return Resources.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return if (resultResponse.articles.isNotEmpty())
                    Resources.Success(resultResponse.filterResponse())
                else
                    Resources.Error(response.message())
            }
        }
        return Resources.Error(response.message())
    }
    private fun handleCategoryNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return if (resultResponse.articles.isNotEmpty())
                    Resources.Success(resultResponse.filterResponse())
                else
                    Resources.Error(response.message())
            }
        }
        return Resources.Error(response.message())
    }

    private fun getSavedNews() = newsRepository.getSavedNews()


}

