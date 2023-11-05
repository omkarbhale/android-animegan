package com.example.animegan

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ImagePicker(private val activity: AppCompatActivity) {
    private val IMAGEPICKER = 3001
    private lateinit var onImagePickedListener: (Uri?) -> Unit

    fun pickImage(onImagePickedListener: (Uri?) -> Unit) {
        val getImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        getImageIntent.type = "image/*"
        activity.startActivityForResult(getImageIntent, IMAGEPICKER)

        this.onImagePickedListener = onImagePickedListener
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != AppCompatActivity.RESULT_OK) return

        if (requestCode == IMAGEPICKER) {
            onImagePickedListener.invoke(data?.data)
        }
    }
}