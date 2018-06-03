package com.app.kotlin.kotlinisfun

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_question.view.*

/**
 * Created by deep on 5/30/18.
 */


class QuestionsAdapter(val context: Context, private val callbacks: Callbacks) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    var data: MutableList<Question> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val li = LayoutInflater.from(context)
        val view = li.inflate(R.layout.list_item_question, parent, false)
        return ViewHolder(view)
    }

    private val listener: View.OnClickListener? = View.OnClickListener {
        val pos = recyclerView.getChildAdapterPosition(it)
        callbacks.onItemClicked(pos, data[pos])
    }

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = data[position]
        holder.creator.text = question.creator
        holder.points.text = question.points.toString()
        holder.question.text = question.question
        holder.constraintLayout.setOnClickListener(listener)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return data.size
    }

    fun addItem(question: Question) {
        data.add(question)
        notifyItemInserted(data.size - 1)
    }

    fun clear() {
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    interface Callbacks {
        fun onItemClicked(pos: Int, question: Question)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val constraintLayout = view.constraint_layout!!
        val question = view.question_et!!
        val creator = view.creator!!
        val points = view.point_count!!
    }
}

