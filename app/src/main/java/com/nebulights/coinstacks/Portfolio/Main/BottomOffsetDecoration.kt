package com.nebulights.coinstacks.Portfolio.Main

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


internal class BottomOffsetDecoration(private val mBottomOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val dataSize = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (dataSize > 0 && position == dataSize - 1) {
            outRect.set(0, 0, 0, mBottomOffset)
        } else {
            outRect.set(0, 0, 0, 0)
        }
    }
}