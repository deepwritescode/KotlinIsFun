package com.app.kotlin.kotlinisfun

import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_signup.*


class SignupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        signup_btn.setOnClickListener {
            signup()
        }

    }

    private fun signup() {
        showProgress(true)
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        onSuccess()
                    } else {
                        // If sign in fails, display a message to the user.
                        showSnackbar("Couldn't signup the user, do you already have an account?")
                    }
                    return@OnCompleteListener
                })
    }

    private fun onSuccess() {
        val docData = HashMap<String, Any>()
        docData["points"] = 50

        FirebaseFirestore.getInstance().collection("UserInfo")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .set(docData)
                .addOnSuccessListener(OnSuccessListener<Void> {
                    showProgress(false)
                    finish()
                })
                .addOnFailureListener(OnFailureListener { e ->
                    showProgress(false)
                    finish()
                })

    }

}
