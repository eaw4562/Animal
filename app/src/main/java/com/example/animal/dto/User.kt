package com.example.animal.dto

data class User(
    var nickname: String,
    var email: String,
    var uid: String){

    constructor(): this("", "", "")

}
