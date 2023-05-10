package com.example.animal.DTO

data class ChatDTO(
    var uid: String? = null,
    var message: String? = null,
    var sendId: String? = null,
    var timestamp: Long? = null,
    var dateString : String? = null,
    var isRead : Boolean? = false
) {
    fun getIsRead() : Boolean {
        return isRead!!
    }
    constructor() : this("", "", "", 0, "", false)
}