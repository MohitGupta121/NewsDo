package com.mohit.newsdo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.mohit.newsdo.R
import com.mohit.newsdo.adapters.HomeRecyclerViewAdapter
import com.mohit.newsdo.util.DepthPageTransformer
import com.mohit.newsdo.util.Resources
import com.mohit.newsdo.util.hide
import com.mohit.newsdo.util.show
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    lateinit var adapter: HomeRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomeRecyclerViewAdapter(viewModel)
        setUpVIewPager()
        observeBreakingNews()

        btn_retry.setOnClickListener {
            viewModel.getBreakingNews("in")
        }

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
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
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