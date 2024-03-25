package com.example.healthymindadmin.Adapters
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.CategoryModel
import com.example.healthymindadmin.QuestionsFragment
import com.example.healthymindadmin.R
import com.example.healthymindadmin.databinding.ItemCategoryBinding
import com.squareup.picasso.Picasso

class CategoryAdapter(private val context: Context, private val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: CategoryModel = list[position]

        holder.binding.textViewCategoryNames.text = model.categoryname
        Picasso.get()
            .load(model.categoryimg)
            .placeholder(R.drawable.gallery)
            .into(holder.binding.categoryImgs)

        holder.itemView.setOnClickListener {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = QuestionsFragment()

            // You can pass any necessary data to the fragment using arguments
            val bundle = Bundle()
            bundle.putString("categoryName", model.categoryname)
            bundle.putString("key", model.key)
            fragment.arguments = bundle

            // Replace the current fragment with QuestionsFragment
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
            fragmentTransaction.commit()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ItemCategoryBinding = ItemCategoryBinding.bind(itemView)
    }
}
