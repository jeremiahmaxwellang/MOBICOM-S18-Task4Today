package com.mobdeve.s18.task4today

import android.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.mobdeve.s18.task4today.ToDoAdapter
import kotlin.Int

// RecyclerItemTouchHelper -> swipe task left to [Edit], right to [Delete]
class RecyclerItemTouchHelper(
    private val adapter : ToDoAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) {
        val context = adapter.activity
        val position = viewHolder.bindingAdapterPosition
        // Java equivalent: final int position = viewHolder.getAdapterPosition()

        // Swipe LEFT to Delete
        if(direction == ItemTouchHelper.LEFT){
            //Builder(context) or main activity
            AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Confirm") { dialog, _ ->
                    adapter.deleteItem(position)
                }

                .setNegativeButton("Cancel") { dialog, _ ->
                    adapter.notifyItemChanged(position) // reset swipe
                    dialog.dismiss()
                }
                .show()
        }
        else{
            adapter.editItem(position)
        }
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
//        viewHolder: MyViewHolder,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, //dX: x direction
        dY: Float, //dY: y direction
        actionState: Int,
        isActive: Boolean
                             ){
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        val icon: Drawable?
        val background: ColorDrawable

        // tutorial part 6: 13:33
        // Swipe Left: Edit icon
        if(dX > 0){
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.edit)
            background = ColorDrawable(ContextCompat.getColor(adapter.activity, R.color.colorPrimaryDark))
        }

        // Swipe right: Delete icon
        else {
            icon = ContextCompat.getDrawable(adapter.activity, R.drawable.delete)
            background = ColorDrawable(Color.RED)
        }

        icon?.let{
            val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight

            if(dX > 0){
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt() +
                    backgroundCornerOffset, itemView.bottom
                )
            }
            // Swiping left
            else if(dX < 0){
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom

                )
            }

            else{
                background.setBounds(0, 0, 0, 0)
            }
        }
            background.draw(c)
            icon.draw(c)
        }


}