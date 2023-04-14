package com.example.animal.DTO

data class ChatDTO(
    val uid: String? = null,
    val message: String? = null,
    val sendId: String? = null,
    var timeStamp: String? = null,
    val receiveId:String? = null
) {
    constructor() : this("", "", "", "", "")
}
