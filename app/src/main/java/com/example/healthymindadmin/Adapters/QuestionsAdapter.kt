package com.example.healthymindadmin.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.QuestionModel
import com.example.healthymindadmin.R
import com.example.healthymindadmin.databinding.ItenQuestionBinding



class QuestionsAdapter(var context: Context, list: ArrayList<QuestionModel>) :
    RecyclerView.Adapter<QuestionsAdapter.viewHolder>() {
    var list: ArrayList<QuestionModel>

    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.iten_question, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val model: QuestionModel = list[position]
        holder.binding.question.text = model.question
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItenQuestionBinding

        init {
            binding = ItenQuestionBinding.bind(itemView)
        }
    }
}