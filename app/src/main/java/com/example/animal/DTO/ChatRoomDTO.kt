package com.example.animal.DTO

data class ChatRoomDTO(
    val id: String? = null,
    val senderUid: String? = null,
    val receiverUid: String? = null,
    val senderEmail: String? = null,
    val receiverEmail: String? = null,
    val lastMessage: String? = null,
    val timestamp: Long = 0
){
    constructor() : this("","","","","","")
}
