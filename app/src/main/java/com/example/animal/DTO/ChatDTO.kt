package com.example.animal.DTO

data class ChatDTO(
    var uid: String? = null,
    var message: String? = null,
    var sendId: String? = null,
    var timestamp: Long? = null,
    var dateString : String? = null
) {
    constructor() : this("", "", "", 0, "")
}