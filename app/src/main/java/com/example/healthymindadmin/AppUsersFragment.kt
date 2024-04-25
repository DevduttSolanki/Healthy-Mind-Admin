package com.example.healthymindadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Adapters.UserAdapter
import com.example.healthymindadmin.Models.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AppUsersFragment : Fragment() {

    private lateinit var appUsersRecyclerView: RecyclerView
    private lateinit var appUsersAdapter: UserAdapter
    private lateinit var appUserList: MutableList<UsersModel>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var progressbar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_users, container, false)

        appUsersRecyclerView = view.findViewById(R.id.app_users_recycler_view)
        progressbar = view.findViewById(R.id.progressbar)
        appUserList = mutableListOf()
        progressbar.visibility = View.VISIBLE

        appUsersAdapter = UserAdapter(appUserList, object : UserAdapter.DeleteListener {
            override fun onLongClick(position: Int, uid: String?) {
                uid?.let {
                    showDeleteConfirmationDialog(it)
                }
            }
        })
        appUsersRecyclerView.adapter = appUsersAdapter
        appUsersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appUserList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UsersModel::class.java)
                    user?.let {
                        appUserList.add(it)
                    }
                }
                appUsersAdapter.notifyDataSetChanged()
                progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Something went wrong,please try again later.", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }
        })

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        return view
    }

    private fun showDeleteConfirmationDialog(uid: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete User")
        builder.setMessage("Are you sure you want to delete this User Account?")

        builder.setPositiveButton("Yes") { _, _ ->
            deleteUserFromFirebase(uid)
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteUserFromFirebase(uid: String) {
        databaseReference.child(uid).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show()
            }
    }
}