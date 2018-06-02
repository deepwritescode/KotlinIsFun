package com.app.kotlin.kotlinisfun

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar

abstract class BaseActivity : AppCompatActivity() {
    val TAG = this.javaClass.name!!

    var progressBar: ProgressBar? = null
    var toolbar: Toolbar? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        progressBar = findViewById(R.id.progress_bar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout!!.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorAccent))
            swipeRefreshLayout!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { refreshData() })
        }
    }

    protected fun refreshData() {

    }

    protected fun setBackButton(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
    }

    protected fun showKeyboard(show: Boolean) {
        if (show) {
            this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        } else {
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }


    protected fun showSnackbar(message: String) {
        if (AppBase.isDebug()) {
            Log.v("$TAG-snackbar", message)
        }
        toolbar?.let { Snackbar.make(it, message, BaseTransientBottomBar.LENGTH_LONG).show() }
    }

    protected fun showSnackbar(message: String, actionText: String, action: View.OnClickListener) {
        if (AppBase.isDebug()) {
            Log.v("$TAG-snackbar", message)
        }
        toolbar?.let { Snackbar.make(it, message, BaseTransientBottomBar.LENGTH_LONG).setAction(actionText, action).show() }
    }

    private fun hideProgressBar() {
        if (swipeRefreshLayout == null) return

        swipeRefreshLayout!!.isRefreshing = false
    }

    private fun showProgressBar() {
        if (swipeRefreshLayout == null) return

        swipeRefreshLayout!!.isRefreshing = true
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    protected fun showProgress(show: Boolean) {
        showKeyboard(!show)

        if (show) showProgressBar()
        else hideProgressBar()

        if (progressBar == null) {
            progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        }

        if (progressBar == null) {
            return
        }

        if (show) {
            progressBar!!.visibility = View.VISIBLE
        } else {
            progressBar!!.visibility = View.INVISIBLE
//            hideNoConnection()
        }
    }

}