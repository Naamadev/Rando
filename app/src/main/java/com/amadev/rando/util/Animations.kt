package com.amadev.rando.util

import android.os.Handler
import android.os.Looper
import android.view.View

object Animations {

    fun animateAlphaWithHandlerDelay(property: View, animationTime: Long, targetAlpha: Float, delay: Long) {
        Handler(Looper.myLooper()!!).postDelayed({
            property.animate().apply {
                duration = (animationTime)
                alpha(targetAlpha)
            }
        }, delay)
    }
}