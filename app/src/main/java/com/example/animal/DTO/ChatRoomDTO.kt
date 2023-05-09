package com.example.animal.DTO

data class ChatRoomDTO(
    val id: String? = null,
    var senderUid: String? = null,
    var reciverUid: String? = null,
    val senderEmail: String? = null,
    var receiverEmail: String? = null,
    var lastMessage: String? = null,
    var dateString: Long = 0
){
    constructor() : this("","","","","","")
}
