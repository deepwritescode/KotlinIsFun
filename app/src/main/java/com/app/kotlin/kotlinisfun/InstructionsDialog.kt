package com.app.kotlin.kotlinisfun

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment


class InstructionsDialog : AppCompatDialogFragment(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title_instructions)

        var text = ""
        text += "Welcome to What's What! \n\n"
        text += "What's What is a game that let's you ask and answer questions for points\n\n"
        text += "Here are the rules\n\n"
        text += "1) You can submit a question for everyone to answer inside the app and pay points from your store to set its bounty\n"
        text += "2) It costs 2 points to try to answer a question\n"
        text += "3) If you answer the question correctly, you win the all points the question is " +
                "worth and the 2 points you paid Otherwise you lose the 2 points, 1 point goes to the " +
                "creator of the question and 1 point is added to the total of the question\n"
        builder.setMessage(text)

        builder.setPositiveButton(android.R.string.ok, this)
        builder.setCancelable(true)
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> dismiss()
        }
    }

    companion object {
        val TAG = "UserInfoDialog"

        fun newInstance(): InstructionsDialog {
            return InstructionsDialog()
        }
    }
}