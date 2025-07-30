package com.mobdeve.s18.task4today

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.content.DialogInterface

class AddNewTask : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): AddNewTask {
            return AddNewTask()
        }

        fun newInstance(id: Int, task: String): AddNewTask {
            val fragment = AddNewTask()
            fragment.arguments = Bundle().apply {
                putInt("id", id)
                putString("task", task)
            }
            return fragment
        }

    }

    private lateinit var newTaskText : EditText
    private lateinit var newTaskSaveButton : Button
    private lateinit var dbHelper : DbHelper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
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

        dbHelper = DbHelper(requireContext())

        var isUpdate = false
        val bundle = arguments

        if(bundle != null){
            isUpdate = true
            val task = bundle.getString("task", "")
            newTaskText.setText(task)

            if(task.isNotEmpty()) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }
        }

        newTaskText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                if(s.isNullOrEmpty()){
                    newTaskSaveButton.setEnabled(false)
                    newTaskSaveButton.setTextColor(Color.GRAY)
                }
                else{
                    newTaskSaveButton.setEnabled(true)
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }) // end of addTextChangedListener

        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if(isUpdate){
                dbHelper.updateTask(bundle?.getInt("id") ?: -1, text)
            }
            else {
                // ToDoModel(status, task)
                // TODO: Fix insertTask function call with date and time
//                val task = TaskModel(0, text)
//                dbHelper.insertTasks(task)
            }
            dismiss()
        } // end of onClickListener
    } // end of onViewCreated()

    // Close "Delete Task?" dialog box
    override fun onDismiss(dialog: DialogInterface) {
        val activity = getActivity()
        if(activity is DialogCloseListener){
            activity.handleDialogClose(dialog)
        }
    }

    // DialogCloseListener interface
    interface DialogCloseListener{
        fun handleDialogClose(dialog: DialogInterface)
    }

}