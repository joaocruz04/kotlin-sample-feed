package com.hubrickchallenge.android.feed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.hubrickchallenge.android.R
import java.lang.Math.abs


class FeedViewImpl : FeedView, AppCompatActivity() {
    
    var recyclerView: RecyclerView? = null
    var presenter = FeedPresenterImpl()
    var adapter: FeedAdapter?=null
    var tooltip: TextView?=null
    var handler: Handler = Handler()

    var hideRunnable = Runnable { hideCountTooltip() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_view_impl)
        recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        tooltip = findViewById(R.id.tooltip) as TextView
        tooltip?.setOnClickListener { countBubbleClick() }
        presenter.registerView(this)
        setupRecyclerView()
    }

    fun setupRecyclerView() {
        recyclerView?.layoutManager = CustomLinearLayoutManager(this)
        adapter = FeedAdapter(presenter.events)
        adapter?.setHasStableIds(true)
        recyclerView?.adapter = adapter

    }

    override fun showTop() {
        adapter?.scrollToTop()
    }

    override fun updateData(newItemsCount: Int) {
        var position = (recyclerView?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        var offset = ((recyclerView?.layoutManager as LinearLayoutManager).findViewByPosition(position))?.top?:0
        if (position>0 || abs(offset)>10)
            showCountTooltip(newItemsCount, 3000)
        adapter?.update(position, newItemsCount, offset)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onPause() {
        super.onPause()
        presenter.stop()
    }

    override fun showCountTooltip(count: Int, duration: Long) {
        handler.removeCallbacksAndMessages(null)
        tooltip?.text = "NEW ${count} ITEMS"
        tooltip?.visibility = View.VISIBLE
        handler.postDelayed(hideRunnable, duration)
    }

    override fun hideCountTooltip() {
        handler.removeCallbacks { hideRunnable }
        tooltip?.visibility = View.GONE
    }

    fun countBubbleClick() {
        presenter.countBubbleClicked()
    }
}
