package com.example.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import com.example.myapplication.adapter.StaggeredGridLayoutAdapter
import com.example.myapplication.recyclerview.PullLoadHeadFootGridRecyclerView
import com.example.myapplication.recyclerview.SpaceItemDecoration
import com.example.myapplication.utils.DisplayTool
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mHeaderView: View

    private lateinit var adapter: StaggeredGridLayoutAdapter

    private var mPage = 1

    private val mList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.fragment_rec_head, null)
        adapter = StaggeredGridLayoutAdapter(this, mList)
        mFindLaunchRv.setStaggeredGridLayout(2)
        mFindLaunchRv.addItemDecoration(
            SpaceItemDecoration(
                DisplayTool(this).dip2px(12.0),
                DisplayTool(this).dip2px(10.0),
                DisplayTool(this).dip2px(5.0),
                1,
                SpaceItemDecoration.STAGGEREDGRIDLAYOUT
            )
        )
        mFindLaunchRv.setAdapter(adapter)

        mFindLaunchRv.addHeadView(mHeaderView)

        mFindLaunchRv.setOnPullLoadMoreListener(object : PullLoadHeadFootGridRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {
                mPage = 1
                postFind()

            }

            override fun onLoadMore() {
                mPage++
                postFind()
            }
        })
        mFindLaunchRv.setRefreshing(true)
    }

    private fun postFind() {
        Thread(Runnable {
            try {
                //模拟网络请求 加载数据
                Thread.sleep(500)
                var data = ArrayList<String>()
                if (mPage <= 5) { //5页后没有数据
                    for (i in 0..19) {
                        when{
                            i % 5 == 0 -> data.add("敏敏我爱你 雷宝宝")
                            i % 5 == 1 -> data.add("除了今生，如果还有来世，我一定要和你再续前缘，我要一直爱着你 护着你 想着你")
                            i % 5 == 2 -> data.add("除了今生，如果还有来世，我一定要和你再续前缘，我要一直爱着你 抱着你 护着你 佛说：五百年的回眸，才换来今生的一次擦肩而过")
                            i % 5 == 3 -> data.add("除了今生，如果还有来世，我一定要和你再续前缘，我要一直爱着你 抱着你 护着你 佛说：五百年的回眸，才换来今生的一次擦肩而过，我要在三生石上刻上我们的名字，来世我要找到你")
                            i % 5 == 4 -> data.add("除了今生，如果还有来世，我一定要和你再续前缘，我要一直爱着你 抱着你 护着你 佛说：五百年的回眸，才换来今生的一次擦肩而过，我要在三生石上刻上我们的名字，来世我要找到你")
                            else -> data.add("敏敏我爱你 雷宝宝")
                        }
                    }
                }

                runOnUiThread {
                    if (mPage == 1) {
                        mList.clear()
                        mFindLaunchRv!!.setNoMore(false)

                        if (data == null || data.size == 0) {
                            mFindLaunchRv.setNoMore("还没有数据")
                        } else {
                            mList.addAll(data)
                            mFindLaunchRv.setPullLoadMoreCompleted()
                        }
                    } else {

                        if (data == null || data.size == 0) {
                            mFindLaunchRv.setNoMore("没有更多数据")
                        } else {
                            mList.addAll(data)
                            mFindLaunchRv.setPullLoadMoreCompleted()
                        }
                    }

                    adapter.notifyDataSetChanged()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()


    }
}
