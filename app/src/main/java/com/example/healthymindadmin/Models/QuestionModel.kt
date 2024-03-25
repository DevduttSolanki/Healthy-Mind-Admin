package com.example.healthymindadmin.Models

class QuestionModel(
    var question: String = "",
    var optionA: String = "",
    var optionB: String = "",
    var optionC: String = "",
    var optionD: String = "",
    var key: String = "",
) {
    constructor() : this("", "", "", "", "", "")
}
