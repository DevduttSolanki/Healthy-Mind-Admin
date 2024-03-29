package com.example.healthymindadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        databaseReference = FirebaseDatabase.getInstance().getReference("AppointmentRequests")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointmentsList.clear()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                    appointment?.let {
                        appointmentsList.add(it)
                    }
                }
                appointmentsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return view}
}