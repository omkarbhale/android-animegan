package com.example.animegan

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ImageConverter() {

    companion object {
//        fun getContentFilePath(context: Context, uri: Uri): String? {
//            var filePath: String? = null
//            val projection = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor = context.contentResolver.query(uri, projection, null, null, null)
//            cursor?.use {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                it.moveToFirst()
//                filePath = it.getString(columnIndex)
//            }
//            return filePath
//        }

        // Function to convert an image
        suspend fun convertImage(client: OkHttpClient, context: Context, imageUri: Uri, version: String): ByteArray? {
            return withContext(Dispatchers.IO) {
                val path:String? = StorageHandler.instance().getContentFilePath(context, imageUri)
                if (path == null) {
                    Toast.makeText(context, "Could not load image", Toast.LENGTH_SHORT).show()
                    return@withContext null
                }
                val imageFile = File(path);

                val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.jpg", imageRequestBody)
                    .build()

                val request = Request.Builder()
                    .url("http://192.168.124.114:5000/convert-to-anime/$version")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    return@withContext response.body?.bytes()
                }

                return@withContext null
            }
        }
    }
}
