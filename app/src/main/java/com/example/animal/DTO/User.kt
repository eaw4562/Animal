package com.example.animal.DTO

data class User(
    var nickname: String,
    var email: String,
    var uid: String){

    constructor(): this("", "", "")

}
