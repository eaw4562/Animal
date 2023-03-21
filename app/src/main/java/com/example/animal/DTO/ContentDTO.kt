package com.example.animal.DTO

data class ContentDTO(
    var title : String? = null,
    var category : String? = null,
    var breed : String? = null,
    var age : String? = null,
    var vaccine : String? = null,
    var where : String? = null,
    var gender : String? = null,
    var spay : String? = null,
    var content : String? = null,
    var imageUrl : String? = null,
    var timeStamp: String? = null, // timeStamp 변수 추가
    var price : String? = null,
    //
    var uid : String? = null,
    var userId : String? = null

    )