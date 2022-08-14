package com.mohit.newsdo.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.mohit.newsdo.R
import com.mohit.newsdo.adapters.SearchRecAdapter
import com.mohit.newsdo.util.*
import com.mohitsharma.virtualnews.ui.fragments.BaseFragment
import com.mohit.newsdo.util.Constants.SEARCH_DELAY_TIME
import com.mohit.newsdo.util.swipeDetector.ItemTouchHelperCallback
import com.mohit.newsdo.util.swipeDetector.RecyclerViewSwipe
import kotlinx.android.synthetic.main.categories_layout.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.ib_clear_selection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.format

class SearchFragment : BaseFragment(R.layout.search_fragment) {

    lateinit var searchAdapter: SearchRecAdapter
    lateinit var categoryAdapter: SearchRecAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchRecAdapter()
        search_rec_view.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(getItemTouchHelperCallBack(searchAdapter)).attachToRecyclerView(this)
        }
        categoryAdapter = SearchRecAdapter()
        category_rec_view.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            ItemTouchHelper(getItemTouchHelperCallBack(categoryAdapter)).attachToRecyclerView(this)
        }

        observeSearchNews()
        handleBackPress()
        observeCategoryNews()
        observeTopBar()

        btn_search.setOnClickListener {
            viewModel.searchTopBarState.postValue(TopBarState.SearchState())
            YoYo.with(Techniques.FadeIn)
                .duration(500)
                .playOn(search_edit_layout)
            search_edit_text.showKeyboard()
        }


        search_edit_text.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(search_edit_text.text.toString())

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        business_card.setOnClickListener {
            activateCategoryState(Constants.BUSINESS)
        }
        entertainment_card.setOnClickListener {
            activateCategoryState(Constants.ENTERTAINMENT)
        }
        health_card.setOnClickListener {
            activateCategoryState(Constants.HEALTH)
        }
        science_card.setOnClickListener {
            activateCategoryState(Constants.SCIENCE)
        }
        sports_card.setOnClickListener {
            activateCategoryState(Constants.SPORTS)
        }
        technology_card.setOnClickListener {
            activateCategoryState(Constants.TECHNOLOGY)
        }

        ib_clear_selection.setOnClickListener {
            viewModel.searchTopBarState.postValue(TopBarState.NormalState())
        }

    }

    private fun performSearch(query: String) = MainScope().launch {
        delay(SEARCH_DELAY_TIME)
        query.let {
            if (it.isNotEmpty()) {
                viewModel.getSearchNews(it.trim())
                viewModel.searchTopBarState.postValue(TopBarState.SearchState(it.trim()))
                search_edit_text.hideKeyboard()
            }
        }
    }


    private fun activateCategoryState(category: String) {
        viewModel.getNewsByCategory(category)
        viewModel.searchTopBarState.postValue(TopBarState.CategoryState(category.format()))
    }

    private fun observeTopBar() {
        viewModel.searchTopBarState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is TopBarState.SearchState -> {
                    ib_clear_selection.hide()

                    if (state.query != null) {
                        state.query.let {
                            search_edit_layout.requestFocus()
                            search_edit_layout.placeholderText = it
                        }
                    }
                    search_top_bar.hide()
                    search_edit_layout.show()
                    search_rec_view.show()
                }
                is TopBarState.CategoryState -> {
                    state.category.let {
                        ib_clear_selection.show()
                        top_bar_title.text = it
                    }
                }
                is TopBarState.NormalState -> {
                    search_edit_layout.hide()
                    ib_clear_selection.hide()
                    top_bar_title.text = Constants.CATEGORY
                    search_rec_view.hide()
                    category_grid_view.show()
                    category_rec_view.hide()
                    search_top_bar.show()
                }
            }
        })
    }


    private fun observeSearchNews() {
        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resources.Success -> {
                    it.data?.let { newsResponse ->
                        searchAdapter.searchDiffer.submitList(newsResponse.articles)
                        progress_bar.hide()
                        category_grid_view.hide()
                    }
                }
                is Resources.Loading -> {
                    progress_bar.show()
                }
                is Resources.Error -> {
                    progress_bar.hide()
                    requireContext().toast("No Result Found!")
                    viewModel.searchTopBarState.postValue(TopBarState.NormalState())
                }
                else -> {
                }
            }
        })
    }

    private fun observeCategoryNews() {
        viewModel.categoryNews.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resources.Success -> {
                    it.data?.let { newsResponse ->
                        categoryAdapter.searchDiffer.submitList(newsResponse.articles)
                        progress_bar.hide()
                        category_rec_view.show()
                        category_grid_view.hide()
                    }
                }
                is Resources.Loading -> {
                    progress_bar.show()
                }
                is Resources.Error -> {
                    progress_bar.hide()
                    requireContext().toast("Something went wrong!")
                    viewModel.searchTopBarState.postValue(TopBarState.NormalState())
                }
                else -> {
                }
            }
        })
    }


    private fun handleBackPress() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when (viewModel.searchTopBarState.value) {
                is TopBarState.SearchState -> {
                    search_edit_layout.hide()
                    search_rec_view.hide()
                    search_top_bar.show()
                    top_bar_title.text = Constants.CATEGORY
                    viewModel.searchTopBarState.postValue(TopBarState.NormalState())
                }
                is TopBarState.CategoryState -> {
                    search_rec_view.hide()
                    category_rec_view.hide()
                    top_bar_title.text = Constants.CATEGORY
                    viewModel.searchTopBarState.postValue(TopBarState.NormalState())
                }
                else -> {
                    viewModel.searchTopBarState.postValue(TopBarState.NormalState())
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun getItemTouchHelperCallBack(adapter: SearchRecAdapter) = ItemTouchHelperCallback(object :
        RecyclerViewSwipe {
        override fun onSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
            val position = viewHolder.adapterPosition
            val currentArticle = adapter.searchDiffer.currentList[position]
            viewModel.saveArticle(currentArticle)
            view?.let {
                Snackbar.make(it, "Saved", Snackbar.LENGTH_LONG).show()
            }
            adapter.notifyDataSetChanged()
        }

        override fun onSwipeRight(viewHolder: RecyclerView.ViewHolder) {
            val position = viewHolder.adapterPosition
            val currentArticle = adapter.searchDiffer.currentList[position]
          requireContext().share(currentArticle)
            adapter.notifyDataSetChanged()
        }

        override fun addSwipeLeftBackgroundColor(): Int = requireContext().getColor(R.color.light_blue)

        override fun addSwipeRightBackgroundColor(): Int = requireContext().getColor(R.color.transparent)

        override fun addSwipeLeftActionIcon(): Int = R.drawable.ic_baseline_bookmark_border_24

    })

}