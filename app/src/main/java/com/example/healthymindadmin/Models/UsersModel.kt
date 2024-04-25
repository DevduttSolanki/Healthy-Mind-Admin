package com.example.healthymindadmin.Models

data class UsersModel(
    val uid: String,
    val name: String,
    val email: String,
    val password: String
)
{
    // Default constructor required by Firebase
    constructor() : this("", "", "", "")
}