package com.mohit.newsdo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.mohit.newsdo.R
import com.mohit.newsdo.adapters.HomeRecAdapter
import com.mohit.newsdo.util.*
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : BaseFragment(R.layout.home_fragment) {
    lateinit var adapter: HomeRecAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomeRecAdapter(viewModel)
        setUpVIewPager()
        observeBreakingNews()

        btn_retry.setOnClickListener {
             viewModel.getBreakingNews("in")
        }

//        viewModel.currentCountryLiveData.observe(viewLifecycleOwner, Observer {
            adapter.differ.currentList.apply {
                if (this.isEmpty()) {
                    viewModel.getBreakingNews("in")
                }
                if (!this.containsAll(this))
                    viewModel.getBreakingNews("in")
            }
            viewModel.currentCountry = "in"
//        })

    }

    private fun observeBreakingNews() {
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resources.Loading -> {
                    retry_view.hide()
                    btn_retry.hide()
                    progress_bar.show()
                }
                is Resources.Success -> {
                    progress_bar.hide()
                    btn_retry.hide()
                    retry_view.hide()
                    it.data?.let { newsResponse ->
                        adapter.differ.submitList(newsResponse.articles)
                        adapter.notifyDataSetChanged()
                    }
                }
                is Resources.Error -> {
                    progress_bar.hide()
                    requireContext().toast("Something went wrong!")
                    btn_retry.show()
                    retry_view.show()
                }
                else -> {
                    progress_bar.show()
                }
            }
        })
    }

    private fun setUpVIewPager() {
        view_pager.adapter = adapter
        view_pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        view_pager.currentItem = viewModel.currentNewsPosition
        view_pager.setPageTransformer(DepthPageTransformer())
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem: Int = view_pager.currentItem + 1
                viewModel.currentNewsPosition = currentItem
                val lastItem: Int = adapter.itemCount
                if (currentItem == lastItem) {
                    try {
                        viewModel.getBreakingNews(viewModel.currentCountry)
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                super.onPageSelected(position)
            }
        })
    }

    override fun onResume() {
        view_pager.let {
            it.setCurrentItem(viewModel.currentNewsPosition - 1)
        }
        super.onResume()
    }

}