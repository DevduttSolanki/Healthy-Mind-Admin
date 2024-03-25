package com.example.healthymindadmin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthymindadmin.Adapters.QuestionsAdapter
import com.example.healthymindadmin.Models.QuestionModel
import com.example.healthymindadmin.databinding.FragmentQuestionsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class QuestionsFragment : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var list: ArrayList<QuestionModel>
    private lateinit var adapter: QuestionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = "QUESTIONS"

        database = FirebaseDatabase.getInstance()
        list = ArrayList()

        val categoryName = requireArguments().getString("categoryName") ?: ""

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyQuestions.layoutManager = layoutManager

        adapter = QuestionsAdapter(requireContext(), list)
        binding.recyQuestions.adapter = adapter

        database.reference.child("categories").child(categoryName).child("questions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    if (snapshot.exists()) {
                        for (dataSnapshot in snapshot.children) {
                            val model = dataSnapshot.getValue(QuestionModel::class.java)
                            model?.key = dataSnapshot.key!!
                            model?.let { list.add(it) }
                        }
                        adapter.notifyDataSetChanged()
                        Log.d("QuestionsFragment", "Questions loaded successfully. Count: ${list.size}")
                    } else {
                        Toast.makeText(requireContext(), "No questions found for this category.", Toast.LENGTH_SHORT).show()
                        Log.d("QuestionsFragment", "No questions found for this category.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                    Toast.makeText(requireContext(), "Database operation cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("QuestionsFragment", "Database operation cancelled: ${error.message}")
                }
            })

        binding.tvAddQuestion.setOnClickListener {
            val fragment = AddQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString("category", categoryName)
                }
            }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)  // Optional: Add fragment to back stack
            transaction.commit()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}