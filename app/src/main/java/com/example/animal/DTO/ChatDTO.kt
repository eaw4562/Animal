package com.example.animal.DTO

data class ChatDTO(
    var uid: String? = null,
    var message: String? = null,
    var sendId: String? = null,
   // var timeStamp: String? = null,
    var receiveId:String? = null
) {
    constructor() : this("", "", "", "")
}
