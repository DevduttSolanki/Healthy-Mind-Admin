package com.example.healthymindadmin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.AppointmentModel
import com.example.healthymindadmin.R
import com.google.firebase.database.FirebaseDatabase

class AppointmentRequestAdapter(private val appointments: List<AppointmentModel>) :
    RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)

        holder.btnApprove.setOnClickListener {
            updateStatus(appointment.appointmentId, "Approved")
        }
        holder.btnReject.setOnClickListener {
            updateStatus(appointment.appointmentId, "Rejected")
        }
    }


    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctor_name_text_view)
        private val appointmentDateTextView: TextView = itemView.findViewById(R.id.appointment_date_text_view)
        private val appointmentTimeTextView: TextView = itemView.findViewById(R.id.appointment_time_text_view)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_text_view)
        val btnApprove: Button = itemView.findViewById(R.id.btn_approve)
        val btnReject: Button = itemView.findViewById(R.id.btn_reject)

        fun bind(appointment: AppointmentModel) {
            doctorNameTextView.text = appointment.doctorName
            appointmentDateTextView.text = appointment.appointmentDate
            appointmentTimeTextView.text = appointment.appointmentTime
            statusTextView.text = appointment.status
        }
    }

    private fun updateStatus(appointmentId: String, status: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("AppointmentRequests")
        databaseReference.child(appointmentId).child("status").setValue(status)
    }
}