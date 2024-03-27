package com.example.admin_java.Adapters

import QuestionModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.R
import com.example.healthymindadmin.databinding.ItenQuestionBinding

class QuestionsAdapter(
    var context: Context, var list: ArrayList<QuestionModel>,
    var categoryName: String, var listener: DeleteListener
) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.iten_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list.getOrNull(position) // Get item at position or null if position is out of bounds
        model?.let { question ->
            holder.bind(question)
            holder.itemView.setOnClickListener {
                listener.onLongClick(holder.adapterPosition, question.key)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItenQuestionBinding.bind(itemView)

        fun bind(question: QuestionModel) {
            binding.question.text = question.question
        }
    }

    interface DeleteListener {
        fun onLongClick(position: Int, id: String?)
    }
}
