package com.example.animal

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

    fun getRealPathFromURI(context: Context, uri: Uri): String {
    var filePath = ""

    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)

    if (cursor != null && cursor.moveToFirst()) {
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        filePath = cursor.getString(columnIndex)
        cursor.close()
    }
    return filePath
}
