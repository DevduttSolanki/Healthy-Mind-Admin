package com.example.healthymindadmin.Models

data class AppointmentModel(
    var doctorName: String? = "",
    var appointmentDate: String? = "",
    var appointmentTime: String? = "",
    var status: String = ""
) {
    // Default constructor required by Firebase
    constructor() : this("", "", "", "")
}
