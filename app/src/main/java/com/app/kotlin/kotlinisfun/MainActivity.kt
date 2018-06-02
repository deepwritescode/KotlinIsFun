package com.app.kotlin.kotlinisfun

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
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


class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
        OnCompleteListener<QuerySnapshot>, QuestionsAdapter.Callbacks {

    val db = FirebaseFirestore.getInstance()

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser == null){
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //show instructions when user logs in
        if (intent.getBooleanExtra("showInstructions", false)) {
            InstructionsDialog.newInstance().show(supportFragmentManager, InstructionsDialog.TAG)
        }

        swipe_refresh_layout.setOnRefreshListener(this)
        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))

        adapter = QuestionsAdapter(this, this)

        val itemDecor = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        recycler.addItemDecoration(itemDecor)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        getData()

        fab.setOnClickListener({

        })
    }

    private val questionCallback = object : AnswerQuestionDialog.EventListener {
        override fun onOkay(answer: String, question: Question) {
            Log.v(TAG, answer)
            Log.v(TAG, question.answer)

            val correct = answer.toLowerCase().equals(question.answer.toLowerCase())
            if (correct) {
                onAnswerCorrect(question)
            } else {
                onAnswerIncorrect(question)
            }
        }
    }

    private fun onAnswerCorrect(question: Question) {

    }

    private fun onAnswerIncorrect(question: Question) {

    }

    override fun onItemClicked(pos: Int, question: Question) {
        AnswerQuestionDialog.newInstance(question, questionCallback)
                .show(supportFragmentManager, AnswerQuestionDialog.TAG)
    }

    override fun onComplete(task: Task<QuerySnapshot>) {
        showProgress(false)

        if (task.isSuccessful) {
            adapter!!.clear()
            for (document in task.result) {
                val question = Question()
                question.answer = (document.get("answer") as String?)!!
                question.question = (document.get("question") as String?)!!
                question.creator = (document.get("creator") as String?)!!
                question.points = (document.get("points") as Long?)!!
                adapter!!.addItem(question)
            }
        } else {
            Log.e("error", task.exception!!.toString())
            showSnackbar("Error getting data please try again")
        }
    }

    override fun onRefresh() {
        getData()
    }

    private var adapter: QuestionsAdapter? = null

    private fun getData() {
        swipe_refresh_layout.isRefreshing = true

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Questions")
        docRef.get().addOnCompleteListener(this)
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
