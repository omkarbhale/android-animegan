package com.example.animegan

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

class StorageHandler() {
    lateinit var root: File

    companion object {
        private var instance: StorageHandler? = null
        fun instance(): StorageHandler {
            if (instance == null) {
                instance = StorageHandler(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/anime-gan")
            }
            return instance as StorageHandler
        }
    }

    private constructor(rootPath: String) : this() {
        this.root = File(rootPath)
        if (!root.exists()) root.mkdirs()
    }

    public fun saveFile(name: String, bytes: ByteArray, applicationContext: Context): Uri {
        val file = File(root, name)
        val outputStream = FileOutputStream(file)
        outputStream.write(bytes)
        outputStream.close()

        MediaScannerConnection.scanFile(applicationContext, arrayOf(file.toString()), null, null)
        return Uri.fromFile(file)
    }

    public fun getContentFilePath(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            filePath = it.getString(columnIndex)
        }
        return filePath
    }
}