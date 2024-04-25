package com.example.healthymindadmin.Models

data class AppointmentModel(
    var appointmentId: String,
    var doctorName: String,
    var appointmentDate: String,
    var appointmentTime: String,
    var status: String = ""
) {
    // Default constructor required by Firebase
    constructor() : this("", "","", "", "")
}
