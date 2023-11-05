package com.example.animegan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class ImageUtils {
    companion object {
        private fun getImageToShare(activity: AppCompatActivity, bitmap: Bitmap): Uri {
            val imageFolder = File(activity.cacheDir, "images")
            var uri: Uri? = null
            try {
                imageFolder.mkdirs()
                val file = File(imageFolder, "shared_file.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                uri = FileProvider.getUriForFile(activity, "com.example.animegan.fileprovider", file)
            } catch (e: Exception) {
                Toast.makeText(activity, "" + e.message, Toast.LENGTH_LONG).show();
            }
            return uri!!
        }

        fun share(activity: AppCompatActivity, imageView: ImageView) {
            val bitmapDrawable: BitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            val uri: Uri = ImageUtils.getImageToShare(activity, bitmap)
            val intent = Intent(Intent.ACTION_SEND)

            // putting uri of image to be shared
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            // Add subject Here
            intent.putExtra(Intent.EXTRA_SUBJECT, "Converted Image")

            // setting type to image
            intent.type = "image/jpg"


            // calling startActivity() to share
            startActivity(activity, Intent.createChooser(intent, "Share Via"), Bundle())
        }
    }
}