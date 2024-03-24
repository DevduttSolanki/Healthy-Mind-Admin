package com.example.healthymindadmin


class CategoryModel {
    var categoryname: String? = null
    var categoryimg: String? = null
    var key: String? = null


    constructor(categoryname: String?, categoryimg: String?, key: String?, setNum: Int) {
        this.categoryname = categoryname
        this.categoryimg = categoryimg
        this.key = key

    }

    constructor()
}

