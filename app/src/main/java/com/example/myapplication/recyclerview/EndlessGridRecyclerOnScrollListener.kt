package com.example.myapplication.recyclerview

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 *
 *
 * 自定义RecylcerView上拉加载处理
 */
abstract class EndlessGridRecyclerOnScrollListener protected constructor(private val mPullLoadHeadFootRecyclerView: PullLoadHeadFootGridRecyclerView) :
    RecyclerView.OnScrollListener() {

    internal var offsetY = 0
    internal var viewHeight = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val linearLayoutManager = layoutManager as LinearLayoutManager?
            offsetY += dy

            val first = linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
            val last = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val firstview = linearLayoutManager.findViewByPosition(first)
            if (viewHeight == 0) {
                viewHeight = firstview!!.height
            }
            var offseta = firstview!!.top
            var sx = 1f + offseta.toFloat() / viewHeight
            if (offsetY == 0) {
                val view = linearLayoutManager.findViewByPosition(first + 1)
                view!!.scaleX = 2f
            }

            firstview.scaleX = sx
            val lastview = linearLayoutManager.findViewByPosition(last)
            offseta = recyclerView.height - lastview!!.bottom
            sx = 1f + offseta.toFloat() / viewHeight
            lastview.scaleX = sx
        }


        distanceY(recyclerView.computeVerticalScrollOffset())

        if (dy <= 0) return


        //新的监听滑动到底部
        if (isSlideToBottom(recyclerView) && !mPullLoadHeadFootRecyclerView.isLoadMore() && !mPullLoadHeadFootRecyclerView.isNoMore()) {
            mPullLoadHeadFootRecyclerView.setLoadMore(true)
            var currentPage = mPullLoadHeadFootRecyclerView.getCurrentPage()
            currentPage++
            onLoadMore(currentPage)
        }
    }


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val linearLayoutManager = layoutManager as LinearLayoutManager?
            if (newState == 0) {
                val postion = linearLayoutManager!!.findFirstVisibleItemPosition()
                val view = linearLayoutManager.findViewByPosition(postion)
                val top = view!!.top
                var offset = 0
                if (viewHeight == 0) {
                    viewHeight = view.height
                }
                if (top == 0) {
                    return
                } else if (-top < viewHeight / 2) {
                    offset = top
                } else {
                    offset = viewHeight + top
                }
                recyclerView.smoothScrollBy(0, offset)
            }

        }

    }

    //监听是否到底部
    private fun isSlideToBottom(recyclerView: RecyclerView?): Boolean {
        if (recyclerView == null) return false
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()
    }


    abstract fun onLoadMore(currentPage: Int)
    abstract fun distanceY(offsetY: Int)


    fun getScollYDistance(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        val position = layoutManager!!.findFirstVisibleItemPosition()
        val firstVisiableChildView = layoutManager.findViewByPosition(position)
        val itemHeight = firstVisiableChildView!!.height
        return position * itemHeight - firstVisiableChildView.top
    }

}