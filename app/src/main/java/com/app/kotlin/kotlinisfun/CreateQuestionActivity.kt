package com.app.kotlin.kotlinisfun

import android.app.Activity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_question.*
import kotlinx.android.synthetic.main.content_create_question.*

class CreateQuestionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_question)
        setSupportActionBar(toolbar)
        setBackButton(true)

        fab.setOnClickListener { view ->
            onOkay()
        }
    }

    private fun onOkay() {
        val questionPoints = questionPoints.text.toString().toLong()
        val question = question_et.text.toString()
        val answer = answer.text.toString()

        if (questionPoints == 0L || question.isEmpty() || answer.isEmpty()) {
            showSnackbar("Please include the question, answer, and the questionPoints must be more than 2")
            return
        }

        showProgress(true)
        var user = FirebaseAuth.getInstance().currentUser
        var firestore = FirebaseFirestore.getInstance()
        firestore.runTransaction {
            //get user's information
            var userInfo = firestore.collection("UserInfo").document(user!!.uid)
            var userPoints = it.get(userInfo)["points"].toString().toLong()

            //check if the user has enough points
            if (userPoints < questionPoints) {
                throw Exception("You don't have enough points, you have $userPoints")
            }

            //update the user's points
            userPoints -= questionPoints
            it.update(userInfo, "points", userPoints)

            //create the new question
            var newQuestion = Question()
            newQuestion.question = question
            newQuestion.answer = answer
            newQuestion.points = questionPoints
            newQuestion.creator = user.email!!
            newQuestion.creatorId = user.uid

            firestore.collection("Questions").add(newQuestion)

            return@runTransaction
        }.addOnCompleteListener {
            showProgress(false)
            if (it.isSuccessful) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }.addOnFailureListener {
            showSnackbar(it.message!!)
        }
    }

}
