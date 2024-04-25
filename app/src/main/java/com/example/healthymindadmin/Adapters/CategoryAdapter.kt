package com.example.healthymindadmin.Adapters
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.CategoryModel
import com.example.healthymindadmin.QuestionsFragment
import com.example.healthymindadmin.R
import com.example.healthymindadmin.databinding.ItemCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(model)
            true // Consume the long click event
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var binding: ItemCategoryBinding = ItemCategoryBinding.bind(itemView)
    }

    private fun showDeleteConfirmationDialog(model: CategoryModel) {
        AlertDialog.Builder(context)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Yes") { _, _ ->
                deleteCategory(model.categoryname!!)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCategory(categoryName: String) {
        val position = list.indexOfFirst { it.categoryname == categoryName }
        if (position != -1) {
            list.removeAt(position)
            notifyItemRemoved(position)

            // Delete from Firebase
            val query = FirebaseDatabase.getInstance().getReference("categories")
                .orderByChild("categoryname").equalTo(categoryName)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            dataSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
