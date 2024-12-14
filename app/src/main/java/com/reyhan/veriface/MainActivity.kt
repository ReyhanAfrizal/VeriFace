package com.reyhan.veriface

import VerifaceAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reyhan.veriface.viewmodel.VerifaceViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var verifaceAdapter: VerifaceAdapter
    private lateinit var searchEditText: EditText
    private val verifaceViewModel: VerifaceViewModel by viewModels()

    // ActivityResultLauncher for getting result from DetailActivity
    private lateinit var deleteResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Edge-to-edge UI support
        enableEdgeToEdge()

        // Initialize the views
        recyclerView = findViewById(R.id.lstVerifaceList)
        searchEditText = findViewById(R.id.txtSearch)
        val searchButton: Button = findViewById(R.id.btnSearch)
        val goAddButton: Button = findViewById(R.id.btnAdd)

        // Set RecyclerView properties
        recyclerView.layoutManager = LinearLayoutManager(this)
        verifaceAdapter = VerifaceAdapter(emptyList()) { veriface ->
            // Handle item click and navigate to DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("VERIFACE_DATA", veriface)  // Passing selected item
            deleteResultLauncher.launch(intent)  // Start activity for result
        }
        recyclerView.adapter = verifaceAdapter

        // Observing data from the ViewModel
        verifaceViewModel.verifaceList.observe(this) { verifaceList ->
            verifaceAdapter = VerifaceAdapter(verifaceList) { veriface ->
                // Handle item click and navigate to DetailActivity
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("VERIFACE_DATA", veriface)  // Passing selected item
                deleteResultLauncher.launch(intent)  // Start activity for result
            }
            recyclerView.adapter = verifaceAdapter
        }

        // Fetch data when the activity is created
        verifaceViewModel.getVerifaceData()

        // Set up the search button functionality
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                filterRecyclerView(query) // Filter the RecyclerView items based on the search query
            } else {
                // Optionally, fetch or display all items when the query is empty
                verifaceViewModel.getVerifaceData()
            }
        }

        // Set up the Add button functionality
        goAddButton.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        // Edge-to-edge layout adjustments for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Register the activity result launcher to handle the result from DetailActivity
        deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Refresh the list when the result is OK (deletion successful)
                verifaceViewModel.getVerifaceData()
            }
        }
    }

    private fun filterRecyclerView(query: String) {
        val filteredList = verifaceViewModel.verifaceList.value?.filter { veriface ->
            veriface?.namaKonsumen?.contains(query, ignoreCase = true) == true
        }
        filteredList?.let {
            verifaceAdapter = VerifaceAdapter(it) { veriface ->
                // Handle item click and navigate to DetailActivity
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("VERIFACE_DATA", veriface)  // Passing selected item
                deleteResultLauncher.launch(intent)  // Start activity for result
            }
            recyclerView.adapter = verifaceAdapter
        } ?: run {
            Toast.makeText(this, "No matches found", Toast.LENGTH_SHORT).show()
        }
    }
}
