package com.example.healthymindadmin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.databinding.ItemCategoryBinding
import com.squareup.picasso.Picasso


class CategoryAdapter(var context: Context, list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var list: ArrayList<CategoryModel>

    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model: CategoryModel = list[position]

        holder.binding.textViewCategoryNames.setText(model.categoryname)
        Picasso.get()
            .load(model.categoryname)
            .placeholder(R.drawable.gallery)
            .into(holder.binding.categoryImgs)

        holder.itemView.setOnClickListener {

            val intent = Intent(context, QuestionsFragment::class.java)
            intent.putExtra("category", model.categoryname)
            intent.putExtra("key", model.key)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemCategoryBinding

        init {
            binding = ItemCategoryBinding.bind(itemView)
        }
    }
}

