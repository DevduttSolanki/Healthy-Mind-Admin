package com.example.healthymindadmin.Models

data class UserTestResultModel(
    val userId: String,
    var resultId: String,
    val categoryName: String,
    val resultText: String
)
{
    // Default constructor required by Firebase
    constructor() : this("", "", "", "")
}
