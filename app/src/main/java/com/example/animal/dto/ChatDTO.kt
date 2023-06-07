package com.example.animal.dto

data class ChatDTO(
    val uid : String? = null,
    val message : String? = null,
    val receiverUid: String? = null,
    val timestamp: Long? = null,
    val dateString: String? = null,
    var read: Boolean = false
){
    constructor() : this("","","",0,"", false)
}
