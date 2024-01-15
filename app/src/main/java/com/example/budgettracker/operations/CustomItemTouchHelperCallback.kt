package com.example.budgettracker.operations

import android.graphics.Canvas
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R

open class CustomItemTouchHelperCallback(
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: OnSwipeListener
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    interface OnSwipeListener {
        fun onSwipe(viewHolder: RecyclerView.ViewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwipe(viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (viewHolder.itemView.findViewById<View>(R.id.date) == null) {
                viewHolder.itemView.translationX = dX
            }
            else {
                viewHolder.itemView.findViewById<View>(R.id.date).translationX = 0f
                viewHolder.itemView.findViewById<ConstraintLayout>(R.id.info).translationX = dX
            }
        }
    }
}
