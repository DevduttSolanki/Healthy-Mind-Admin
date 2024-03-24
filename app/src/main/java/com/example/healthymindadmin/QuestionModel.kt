package com.example.healthymindadmin


class QuestionModel {
    var question: String? = null
    var optionA: String? = null
    var optionB: String? = null
    var optionC: String? = null
    var optionD: String? = null
    var key: String? = null

    constructor(
        question: String?,
        optionA: String?,
        optionB: String?,
        optionC: String?,
        optionD: String?,
        key: String?,

    ) {
        this.question = question
        this.optionA = optionA
        this.optionB = optionB
        this.optionC = optionC
        this.optionD = optionD
        this.key = key
    }

    constructor()
}