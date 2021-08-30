package com.amadev.rando.layoutManager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class ScaleCenterItemRecyclerView(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean
) :
    LinearLayoutManager(
        context,
        orientation,
        reverseLayout
    ) {

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = width / 3
        return true
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        scaleMiddleItem()
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleMiddleItem()
            scrolled
        } else {
            0
        }
    }

    private fun scaleMiddleItem() {
        val middle = width / 2
        val d1 = 0.9f * middle

        (0 until childCount).forEach { i ->
            val child = getChildAt(i)
            val childMid = (getDecoratedRight(child!!) + getDecoratedLeft(child!!)) / 2f
            val d = d1.coerceAtMost(abs(middle - childMid))
            val scale = 1 - 0.15f * d / d1
            child.scaleX = scale
            child.scaleY = scale
        }
    }
}