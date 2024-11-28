package com.example.cointrol.ui;

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            bottom = spacing


            if (parent.getChildAdapterPosition(view) == 0) {
                top = spacing
            }
        }
    }
}
