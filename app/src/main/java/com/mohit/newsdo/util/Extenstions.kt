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
import com.mohit.newsdo.R
import com.mohit.newsdo.adapters.SavedRecAdapter
import com.mohit.newsdo.model.Article
import com.mohit.newsdo.model.NewsResponse
import com.shashank.sony.fancytoastlib.FancyToast
import com.thefinestartist.finestwebview.FinestWebView
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem


fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.successToast(msg: String){
    FancyToast.makeText(this,msg,FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
}

fun Context.errorToast(msg: String){
    FancyToast.makeText(this,msg,FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show()
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun String.format(): String {
    return "${this[0].toUpperCase()}${this.substring(1, this.length)}"
}

fun Float.convertToDp(context: Context): Float {
    return this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun View.slideDown() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@slideDown.visibility = View.GONE
                this@slideDown.isVisible = false
            }
        })
}

fun View.slideUp() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    if (this.height > 0) {
        slideUpNow(this)
    } else {
        // wait till height is measured
        this.post { slideUpNow(this) }
    }
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

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    this.clearFocus()
    val inputMethodManager: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}


fun Context.getColor(id: Int) = ContextCompat.getColor(this, id)

fun View.revealWithAnimation() {

    val cx: Int = this.measuredWidth
    val cy: Int = this.measuredHeight
    val finalRadius: Int = Math.max(this.width, this.height)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius.toFloat())
    this.show()
    anim.start()

}

fun View.hideWithAnimation() {

    val cx: Int = this.measuredWidth
    val cy: Int = this.measuredHeight

    val initialRadius: Int = Math.max(this.width, this.height)

    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), 0f)

    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            this@hideWithAnimation.hide()
        }
    })
    anim.start()

}

fun RecyclerView.setUpWithAdapter(context: Context, adapter: SavedRecAdapter) {
    this.adapter = adapter
    this.layoutManager = LinearLayoutManager(context)
}

fun ExpandableBottomBar.setOnItemReselectedListener(listener: ((View, ExpandableBottomBarMenuItem) -> Unit)?) {
    this.onItemReselectedListener = listener
}

fun Context.showArticleInWebView(article: Article) {
    FinestWebView.Builder(this)
        .toolbarScrollFlags(0)
        .titleDefault(article.source.name)
        .gradientDivider(true)
        .webViewSupportZoom(true)
        .statusBarColorRes(R.color.light_blue)
        .show(article.url)
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