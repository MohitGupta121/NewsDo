package com.mohit.newsdo.util.swipeDetector

import android.view.GestureDetector
import android.view.MotionEvent


class SwipeGestureDetector(private val swipeActions: SwipeActions) :
    GestureDetector.OnGestureListener {
    private val MIN_X_SWIPE_DISTANCE = 180
    private val MIN_Y_SWIPE_DISTANCE = 180
    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {


        //TypeCast the difference of co-ordinates to int and store in another variable
        val distanceSwipedInX = (e1.x - e2.x).toInt()
        val distanceSwipedInY = (e1.y - e2.y).toInt()

        // Make Check For Horizontal Swipe
        if (Math.abs(distanceSwipedInX) > MIN_X_SWIPE_DISTANCE) {

            // Now Check Which Side Swipe Happened
            if (distanceSwipedInX > 0) {
                swipeActions.onSwipeLeft()
            }
            return true
        }

        // Make Check For Horizontal Swipe
        if (Math.abs(distanceSwipedInY) > MIN_Y_SWIPE_DISTANCE) {

            // Now Check Which Side Swipe Happened
            if (distanceSwipedInY > 0) {
                swipeActions.onSwipeUp()
            } else {
                swipeActions.onSwipeDown()
            }
        }
        return false
    }
}
