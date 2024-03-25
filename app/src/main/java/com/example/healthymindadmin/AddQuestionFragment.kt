package com.example.healthymindadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthymindadmin.Models.QuestionModel
import com.example.healthymindadmin.databinding.FragmentAddQuestionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddQuestionFragment : Fragment() {

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!

    private var categoryName: String? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddQuestionBinding.inflate(inflater, container, false)
        val view = binding.root

        categoryName = requireArguments().getString("category")
        database = FirebaseDatabase.getInstance()

        binding.btnUploadQuestion.setOnClickListener {

            val questionText = binding.txtInputQuestion.text.toString().trim()
            if (questionText.isEmpty()) {
                binding.txtInputQuestion.error = "Question is required"
                return@setOnClickListener
            }

            val optionA = (binding.ansContainer.getChildAt(0) as EditText).text.toString().trim()
            val optionB = (binding.ansContainer.getChildAt(1) as EditText).text.toString().trim()
            val optionC = (binding.ansContainer.getChildAt(2) as EditText).text.toString().trim()
            val optionD = (binding.ansContainer.getChildAt(3) as EditText).text.toString().trim()

            val model = QuestionModel(
                question = questionText,
                optionA = optionA,
                optionB = optionB,
                optionC = optionC,
                optionD = optionD,
                key = ""
            )

            val questionsRef = database!!.getReference("categories").child(categoryName!!).child("questions")
            questionsRef.push().setValue(model)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Question added successfully", Toast.LENGTH_SHORT).show()
                        // Clear EditText fields
                        binding.txtInputQuestion.text?.clear()
                        for (i in 0 until binding.ansContainer.childCount) {
                            (binding.ansContainer.getChildAt(i) as EditText).text?.clear()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to add question", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Update question keys with category keys
        val categoriesRef = database!!.getReference("categories")
        categoriesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (categorySnapshot in snapshot.children) {

                    val categoryKey = categorySnapshot.key
                    categoryKey?.let {

                        val questionsRef = categoriesRef.child(categoryKey).child("questions")
                        questionsRef.addValueEventListener(object : ValueEventListener {

                            override fun onDataChange(questionSnapshot: DataSnapshot) {

                                for (questionSnapshot in questionSnapshot.children) {
                                    val questionKey = questionSnapshot.key
                                    // Update the key field of the question with the category key
                                    questionsRef.child(questionKey!!).child("key").setValue(categoryKey)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle onCancelled
                                Toast.makeText(requireContext(), "Database operation cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
                Toast.makeText(requireContext(), "Database operation cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}