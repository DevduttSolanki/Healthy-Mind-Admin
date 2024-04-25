package com.example.healthymindadmin


import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthymindadmin.Adapters.CategoryAdapter
import com.example.healthymindadmin.Models.CategoryModel
import com.example.healthymindadmin.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var addCategoryImg: CircleImageView
    private lateinit var txtEnterCategoryName: EditText
    private lateinit var btnUploadCategory: Button

    private lateinit var viewFetchImg: View
    private lateinit var imageUri: Uri

    private lateinit var dialog: Dialog
    private lateinit var progressDialog: ProgressDialog

    private var list: ArrayList<CategoryModel> = ArrayList()
    private lateinit var adapter: CategoryAdapter
    private lateinit var progressbar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        list = ArrayList()

        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_add_category)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Uploading")
        progressDialog.setMessage("Please wait.")

        progressbar = binding.progressbar
        btnUploadCategory = dialog.findViewById(R.id.btnUploadCategory)
        txtEnterCategoryName = dialog.findViewById(R.id.txtEnterCategoryName)
        addCategoryImg = dialog.findViewById(R.id.addCategoryImg)
        viewFetchImg = dialog.findViewById(R.id.viewFetchImg)

        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.recycActivityMain.layoutManager = layoutManager

        adapter = CategoryAdapter(requireActivity(), list)
        binding.recycActivityMain.adapter = adapter

        progressbar.visibility = View.VISIBLE // Show progress bar when loading starts

        database.reference.child("categories").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                    for (dataSnapshot in snapshot.children) {
                        val categoryName =
                            dataSnapshot.child("categoryname").getValue(String::class.java)
                        val categoryImg =
                            dataSnapshot.child("categoryimg").getValue(String::class.java)
                        val categoryKey = dataSnapshot.key

                        categoryName?.let {
                            categoryImg?.let { it1 ->
                                categoryKey?.let { it2 ->
                                    list.add(
                                        CategoryModel(it, it1, it2)
                                    )
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                    progressbar.visibility = View.GONE
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Category does not exist.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), error.message, Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }
        })

        binding.tvAddCategory.setOnClickListener {
            dialog.show()
        }

        viewFetchImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1)
        }

        btnUploadCategory.setOnClickListener {
            val name = txtEnterCategoryName.text.toString()
            if (!::imageUri.isInitialized) {
                Toast.makeText(
                    requireActivity(),
                    "Please upload category Image.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (name.isEmpty()) {
                txtEnterCategoryName.error = "Enter category name"
            } else {
                progressDialog.show()
                uploadData()
            }
        }

        return view
    }

    private fun uploadData() {
        val categoryName = txtEnterCategoryName.text.toString()

        // Check if category name is empty
        if (categoryName.isEmpty()) {
            txtEnterCategoryName.error = "Enter category name"
            return
        }

        // Check if category name already exists
        database.reference.child("categories")
            .orderByChild("categoryname")
            .equalTo(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Category name already exists
                        Toast.makeText(requireActivity(), "Category name must be unique.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    } else {
                        // Category name is unique, proceed with upload
                        val reference = storage.reference.child("category")
                            .child(Date().time.toString())

                        reference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val categoryModel = CategoryModel(
                                    categoryName,
                                    uri.toString()
                                )

                                database.reference.child("categories").push()
                                    .setValue(categoryModel)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireActivity(), "Data Uploaded.", Toast.LENGTH_SHORT)
                                            .show()
                                        addCategoryImg.setImageResource(R.drawable.gallery)
                                        txtEnterCategoryName.setText("")
                                        progressDialog.dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                                        progressDialog.dismiss()
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {


            imageUri = data.data!!
            addCategoryImg.setImageURI(imageUri)
        }
    }
}

