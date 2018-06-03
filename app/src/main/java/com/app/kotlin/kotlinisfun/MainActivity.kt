package com.app.kotlin.kotlinisfun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : BaseActivity(), OnCompleteListener<QuerySnapshot>, QuestionsAdapter.Callbacks {

    val user = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //show instructions when user logs in
        if (intent.getBooleanExtra("showInstructions", false)) {
            InstructionsDialog.newInstance().show(supportFragmentManager, InstructionsDialog.TAG)
        }

        adapter = QuestionsAdapter(this, this)

        val itemDecor = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        recycler.addItemDecoration(itemDecor)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        fab.setOnClickListener({
            var i = Intent(this, CreateQuestionActivity::class.java)
            startActivityForResult(i, CREATE_REQ)
        })

        onRefresh()
    }

    private var points: Long = 0

    override fun onStart() {
        super.onStart()
        if (user == null) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
            return
        }

        var infoRef = firestore.collection("UserInfo").document(user.uid)
        infoRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            points = documentSnapshot!!["points"].toString().toLong()

            toolbar!!.title = " Points:" + points
        }
    }


    private val questionCallback = object : AnswerQuestionDialog.EventListener {
        override fun onOkay(answer: String, question: Question) {
            showKeyboard(false)
            val correct = answer.toLowerCase().equals(question.answer.toLowerCase())
            if (correct) {
                onAnswerCorrect(question)
            } else {
                onAnswerIncorrect(question)
            }
        }
    }

    private fun onAnswerCorrect(question: Question) {
        showProgress(true)
        firestore.runTransaction {
            var infoRef = firestore.collection("UserInfo").document(user!!.uid)
            var questionRef = firestore.collection("Questions").document(question.id)

            // get the user's information specifically their points
            var userPoints = it.get(infoRef)["points"].toString().toLong()

            // give the user the points that the question was worth
            var questionPoints = it.get(questionRef)["points"].toString().toLong()
            it.update(infoRef, "points", userPoints + questionPoints)

            // mark the question answered
            it.update(questionRef, "answered", true)

            return@runTransaction
        }.addOnCompleteListener {
            showProgress(false)
            if (it.isSuccessful) {
                onRefresh()
                showSnackbar("Correct!")
            }
        }.addOnFailureListener {
            Log.v(TAG, it.toString())
            showSnackbar("Error updating information")
        }
    }

    private fun onAnswerIncorrect(question: Question) {
        showProgress(true)
        firestore.runTransaction {
            var infoRef = firestore.collection("UserInfo").document(user!!.uid)
            var addRef = firestore.collection("UserInfo").document(question.creatorId)
            var questionRef = firestore.collection("Questions").document(question.id)

            // subtract 2 points from the users total points
            var points = it.get(infoRef)["points"].toString().toLong()
            points -= 2

            // add one point to the user that created the question
            var creatorPoints = it.get(addRef)["points"].toString().toLong()
            creatorPoints += 1

            // add one point to the bounty of the question
            var questionPoints = it.get(questionRef)["points"].toString().toLong()
            questionPoints += 1

            //update the information
            it.update(infoRef, "points", points)
            it.update(addRef, "points", creatorPoints)
            it.update(questionRef, "points", questionPoints)

            return@runTransaction
        }.addOnCompleteListener {
            showProgress(false)
            if (it.isSuccessful) {
                onRefresh()
                showSnackbar("That was the wrong answer please try again")
            }
        }.addOnFailureListener {
            Log.v(TAG, it.toString())
            showSnackbar("Error updating information")
        }
    }

    override fun onItemClicked(pos: Int, question: Question) {
        if (user!!.email.equals(question.creator)) {
            showSnackbar("You can't answer your own question")
            return
        }

        if (points < 2L) {
            showSnackbar("you don't have enough points to answer questions =(")
        } else {
            AnswerQuestionDialog.newInstance(question, questionCallback)
                    .show(this.supportFragmentManager, AnswerQuestionDialog.TAG)
        }
    }

    override fun onComplete(task: Task<QuerySnapshot>) {
        showProgress(false)

        if (task.isSuccessful) {
            adapter!!.clear()
            for (document in task.result) {
                val question = Question()
                question.id = document.id
                question.answer = (document.get("answer") as String?)!!
                question.question = (document.get("question") as String?)!!
                question.creator = (document.get("creator") as String?)!!
                question.creatorId = (document.get("creatorId") as String?)!!
                question.points = (document.get("points") as Long?)!!
                adapter!!.addItem(question)
            }
        } else {
            Log.e("error", task.exception!!.toString())
            showSnackbar("Error getting data please try again")
        }
    }

    override fun onRefresh() {
        val docRef = firestore.collection("Questions")
        docRef.whereEqualTo("answered", false).get().addOnCompleteListener(this)
        showProgress(true)
    }

    private var adapter: QuestionsAdapter? = null

    private val CREATE_REQ: Int = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CREATE_REQ -> {
                if (resultCode == Activity.RESULT_OK) {
                    onRefresh()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
