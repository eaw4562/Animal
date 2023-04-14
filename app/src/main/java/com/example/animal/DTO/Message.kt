package com.example.animal.DTO

data class Message(
    var message: String?,
    var sendId: String?
){
    constructor(): this("","")
}
