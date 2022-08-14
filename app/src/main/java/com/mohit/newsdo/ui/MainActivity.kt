package com.mohit.newsdo.ui

import am.appwise.components.ni.NoInternetDialog
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mohit.newsdo.R
import com.mohit.newsdo.database.ArticleDatabase
import com.mohit.newsdo.repository.NewsRepository
import com.mohit.newsdo.util.countryPicker.CustomCountryPicker
import com.mohit.newsdo.util.setOnItemReselectedListener
import com.mohit.newsdo.util.slideDown
import com.mohit.newsdo.util.slideUp
import com.mohit.newsdo.util.swipeDetector.SwipeActions
import com.mohit.newsdo.util.swipeDetector.SwipeGestureDetector
import github.com.st235.lib_expandablebottombar.navigation.ExpandableBottomBarNavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.country_picker_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var newsRepository: NewsRepository
    private lateinit var countryPicker: CustomCountryPicker
    lateinit var noInternetDialog: NoInternetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NewsDo)
        newsRepository = NewsRepository(this, ArticleDatabase(this))
        newsRepository.getDataStore().readUiModeFromDataStore.asLiveData()
            .observe(this, Observer { isDarkMode ->
                nightModeButton.isChecked = isDarkMode
            })


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        countryPicker = CustomCountryPicker(this).attach(country_picker_bottom_sheet)

        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        setUpBottomSheet()
        detectSwipeGestures()

        noInternetDialog = noInternetAlert()

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

    private fun detectSwipeGestures() {
        val swipeGestureDetector = SwipeGestureDetector(object : SwipeActions {
            override fun onSwipeLeft() {}

            override fun onSwipeUp() {
                if (bottom_bar.isVisible) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                else bottom_bar.slideUp()
            }

            override fun onSwipeDown() {
                bottom_bar.slideDown()
            }
        })

        val gestureDetectorCompat = GestureDetectorCompat(applicationContext, swipeGestureDetector)
        btn_swipe_up.setOnTouchListener { view, motionEvent ->
            gestureDetectorCompat.onTouchEvent(motionEvent)
            view.performClick()
            true
        }
    }

    private fun restartActivity() {
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(Intent(applicationContext, MainActivity::class.java), options.toBundle())
        finish()
    }

    private fun isDarkMode(isDarkMode: Boolean) = GlobalScope.launch(Dispatchers.IO) {
        newsRepository.getDataStore().saveUiMode(isDarkMode)
    }

    private fun saveCurrentCountry(country: String) = GlobalScope.launch(Dispatchers.IO) {
        newsRepository.getDataStore().saveToDataStore(country)
    }


    private fun setUpBottomSheet() {
        viewModel.currentCountryLiveData.observe(this, Observer {
            tv_selectedCountry.text = countryPicker.getCountryByCode(it)
            viewModel.currentCountry = it
        })

        enable_dark_mode.setOnClickListener {
            nightModeButton.performClick()
        }

        nightModeButton.setOnCheckedChangeListener { _, isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                isDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                isDarkMode(false)
            }
        }

        choose_country.setOnClickListener {
            countryPicker.show()
            countryPicker.adapter.setOnCountrySelectedListener {
                tv_selectedCountry.apply {
                    text = it.name
                    tag = it.name
                }
                saveCurrentCountry(it.code)
                countryPicker.dismiss()
                restartActivity()

            }
        }

        feedback.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "mohitsharma.2cse23@jecrc.ac.in", null)
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            startActivity(Intent.createChooser(emailIntent, "Feedback"))
        }

    }

    private fun noInternetAlert() = NoInternetDialog.Builder(this)
        .setCancelable(false)
        .setDialogRadius(50f)
        .setBgGradientCenter(resources.getColor(R.color.light_blue))
        .setBgGradientStart(resources.getColor(R.color.light_blue))
        .setBgGradientEnd(resources.getColor(R.color.light_blue))
        .setButtonColor(resources.getColor(R.color.white))
        .setButtonIconsColor(resources.getColor(R.color.light_blue))
        .setButtonTextColor(resources.getColor(R.color.black))
        .setWifiLoaderColor(resources.getColor(R.color.light_blue))
        .build()


    override fun onDestroy() {
        super.onDestroy()
        noInternetDialog.onDestroy()
    }


    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            countryPicker.dismiss()
        } else {
            super.onBackPressed()
        }

    }

}