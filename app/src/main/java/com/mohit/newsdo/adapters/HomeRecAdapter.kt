package com.mohit.newsdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.mohit.newsdo.R
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.ui.NewsViewModel
import com.mohit.newsdo.util.share
import com.mohit.newsdo.util.showArticleInWebView
import com.mohit.newsdo.util.toast
import kotlinx.android.synthetic.main.item_article.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeRecAdapter( var viewModel: NewsViewModel) : RecyclerView.Adapter<HomeRecAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_article, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            tv_author.text = article.author
            tv_date.text = article.publishedAt
            tv_headline.text = article.title
            tv_content.text = article.description
            val s = "Click to view more at ${article.source.name}"
            tv_swipe_left.text = s
            tv_swipe_left.setOnClickListener {
              context.showArticleInWebView(article)
            }
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                if (viewModel.isArticleSaved(article)) {
                    fab_saved.setImageResource(R.drawable.ic_icons8_bookmark)
                }
            }
            Glide.with(this)
                .load(article.urlToImage)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .signature(ObjectKey(article.title))
                .centerCrop()
                .placeholder(resources.getDrawable(R.drawable.photo_placeholder, null))
                .error(R.drawable.photo_placeholder)
                .into(iv_content_image)
            fab_saved.setOnClickListener {
                viewModel.saveArticle(article)
                context.toast("Saved")
                notifyDataSetChanged()
            }
            ib_share_article.setOnClickListener {
                context.share(article)
            }
        }
    }

    private val differCallback: DiffUtil.ItemCallback<Article> = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.description == newItem.description

    }
    val differ = AsyncListDiffer(this, differCallback)



    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}