package com.app.kotlin.kotlinisfun

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater


class AnswerQuestionDialog : AppCompatDialogFragment(), DialogInterface.OnClickListener {

    private var question: Question? = null
    private var mEventListener: EventListener? = null

    private var textInput: TextInputEditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title_answer_question)

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_answer_question, null)

        this.textInput = view.findViewById(R.id.text_input)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok, this)
        builder.setNegativeButton(android.R.string.cancel, this)
        builder.setCancelable(true)
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> mEventListener?.onOkay(this.textInput!!.text.toString(), this.question!!)
            DialogInterface.BUTTON_NEGATIVE -> onCancel(dialog)
        }
    }

    interface EventListener {
        //called when the user presses the ok button and inputs the answer
        fun onOkay(answer: String, question: Question)
    }

    companion object {
        const val TAG = "AnswerQuestionDialog"

        fun newInstance(question: Question, listener: EventListener): AnswerQuestionDialog {
            val frag = AnswerQuestionDialog()
            frag.mEventListener = listener
            frag.question = question
            return frag
        }
    }
}