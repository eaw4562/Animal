package com.example.animal.DTO

data class User(
    var name: String,
    var email: String,
    var uid: String){

    constructor(): this("", "", "")

}
