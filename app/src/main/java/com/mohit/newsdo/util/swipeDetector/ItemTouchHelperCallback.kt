package com.mohit.newsdo.util.swipeDetector

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mohit.newsdo.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ItemTouchHelperCallback(private val swipeActions: RecyclerViewSwipe) :
    ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> {
                swipeActions.onSwipeLeft(viewHolder)
            }
            ItemTouchHelper.RIGHT -> {
                swipeActions.onSwipeRight(viewHolder)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        ).addSwipeLeftBackgroundColor(
            swipeActions.addSwipeLeftBackgroundColor()
        ).addSwipeLeftActionIcon(
                swipeActions.addSwipeLeftActionIcon()
        ).addSwipeRightBackgroundColor(
                swipeActions.addSwipeRightBackgroundColor()
        ).addSwipeRightActionIcon(R.drawable.ic_share_2)
            .create()
            .decorate()
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }


}