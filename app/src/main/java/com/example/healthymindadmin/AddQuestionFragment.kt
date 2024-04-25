package com.example.healthymindadmin

import QuestionModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthymindadmin.databinding.FragmentAddQuestionBinding
import com.google.firebase.database.FirebaseDatabase

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
                null
            )

            if(optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()){

                Toast.makeText(requireContext(), "Please enter all options.", Toast.LENGTH_SHORT).show()
                if (optionA.isEmpty()){
                    binding.editTextText7.error = "Please add option A"
                }
                if (optionB.isEmpty()){
                    binding.editTextText8.error = "Please add option B"
                }
                if (optionC.isEmpty()){
                    binding.editTextText9.error = "Please add option C"
                }
                if (optionD.isEmpty()){
                    binding.editTextText11.error = "Please add option D"
                }

            }else {

                val questionsRef = database!!.getReference("category-questions").child(categoryName!!).child("questions")
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
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}