package com.mohit.newsdo.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.model.NewsResponse
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

private fun slideUpNow(view: View) {
    view.translationY = view.height.toFloat()
    view.animate()
        .translationY(0f)
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.VISIBLE
                view.isVisible = true
                view.alpha = 1f
            }
        })
}

fun ExpandableBottomBar.setOnItemReselectedListener(listener: ((View, ExpandableBottomBarMenuItem) -> Unit)?) {
    this.onItemReselectedListener = listener
}

fun Context.share(article: Article) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    share.putExtra(Intent.EXTRA_SUBJECT, article.title)
    share.putExtra(Intent.EXTRA_TEXT, article.url)
    this.startActivity(Intent.createChooser(share, "Share Article"))
}

fun NewsResponse.filterResponse(): NewsResponse {

    val list = mutableListOf<Article>()
    for (article in this.articles) {
        if (article.description != "" && article.description != null) {
            article.apply {
                if (ifNotFormatted(this)) {
                    publishedAt = formatDate(publishedAt)
                    author = formatAuthor(author)
                }
            }
        } else {
            list.add(article)
        }
    }
    this.articles = this.articles.minus(list) as MutableList<Article>

    return this

}

private fun ifNotFormatted(article: Article): Boolean {
    article.author.apply {
        if (this != null) {
            if (this.isNotEmpty() && this.length > 6) {
                val source = this.substring(0..5)
                return source != "Source"
            }
        }
    }
    return true
}


private fun formatAuthor(a: String): String {
    return if (a == "" || a == null) {
        "Source: Unknown"
    } else {
        "Source: $a"
    }
}

private fun formatDate(d: String): String {
    val year = d.substring(0, 4)
    val month = d.substring(5, 7)
    val date = d.substring(8, 10)
    return "Published at : $date-$month-$year"
}