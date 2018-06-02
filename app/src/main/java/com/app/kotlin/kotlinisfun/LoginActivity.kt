package com.app.kotlin.kotlinisfun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*


class LoginActivity : BaseActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        login_btn.setOnClickListener {
            login()
        }

        signup_btn.setOnClickListener {
            val i = Intent(this, SignupActivity::class.java)
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("showInstructions", true)
            startActivity(i)
            finish()
        }
    }

    private fun login() {
        val u = email_et.text.toString()
        val p = password_et.text.toString()

        if (u.isEmpty() || p.isEmpty()) {
            Snackbar.make(login_btn, "please fill in both username and password", Snackbar.LENGTH_SHORT).show()
            return
        }

        showProgress(true)
        auth.signInWithEmailAndPassword(u, p)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    showProgress(false)
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Snackbar.make(login_btn, "Authentication failed.", Snackbar.LENGTH_SHORT).show()
                    }
                    return@OnCompleteListener
                })
    }

}
