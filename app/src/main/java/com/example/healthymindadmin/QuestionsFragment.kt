package com.example.healthymindadmin

import QuestionModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin_java.Adapters.QuestionsAdapter
import com.example.admin_java.Adapters.QuestionsAdapter.DeleteListener
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


        // new added
        adapter = QuestionsAdapter(requireContext(), list, categoryName, object : DeleteListener {

            override fun onLongClick(position: Int, id: String?) {

                if (id != null) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Delete question")
                    builder.setMessage("Are you sure you want to delete this question?")

                    builder.setPositiveButton("Yes") { dialog, which ->
                        if (isAdded) {
                            database.reference.child("category-questions").child(categoryName).child("questions")
                                .child(id).removeValue()
                                .addOnSuccessListener {
                                    if (isAdded) {
                                        Toast.makeText(requireActivity(), "Question deleted.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }


                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    val alertDialog = builder.create()
                    alertDialog.show()

                } else {

                    Log.e("QuestionsFragment", "onLongClick: id is null")
                    // Handle the case where id is null, if needed
                }
            }
        })





        binding.recyQuestions.adapter = adapter


        database.reference.child("category-questions").child(categoryName).child("questions")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded) {
                        val previousItemCount = list.size
                        list.clear()

                        if (snapshot.exists()) {
                            Log.d("QuestionsFragment", "onDataChange: Data exists")
                            for (dataSnapshot in snapshot.children) {
                                val model = dataSnapshot.getValue(QuestionModel::class.java)
                                model?.key = dataSnapshot.key!!
                                model?.let { list.add(it) }
                            }
                        } else {
                            Toast.makeText(requireContext(), "No questions found for this category.", Toast.LENGTH_SHORT).show()
                        }

                        // Notify adapter if item count changed
                        if (previousItemCount != list.size) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                    Log.e("QuestionsFragment", "onCancelled: ${error.message}")
                    Toast.makeText(requireContext(), "Something went wrong, please try again later: ${error.message}", Toast.LENGTH_SHORT).show()

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