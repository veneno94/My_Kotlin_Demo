package com.example.myapplication.recyclerview

import android.support.v7.widget.RecyclerView

/**
 * 自定义RecylcerView上拉加载处理
 */
abstract class EndlessGridRecyclerOnScrollListener protected constructor(private val mPullLoadHeadFootRecyclerView: PullLoadHeadFootGridRecyclerView) :
    RecyclerView.OnScrollListener() {


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

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

    //监听是否到底部
    private fun isSlideToBottom(recyclerView: RecyclerView?): Boolean {
        if (recyclerView == null) return false
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()
    }

    abstract fun onLoadMore(currentPage: Int)
    abstract fun distanceY(offsetY: Int)
}