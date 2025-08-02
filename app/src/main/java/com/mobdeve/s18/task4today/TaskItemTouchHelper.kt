import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s18.task4today.adapter.TaskAdapter
import com.mobdeve.s18.task4today.R

// TaskItemTouchHelper: class for swiping functionality on Tasks
class TaskItemTouchHelper(
    private val taskAdapter: TaskAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // Only implementing this because ItemTouchHelper requires it
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    // Open the Dialog Box
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val context = taskAdapter.getContext()
        val position = viewHolder.bindingAdapterPosition

        // Swipe LEFT to Delete
        if (direction == ItemTouchHelper.LEFT) {
            AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setMessage("Proceed with task deletion?")
                .setPositiveButton("CONFIRM") { dialog, _ ->
                    taskAdapter.notifyItemChanged(position) // reset swipe
                    dialog.dismiss()

                    // Delete Task from DB
                    taskAdapter.deleteTask(position)
                }
                .show()
        } else {
//            adapter.editTask(position)
        }
    }

    // Function to set the Edit and Delete Icons
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, //dX: x direction
        dY: Float, //dY: y direction
        actionState: Int,
        isActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        val icon: Drawable?
        val background: GradientDrawable = GradientDrawable()

        // EDIT COLOR: Green if SWIPE RIGHT
        if (dX > 0) {
            icon = ContextCompat.getDrawable(taskAdapter.getContext(), R.drawable.edit)
            background.setColor(ContextCompat.getColor(taskAdapter.getContext(), R.color.green_confirm))
        }
        // DELETE COLOR: Red if SWIPE LEFT
        else {
            icon = ContextCompat.getDrawable(taskAdapter.getContext(), R.drawable.delete)
            background.setColor(ContextCompat.getColor(taskAdapter.getContext(), R.color.red_cancel))
        }

        // Set the corner radius for all edges
        background.cornerRadius = 30f  // Apply uniform rounded corners

        // Set Edit/Delete icon positions
        icon?.let {
            val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight

            // Position of Edit Icon
            if (dX > 0) {
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }

            // Position of Delete Icon
            else if (dX < 0) {
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
            } else {
                background.setBounds(0, 0, 0, 0)
            }

            background.draw(c)
            icon.draw(c)
        }
    } // end of onChildDraw
}