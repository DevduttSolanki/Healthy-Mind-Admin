package com.example.healthymindadmin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentsFragment : Fragment() {

    private lateinit var selectedDatesEditText: EditText
    private lateinit var btnDatePicker: Button
    private lateinit var edtDoctors: EditText
    private lateinit var btnApplyChanges: Button
    private val selectedDates = mutableSetOf<Long>()

    private lateinit var databaseReference: DatabaseReference
    private lateinit var doctorReference: DatabaseReference
    private lateinit var progressbar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointments, container, false)

        selectedDatesEditText = view.findViewById(R.id.tv_appo_date)
        btnDatePicker = view.findViewById(R.id.btn_appo_date)
        edtDoctors = view.findViewById(R.id.edt_doctors)
        btnApplyChanges = view.findViewById(R.id.btn_apply_changes)
        progressbar = view.findViewById(R.id.progressbar)

        progressbar.visibility = View.VISIBLE // Show progress bar when loading starts

        btnDatePicker.setOnClickListener {
            showDatePicker()
        }

        btnApplyChanges.setOnClickListener {
            saveSelectedDatesToFirebase()
        }

        // Disable editing on the selectedDatesEditText
        selectedDatesEditText.isEnabled = false

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("SelectedDatesAdmin")
        doctorReference = FirebaseDatabase.getInstance().reference.child("DoctorNamesAdmin")

        // Fetch and display doctor names from Firebase
        fetchDoctorNames()

        return view
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                selectedDates.add(selectedDate.timeInMillis)
                updateSelectedDatesText()
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set min date to current date to prevent selection of past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.setOnDismissListener {
            updateSelectedDatesText()
        }

        datePickerDialog.show()
    }

    private fun updateSelectedDatesText() {
        val selectedDatesText = StringBuilder()
        for (dateInMillis in selectedDates) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateInMillis
            selectedDatesText.append("${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}\n")
        }
        selectedDatesEditText.setText(selectedDatesText.toString().trim())
    }

    private fun saveSelectedDatesToFirebase() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Check if edt_doctors EditText is empty
        if (edtDoctors.text.trim().isEmpty()) {
            // Remove all doctor names from Firebase
            doctorReference.removeValue().addOnSuccessListener {
                // Show toast message
                Toast.makeText(requireContext(), "All doctor names removed", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // Handle failure
                Toast.makeText(requireContext(), "Failed to remove doctor names", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Save selected dates to Firebase
            for (dateInMillis in selectedDates) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateInMillis
                val formattedDate = dateFormat.format(calendar.time)

                val selectedDateRef = databaseReference.push()
                selectedDateRef.setValue(formattedDate)
            }

            // Save doctor names
            val doctors = edtDoctors.text.toString().trim().split("\n")
            for (doctor in doctors) {
                doctorReference.push().setValue(doctor)
            }

            // Show toast message
            Toast.makeText(requireContext(), "Changes applied successfully", Toast.LENGTH_SHORT).show()
        }

        selectedDatesEditText.setText("")
    }


    private fun fetchDoctorNames() {
        doctorReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctors = mutableListOf<String>()
                for (doctorSnapshot in dataSnapshot.children) {
                    val doctorName = doctorSnapshot.getValue(String::class.java)
                    doctorName?.let { doctors.add(it) }
                    progressbar.visibility = View.GONE // Show progress bar when loading starts
                }
                // Set the doctor names to the edtDoctors EditText
                edtDoctors.setText(doctors.joinToString("\n"))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Something went wrong,please try again later.", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }
        })
    }

}

