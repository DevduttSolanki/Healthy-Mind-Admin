package com.example.healthymindadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Adapters.UserTestResultAdapter
import com.example.healthymindadmin.Models.UserTestResultModel
import com.example.healthymindadmin.databinding.FragmentUserTestResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserTestResultFragment : Fragment(), UserTestResultAdapter.OnSendClickListener {


    private var _binding: FragmentUserTestResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserTestResultAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var resultList: MutableList<UserTestResultModel>
    private lateinit var progressbar : ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserTestResultBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.myResultsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressbar = binding.progressbar

        resultList = mutableListOf()
        adapter = UserTestResultAdapter(resultList) { resultId, docAdvise ->
            onSendClick(resultId, docAdvise) // Pass resultId and docAdvise to the click listener
        }
        recyclerView.adapter = adapter // Set the adapter after initialization

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        progressbar.visibility = View.VISIBLE // Show progress bar when loading starts

        loaadResults()

        return view
    }

    override fun onSendClick(resultId: String, docAdvise: String) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("results")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                resultList.clear()
                for (appointmentSnapshot in snapshot.children) {
                    val appointmentId = appointmentSnapshot.key.toString()

                    // Check if the appointmentId matches the desired appointment
                    if (appointmentId == resultId) {
                        val doctorAdviceRef = appointmentSnapshot.child("doctorAdvice")

                        // Update the doctor's advice directly at the existing resultId node
                        doctorAdviceRef.ref.setValue(docAdvise)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Doctor's advice sent successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Failed to update doctor's advice,please try again later.", Toast.LENGTH_SHORT).show()
                            }

                        return  // Exit the loop once the update is done
                    }
                }

                // If the loop completes without finding a matching appointmentId
                Toast.makeText(requireContext(), "Appointment not found", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(),"Something went wrong, please try again later",Toast.LENGTH_LONG).show()
                progressbar.visibility = View.GONE
            }
        })
    }


    private fun loaadResults(){

        database = FirebaseDatabase.getInstance().getReference("results")

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                resultList.clear()

                for (resultSnapshot in snapshot.children) {

                    val results = resultSnapshot.getValue(UserTestResultModel::class.java)
                    results?.let {
                        it.resultId = resultSnapshot.key.toString() // Assuming appointmentId is set here
                        resultList.add(it)
                    }
                }
                adapter.setData(resultList) // Update the adapter with new data
                progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(),"Something went wrong,please try again later",Toast.LENGTH_LONG).show()
                progressbar.visibility = View.GONE
            }
        })

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}