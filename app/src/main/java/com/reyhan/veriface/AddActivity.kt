package com.reyhan.veriface

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.reyhan.veriface.model.ResponseVeriface
import com.reyhan.veriface.viewmodel.VerifaceViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private val verifaceViewModel: VerifaceViewModel by viewModels()
    private var fileKtp: File? = null
    private var fileSelfie: File? = null
    private lateinit var imageViewKtp: ImageView
    private lateinit var imageViewSelfie: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)

        val editData = intent.getParcelableExtra<ResponseVeriface>("EDIT_VERIFACE_DATA")

        if (editData != null) {
            populateFields(editData)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViewKtp = findViewById(R.id.imgInputKtp)
        imageViewSelfie = findViewById(R.id.imgInputSelfie)
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.btnKtp).setOnClickListener {
            requestPermissionsAndCaptureImage("KTP")
        }

        findViewById<Button>(R.id.btnSelfie).setOnClickListener {
            requestPermissionsAndCaptureImage("Selfie")
        }

        val btnSend: Button = findViewById(R.id.btnSend)
        val namaKonsumenField: EditText = findViewById(R.id.btnInputNamaKonsumen)
        val notesField: EditText = findViewById(R.id.txtInputNotes)

        btnSend.setOnClickListener {
            val namaKonsumen = namaKonsumenField.text.toString()
            val notes = notesField.text.toString()

            // Validation: Check if fields are empty
            if (namaKonsumen.isEmpty()) {
                Toast.makeText(this, "Nama Konsumen is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fileKtp == null && editData == null) {
                Toast.makeText(this, "Please capture or select the KTP image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fileSelfie == null && editData == null) {
                Toast.makeText(this, "Please capture or select the Selfie image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE // Show the loading indicator

            // Create RequestBody for fields
            val namaKonsumenBody = RequestBody.create("text/plain".toMediaTypeOrNull(), namaKonsumen)
            val notesBody = RequestBody.create("text/plain".toMediaTypeOrNull(), notes)
            val resultBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "0")

            // Create image parts for KTP and Selfie
            val fotoKtpPart = fileKtp?.let { createImagePart("fotoKtp", it) }
            val fotoSelfiePart = fileSelfie?.let { createImagePart("fotoSelfie", it) }

            // If we are editing data, update it; otherwise, add new data
            if (editData != null) {
                // Update existing data
                editData.id?.let { it1 ->
                    verifaceViewModel.updateVerifaceData(
                        it1,
                        namaKonsumenBody,
                        notesBody,
                        resultBody,
                        fotoKtpPart,
                        fotoSelfiePart
                    )
                }
            } else {
                // Add new data
                if (fotoKtpPart != null && fotoSelfiePart != null) {
                    verifaceViewModel.addVerifaceData(
                        namaKonsumenBody,
                        notesBody,
                        resultBody,
                        fotoKtpPart,
                        fotoSelfiePart
                    )
                } else {
//                    Toast.makeText(this, "Error: KTP or Selfie image missing", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE // Hide the loading indicator
                    return@setOnClickListener
                }
            }

            // Observe the operation status and handle success or failure
            verifaceViewModel.operationStatus.observe(this) { status ->
                progressBar.visibility = View.GONE // Hide the loading indicator

                when (status) {
                    "Data added successfully", "Data updated successfully" -> {
                        Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
}

        private fun requestPermissionsAndCaptureImage(type: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            openImageSourceOptions(type)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun openImageSourceOptions(type: String) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        startActivityForResult(chooserIntent, if (type == "KTP") KTP_REQUEST_CODE else SELFIE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data

            when (requestCode) {
                KTP_REQUEST_CODE -> {
                    if (imageUri != null) {
                        imageViewKtp.setImageURI(imageUri)
                        fileKtp = getFileFromUri(imageUri) // Get the actual file from URI
                    } else {
                        val photo = data?.extras?.get("data") as Bitmap
                        imageViewKtp.setImageBitmap(photo)
                        fileKtp = saveBitmapToFile(photo, "ktp_image.jpg")
                    }
                }
                SELFIE_REQUEST_CODE -> {
                    if (imageUri != null) {
                        imageViewSelfie.setImageURI(imageUri)
                        fileSelfie = getFileFromUri(imageUri) // Get the actual file from URI
                    } else {
                        val photo = data?.extras?.get("data") as Bitmap
                        imageViewSelfie.setImageBitmap(photo)
                        fileSelfie = saveBitmapToFile(photo, "selfie_image.jpg")
                    }
                }
            }
        }
    }

    // Function to get the file from the URI (for gallery image selection)
    private fun getFileFromUri(uri: Uri): File? {
        val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.moveToFirst()
        val filePath = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        cursor?.close()
        return if (filePath != null) File(filePath) else null
    }


    private fun createImagePart(partName: String, file: File): MultipartBody.Part {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String): File? {
        val file = File(cacheDir, fileName)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val KTP_REQUEST_CODE = 101
        private const val SELFIE_REQUEST_CODE = 102
    }

    private fun populateFields(data: ResponseVeriface) {
        findViewById<EditText>(R.id.btnInputNamaKonsumen).setText(data.namaKonsumen)
        findViewById<EditText>(R.id.txtInputNotes).setText(data.notes)

        // Load images
        Picasso.get().load(data.fotoKtp).into(findViewById<ImageView>(R.id.imgInputKtp))
        Picasso.get().load(data.fotoSelfie).into(findViewById<ImageView>(R.id.imgInputSelfie))

        // Store file references for editing
        fileKtp = File(data.fotoKtp)  // If available locally
        fileSelfie = File(data.fotoSelfie)  // If available locally
    }
}
