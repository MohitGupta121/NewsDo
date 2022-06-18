package com.mohit.newsdo.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.mohit.newsdo.R
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.ui.NewsViewModel
import com.mohit.newsdo.util.TopBarState
import com.mohit.newsdo.util.showArticleInWebView
import kotlinx.android.synthetic.main.saved_article.view.*

class SavedRecAdapter(private val viewModel: NewsViewModel) :
    RecyclerView.Adapter<SavedRecAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    var selectedItems = mutableListOf<Article>()

    private val savedDifferCallback: DiffUtil.ItemCallback<Article> =
        object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.description == newItem.description

        }
    val savedDiffer = AsyncListDiffer(this, savedDifferCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_article, parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = savedDiffer.currentList[position]
        holder.itemView.apply {
            tv_headline.text = article.title
            tv_date.text = article.publishedAt
            if (viewModel.savedTopBarState.value == TopBarState.NormalState()){
                item_saved_article.foreground = null
            }
            Glide.with(this)
                .load(article.urlToImage)
                .centerCrop()
                .signature(ObjectKey(article.title))
                .placeholder(R.drawable.photo_placeholder)
                .error(R.drawable.photo_placeholder)
                .into(iv_content_image)

            setOnLongClickListener {
                selectedItems.add(article)
                item_saved_article.foreground = ResourcesCompat.getDrawable(resources, R.color.blue_shade, null)
                viewModel.savedTopBarState.postValue(TopBarState.SelectionState(selectedItems))
                true
            }
            setOnClickListener {
                when(viewModel.savedTopBarState.value){
                    is TopBarState.SelectionState ->{
                        if (!selectedItems.contains(article)) {
                        selectedItems.add(article)
                        item_saved_article.foreground =ResourcesCompat.getDrawable(resources, R.color.blue_shade, null)
                        viewModel.savedTopBarState.postValue(TopBarState.SelectionState(selectedItems))
                        } else {
                        selectedItems.remove(article)
                        item_saved_article.foreground = null
                        viewModel.savedTopBarState.postValue( TopBarState.SelectionState(selectedItems) )
                        }
                    }
                    else -> {
                        context.showArticleInWebView(article)
                    }
                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = savedDiffer.currentList.size
}