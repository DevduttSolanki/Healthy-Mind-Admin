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
import com.example.healthymindadmin.Adapters.AppointmentRequestAdapter
import com.example.healthymindadmin.Models.AppointmentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AppointmentRequestsFragment : Fragment() {

    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsAdapter: AppointmentRequestAdapter
    private lateinit var appointmentsList: MutableList<AppointmentModel>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var progressbar : ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment_requests, container, false)

        appointmentsRecyclerView = view.findViewById(R.id.my_appointments_recycler_view)
        appointmentsList = mutableListOf()
        appointmentsAdapter = AppointmentRequestAdapter(appointmentsList)
        appointmentsRecyclerView.adapter = appointmentsAdapter
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressbar = view.findViewById(R.id.progressbar)

        progressbar.visibility = View.VISIBLE

        databaseReference = FirebaseDatabase.getInstance().getReference("AppointmentRequests")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointmentsList.clear()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                    appointment?.let {
                        it.appointmentId = appointmentSnapshot.key.toString() // Assuming appointmentId is set here
                        appointmentsList.add(it)
                    }
                }
                appointmentsAdapter.notifyDataSetChanged()
                progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(),"Something went wrong,please try again later",Toast.LENGTH_LONG).show()
                progressbar.visibility = View.GONE
            }
        })

        return view
    }
}