package com.reyhan.veriface

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import com.reyhan.veriface.model.ResponseVeriface
import com.reyhan.veriface.viewmodel.VerifaceViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var txtNamaKonsumen: TextView
    private lateinit var txtTanggal: TextView
    private lateinit var imgKtp: ImageView
    private lateinit var imgSelfie: ImageView
    private lateinit var txtResult: TextView
    private lateinit var txtNotes: TextView
    private lateinit var btnDelete: Button  // Add the delete button
    private lateinit var btnEdit: Button  // Add the edit button

    private lateinit var veriface: ResponseVeriface  // Hold the data to be deleted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Initialize views
        txtNamaKonsumen = findViewById(R.id.txtNamaLengkap)
        txtTanggal = findViewById(R.id.txtTime)
        imgKtp = findViewById(R.id.imgKtpDetail)
        imgSelfie = findViewById(R.id.imgFaceDetail)
        txtResult = findViewById(R.id.txtResultDetail)
        txtNotes = findViewById(R.id.txtNotesDetail)
        btnDelete = findViewById(R.id.btnDeleteDetail)  // Initialize the delete button
        btnEdit = findViewById(R.id.btnEditDetail)  // Initialize the edit button

        // Get the passed data from Intent
        veriface = intent.getParcelableExtra("VERIFACE_DATA")!!

        // Set data to views
        txtNamaKonsumen.text = veriface.namaKonsumen
        txtTanggal.text = veriface.createdDataAt
        txtResult.text = veriface.result
        txtNotes.text = veriface.notes

        // Load images using Picasso
        Picasso.get().load(veriface.fotoKtp).into(imgKtp)
        Picasso.get().load(veriface.fotoSelfie).into(imgSelfie)

        // Set up the delete button click listener
        btnDelete.setOnClickListener {
            val id = veriface.id
            Log.d("DetailActivity", "Veriface ID: $id")
            if (id != null) {
                showDeleteConfirmationDialog(id)
            } else {
                Log.e("DetailActivity", "ID is null")
                Toast.makeText(this, "Invalid data ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the Edit button click
        btnEdit.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.putExtra("EDIT_VERIFACE_DATA", veriface)  // Pass the data
            startActivityForResult(intent, REQUEST_EDIT)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to show delete confirmation dialog
    private fun showDeleteConfirmationDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this data?")
            .setPositiveButton("Yes") { _, _ ->
                deleteVerifaceData(id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Function to delete data
    private fun deleteVerifaceData(id: Int) {
        val viewModel = VerifaceViewModel(application)

        // Call the ViewModel to delete the data
        viewModel.deleteVerifaceData(id)

        // Observe the status of the delete operation
        viewModel.operationStatus.observe(this, { status ->
            if (status == "Data deleted successfully") {
                Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()

                // Send result back to MainActivity
                setResult(Activity.RESULT_OK)
                finish()  // Close the current activity and return to the previous screen
            } else {
                Toast.makeText(this, "Failed to delete data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val REQUEST_EDIT = 2
    }
}


