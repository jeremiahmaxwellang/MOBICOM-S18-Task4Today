package com.mobdeve.s18.task4today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.content.DialogInterface
import android.widget.TextView
import androidx.fragment.app.DialogFragment

// EditTask - Display Overlay for Editing Tasks
class EditTask : DialogFragment() {
    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): EditTask {
            return EditTask()
        }

        fun newInstance(id: Int, task: String): EditTask {
            val fragment = EditTask()
            fragment.arguments = Bundle().apply {
                putInt("id", id)
                putString("task", task)
            }
            return fragment
        }

    }

    private lateinit var taskInput : EditText
    private lateinit var confirmButton : Button
    private lateinit var cancelButton : Button
    private lateinit var timeButton: Button
    private lateinit var overlayTitle: TextView
    private lateinit var newTaskTitle: TextView
    private lateinit var dbHelper : DbHelper
    private var dialogCloseListener: DialogCloseListener? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View{
        val view: View = inflater.inflate(R.layout.overlay_new_task, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        overlayTitle = view.findViewById(R.id.overlayTitle)
        newTaskTitle = view.findViewById(R.id.newTaskTitle)
        taskInput = view.findViewById(R.id.taskInput)
        confirmButton = view.findViewById(R.id.confirmButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        timeButton = view.findViewById(R.id.timeButton)

        // Set Overlay Design
        overlayTitle.visibility = View.GONE
        newTaskTitle.setText("Edit Task")
        timeButton.visibility = View.GONE // hiding time button

        dbHelper = DbHelper(requireContext())

        var isUpdate = false
        val bundle = arguments

        // Update Task
        if(bundle != null){
            isUpdate = true
            val task = bundle.getString("task", "")
            taskInput.setText(task)
        }

        confirmButton.setOnClickListener {
            val text = taskInput.text.toString()

            if(isUpdate){
                dbHelper.updateTask(bundle?.getInt("id") ?: -1, text)
            }
            dismiss()
        } // end of CONFIRM btn listener

        cancelButton.setOnClickListener {
            dismiss()
        } // end of CANCEL btn listener

    } // end of onViewCreated()

    // DialogCloseListener interface
    interface DialogCloseListener{
        fun handleDialogClose(dialog: DialogInterface)
    }

    // Dismiss Dialog Box Behavior
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogCloseListener?.handleDialogClose(dialog)

        val activity = activity
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }

    // Set the Dialog Close Listener to the current fragment's listener
    // Called in onDismiss()
    fun setDialogCloseListener(listener: DialogCloseListener?) {
        this.dialogCloseListener = listener
    }

}