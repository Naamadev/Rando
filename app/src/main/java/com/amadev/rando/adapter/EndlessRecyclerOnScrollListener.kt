package com.amadev.rando.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerOnScrollListener(private val mLinearLayoutManager: GridLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 9
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && totalItemCount - visibleItemCount
            <= firstVisibleItem + visibleThreshold
        ) {
            onLoadMore(currentPage)
            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)
}
