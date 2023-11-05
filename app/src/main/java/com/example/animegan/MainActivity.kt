package com.example.animegan

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet.Layout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    private lateinit var imagePicker: ImagePicker
    private var selectedImageUri: Uri? = null

    private lateinit var previewImage: ImageView
    private lateinit var outputImage: ImageView
    private lateinit var outImageLayout: LinearLayout

    private var client: OkHttpClient = OkHttpClient()
    private val mainScope = MainScope()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewImage = findViewById(R.id.imageView)
        outputImage = findViewById(R.id.outImage)
        outImageLayout = findViewById(R.id.outLayout)
        imagePicker = ImagePicker(this)

        findViewById<Button>(R.id.buttonSelect).setOnClickListener {
            imagePicker.pickImage { uri ->
                selectedImageUri = uri
                previewImage.setImageURI(uri)
            }
        }

        findViewById<Button>(R.id.buttonProceedV1).setOnClickListener {
            handleProceed("v1")
        }
        findViewById<Button>(R.id.buttonProceedV2).setOnClickListener {
            handleProceed("v2")
        }
        findViewById<Button>(R.id.buttonShare).setOnClickListener {
            ImageUtils.share(this, outputImage)
        }
    }

    private fun handleProceed(version: String) {
        mainScope.launch {
            if (selectedImageUri == null) {
                Toast.makeText(this@MainActivity, "Image not selected!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Make API Call
            val bytes: ByteArray? = ImageConverter.convertImage(client, this@MainActivity, selectedImageUri as Uri, version)
            if (bytes == null) {
                Toast.makeText(this@MainActivity, "API request failed :(", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val fileName = "image_" + System.currentTimeMillis().toString() + ".jpg"
            val uri: Uri = StorageHandler.instance().saveFile(fileName, bytes, this@MainActivity)
            Toast.makeText(this@MainActivity, "Saved $fileName", Toast.LENGTH_SHORT).show()

            // Display image
            outputImage.setImageURI(uri)
            outImageLayout.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

}