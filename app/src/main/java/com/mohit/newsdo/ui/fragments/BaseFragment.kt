package com.mohitsharma.virtualnews.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mohit.newsdo.ui.MainActivity
import com.mohit.newsdo.ui.NewsViewModel

abstract class BaseFragment(layout: Int) : Fragment(layout) {

    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

}