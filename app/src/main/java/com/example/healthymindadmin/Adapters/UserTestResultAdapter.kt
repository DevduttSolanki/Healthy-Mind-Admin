package com.example.healthymindadmin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.UserTestResultModel
import com.example.healthymindadmin.R

class UserTestResultAdapter( private var resultList : List<UserTestResultModel>,
                             private val onItemClick: (String,String) -> Unit)
    : RecyclerView.Adapter<UserTestResultAdapter.ResultViewHolder>() {


    interface OnSendClickListener {
        fun onSendClick(resultId: String, docAdvise: String)
    }
    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId: TextView = itemView.findViewById(R.id.tv_uid1)
        val resultId: TextView = itemView.findViewById(R.id.tv_resultId1)
        val categoryName: TextView = itemView.findViewById(R.id.tv_categoryName1)
        val resultText: TextView = itemView.findViewById(R.id.tv_result1)
        val btnSend: Button = itemView.findViewById(R.id.btnSend)
        var docAdvise: EditText = itemView.findViewById(R.id.doc_advise)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):UserTestResultAdapter.ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_test_results, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserTestResultAdapter.ResultViewHolder, position: Int) {
        val currentItem = resultList[position]
        holder.userId.text = currentItem.userId
        holder.resultId.text = currentItem.resultId
        holder.categoryName.text = currentItem.categoryName
        holder.resultText.text = currentItem.resultText
        //holder.docAdvise.text = currentItem.


        // Clear the text in the docAdvise EditText
        holder.docAdvise.setText("")

        // Set click listener for the button
        holder.btnSend.setOnClickListener {
            val docAdvise = holder.docAdvise.text.toString().trim() // Get doctor's advice from EditText
            onItemClick(currentItem.resultId, docAdvise) // Pass both resultId and docAdvise to the listener
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    fun setData(data: List<UserTestResultModel>) {
        resultList = data.toMutableList() // Assign new data to resultList
        notifyDataSetChanged()
    }
}