package com.example.myapplication.recyclerview

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * RecyclerView添加HeadAndFoot辅助类
 */
class HeaderViewGridRecyclerAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mWrappedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private val mHeaderViews: MutableList<View>
    private val mFooterViews: MutableList<View>
    private val mItemTypesOffset: MutableMap<Class<*>, Int>


    private val wrappedItemCount: Int
        get() = mWrappedAdapter!!.itemCount


    private val headerCount: Int
        get() = mHeaderViews.size


    private val footerCount: Int
        get() = mFooterViews.size


    private val adapterTypeOffset: Int
        get() = mItemTypesOffset[mWrappedAdapter!!.javaClass]!!


    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            notifyDataSetChanged()
        }


        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

            super.onItemRangeChanged(positionStart, itemCount)
            notifyItemRangeChanged(positionStart + headerCount, itemCount)
        }


        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            super.onItemRangeInserted(positionStart, itemCount)
            notifyItemRangeInserted(positionStart + headerCount, itemCount)
        }


        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            super.onItemRangeRemoved(positionStart, itemCount)
            notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        }


        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            val hCount = headerCount
            // TODO: No notifyItemRangeMoved method?
            notifyItemRangeChanged(fromPosition + hCount, toPosition + hCount + itemCount)
        }
    }

    init {
        mHeaderViews = ArrayList()
        mFooterViews = ArrayList()
        mItemTypesOffset = HashMap()
        setWrappedAdapter(adapter)
    }
    

    override fun getItemViewType(position: Int): Int {
        val hCount = headerCount
        if (position < hCount) {
            return HEADERS_START + position
        } else {
            val itemCount = mWrappedAdapter!!.itemCount
            return if (position < hCount + itemCount) {
                adapterTypeOffset + mWrappedAdapter!!.getItemViewType(position - hCount)
            } else {
                FOOTERS_START + position - hCount - itemCount
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when{
            viewType < HEADERS_START + headerCount -> StaticViewHolder(
                mHeaderViews[viewType - HEADERS_START]
            )
            viewType < FOOTERS_START + footerCount -> StaticViewHolder(
                mFooterViews[viewType - FOOTERS_START]
            )
            else -> mWrappedAdapter!!.onCreateViewHolder(viewGroup, viewType - adapterTypeOffset)
        }
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val hCount = headerCount
        if (position >= hCount && position < hCount + mWrappedAdapter!!.itemCount) {
            mWrappedAdapter!!.onBindViewHolder(viewHolder, position - hCount)
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager as GridLayoutManager?
            gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val spanSize = 1 //每个griditem 占1份  一行有3个网格item 每个spansize为1  一个item占满则需要返回3
                    val hCount = headerCount
                    if (position < hCount) {
                        return gridLayoutManager.spanCount
                    }

                    val itemCount = mWrappedAdapter!!.itemCount
                    return if (position >= hCount + itemCount) {
                        gridLayoutManager.spanCount
                    } else spanSize
                }
            }


        }

    }

    override fun onViewAttachedToWindow(viewHolder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(viewHolder)
        if (viewHolder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {

            val viewType = viewHolder.itemViewType
            if (viewType < HEADERS_START + headerCount) {
                // 获取cardview的布局属性，记住这里要是布局的最外层的控件的布局属性，如果是里层的会报cast错误
                val clp = viewHolder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                // 最最关键一步，设置当前view占满列数，这样就可以占据两列实现头部了
                if (clp != null)
                    clp.isFullSpan = true
            } else if (viewType < FOOTERS_START + footerCount) {
                // 获取cardview的布局属性，记住这里要是布局的最外层的控件的布局属性，如果是里层的会报cast错误
                val clp1 = viewHolder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                // 最最关键一步，设置当前view占满列数，这样就可以占据两列实现头部了
                clp1.isFullSpan = true
            }
        }


    }

    fun addHeaderView(view: View) {
        mHeaderViews.add(view)
    }


    fun addFooterView(view: View) {
        mFooterViews.add(view)
    }


    fun removeHeadView() {
        mHeaderViews.clear()
    }


    fun removeFootView() {
        mFooterViews.clear()
    }


    override fun getItemCount(): Int {
        return headerCount + footerCount + wrappedItemCount
    }


    private fun setWrappedAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        if (mWrappedAdapter != null) mWrappedAdapter!!.unregisterAdapterDataObserver(mDataObserver)
        mWrappedAdapter = adapter
        val adapterClass = mWrappedAdapter!!.javaClass
        if (!mItemTypesOffset.containsKey(adapterClass)) putAdapterTypeOffset(adapterClass)
        mWrappedAdapter!!.registerAdapterDataObserver(mDataObserver)
    }


    private fun putAdapterTypeOffset(adapterClass: Class<*>) {
        mItemTypesOffset[adapterClass] = ITEMS_START + mItemTypesOffset.size * ADAPTER_MAX_TYPES
    }

    private class StaticViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val HEADERS_START = Integer.MIN_VALUE
        private const val FOOTERS_START = Integer.MIN_VALUE + 10
        private const val ITEMS_START = Integer.MIN_VALUE + 20
        private const val ADAPTER_MAX_TYPES = 100
    }
}
