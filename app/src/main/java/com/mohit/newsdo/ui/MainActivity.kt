package com.mohit.newsdo.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mohit.newsdo.R
import com.mohit.newsdo.database.ArticleDatabase
import com.mohit.newsdo.repository.NewsRepository
import com.mohit.newsdo.util.setOnItemReselectedListener
import github.com.st235.lib_expandablebottombar.navigation.ExpandableBottomBarNavigationUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var newsRepository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NewsDo)
        newsRepository = NewsRepository(this, ArticleDatabase(this))


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        val navController = findNavController(R.id.nav_host_fragment)
        ExpandableBottomBarNavigationUI.setupWithNavController(
            bottom_bar,
            navController
        )
        bottom_bar.setOnItemReselectedListener { view, menuItem ->
            viewModel.currentNewsPosition = 0
            navController.navigate(menuItem.itemId)
        }

    }
}