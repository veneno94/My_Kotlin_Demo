package com.example.myapplication.recyclerview

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.myapplication.R

class PullLoadHeadFootGridRecyclerView : LinearLayout, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mAdapter: HeaderViewGridRecyclerAdapter
    private lateinit var loadMoreView: View
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mLoadTv: TextView
    private lateinit var loadMoreLl: LinearLayout

    private var mContext: Context? = null
    private var isLoading = true
    private var previousTotal = 0
    private var currentPage = 1
    private var isLoadMore = false
    private var isNoMore = false//没有更多了


    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context?) {
        mContext = context
        val view = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_head_foot_layout, null)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_green_dark,
            android.R.color.holo_blue_dark,
            android.R.color.holo_orange_dark
        )
        mSwipeRefreshLayout.setOnRefreshListener(this)

        mRecyclerView = view.findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)


        mRecyclerView.addOnScrollListener(object : EndlessGridRecyclerOnScrollListener(this) {
            override fun onLoadMore(i: Int) {
                mPullLoadMoreListener?.let {
                    loadMoreLl.visibility = View.VISIBLE
                    mPullLoadMoreListener?.onLoadMore()
                }
            }

            override fun distanceY(offsetY: Int) {
                distanceListener?.let {
                    distanceListener?.distanceY(offsetY)
                }
            }
        })

        this.addView(view)
    }

    override fun onRefresh() {
        setNoMore(false)
        setIsLoading(true)
        setPreviousTotal(0)
        setCurrentPage(0)
        if (mProgressBar != null && mLoadTv != null) {
            loadMoreLl.visibility = View.GONE
            mProgressBar.visibility = View.VISIBLE
            mLoadTv.text = "正在加载中..."
        }
        mPullLoadMoreListener?.let {
            mPullLoadMoreListener?.onRefresh()
        }

    }

    fun getmRecyclerView(): RecyclerView {
        return mRecyclerView
    }

    fun setLinearLayout() {
        val linearLayoutManager = CrashLinearLayoutManager(mContext)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = linearLayoutManager
    }


    /**
     * GridLayoutManager
     */

    fun setGridLayout(spanCount: Int) {
        val gridLayoutManager = GridLayoutManager(mContext, spanCount)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = gridLayoutManager
    }

    /**
     * StaggeredGridLayoutManager
     */
    fun setStaggeredGridLayout(spanCount: Int) {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        mRecyclerView.layoutManager = staggeredGridLayoutManager
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mAdapter = HeaderViewGridRecyclerAdapter(adapter)
        mRecyclerView.adapter = mAdapter
        loadMoreView = LayoutInflater.from(mContext).inflate(R.layout.layout_load_more, mRecyclerView, false)
        loadMoreLl = loadMoreView.findViewById(R.id.load_more_ll)
        mProgressBar = loadMoreView.findViewById(R.id.load_pro)
        mLoadTv = loadMoreView.findViewById(R.id.load_tv)
        mAdapter.addFooterView(loadMoreView)
        loadMoreLl.visibility = View.GONE

    }


    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView.let {
            this.mRecyclerView.addItemDecoration(itemDecoration)
        }

    }

    fun getPreviousTotal(): Int {
        return previousTotal
    }

    fun setPreviousTotal(previousTotal: Int) {
        this.previousTotal = previousTotal
    }

    fun getCurrentPage(): Int {
        return currentPage
    }

    fun setCurrentPage(currentPage: Int) {
        this.currentPage = currentPage
    }

    fun addHeadView(headView: View) {
        mAdapter.let {
            mAdapter.addHeaderView(headView)
        }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        mSwipeRefreshLayout.isRefreshing = isRefreshing
        mPullLoadMoreListener?.let {
            mPullLoadMoreListener?.onRefresh()
        }
    }

    fun scrollToTop() {
        mRecyclerView.scrollToPosition(0)
    }

    fun isLoadMore(): Boolean {
        return isLoadMore
    }

    fun setLoadMore(loadMore: Boolean) {
        isLoadMore = loadMore
    }

    fun isLoading(): Boolean {
        return isLoading
    }

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    fun isNoMore(): Boolean {
        return isNoMore
    }

    fun setNoMore(noMore: Boolean) {
        isNoMore = noMore
        if (!isNoMore) { //当是false的时候改变文字和显示圆圈
            if (mProgressBar != null && mLoadTv != null) {
                mProgressBar.visibility = View.VISIBLE
                mLoadTv.text = "正在加载中..."
            }
        }
    }

    //设置没有更多数据了
    fun setNoMore(text: String) {
        setNoMore(true)
        setLoadMore(false)
        mSwipeRefreshLayout.isRefreshing = false
        loadMoreLl.visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
        mLoadTv.text = text
    }


    fun setPullLoadMoreCompleted() {
        setLoadMore(false)
        mSwipeRefreshLayout.isRefreshing = false
        loadMoreLl.visibility = View.GONE
    }

    interface PullLoadMoreListener {
        fun onRefresh()

        fun onLoadMore()
    }

    interface DistanceListener {
        fun distanceY(offsetY: Int)

    }

    private var distanceListener: DistanceListener? = null

    fun setDistanceListener(distanceListener: DistanceListener) {
        this.distanceListener = distanceListener
    }

    fun setOnPullLoadMoreListener(listener: PullLoadMoreListener) {
        mPullLoadMoreListener = listener
    }

    private var mPullLoadMoreListener: PullLoadMoreListener? = null

}