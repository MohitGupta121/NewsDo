package com.mohit.newsdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.mohit.newsdo.R
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.util.showArticleInWebView
import kotlinx.android.synthetic.main.saved_article.view.*

class SearchRecAdapter() :
    RecyclerView.Adapter<SearchRecAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val searchDifferCallback: DiffUtil.ItemCallback<Article> =
        object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.description == newItem.description

        }


    val searchDiffer = AsyncListDiffer(this, searchDifferCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_article, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = searchDiffer.currentList[position]
        holder.itemView.apply {
            tv_headline.text = article.title
            tv_date.text = article.publishedAt
            Glide.with(this)
                .load(article.urlToImage)
                .centerCrop()
                .signature(ObjectKey(article.title))
                .placeholder(R.drawable.photo_placeholder)
                .error(R.drawable.photo_placeholder)
                .into(iv_content_image)

            setOnClickListener {
                context.showArticleInWebView(article)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = searchDiffer.currentList.size


}