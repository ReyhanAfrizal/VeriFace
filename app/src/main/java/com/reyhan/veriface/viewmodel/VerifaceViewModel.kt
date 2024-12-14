package com.reyhan.veriface.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reyhan.veriface.model.ResponseVeriface
import com.reyhan.veriface.services.NetworkConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifaceViewModel(application: Application) : AndroidViewModel(application) {

    private val _verifaceList = MutableLiveData<List<ResponseVeriface>>()
    val verifaceList: LiveData<List<ResponseVeriface>>
        get() = _verifaceList

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String>
        get() = _operationStatus

    // Fetch the list of ResponseVeriface data
    fun getVerifaceData() {
        NetworkConfig().getVerifaceService().getAllData()
            .enqueue(object : Callback<List<ResponseVeriface>> {
                override fun onResponse(call: Call<List<ResponseVeriface>>, response: Response<List<ResponseVeriface>>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _verifaceList.postValue(it)
                        } ?: run {
                            Toast.makeText(getApplication<Application>().applicationContext, "No data available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(getApplication<Application>().applicationContext, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ResponseVeriface>>, t: Throwable) {
                    Toast.makeText(getApplication<Application>().applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Add a new data entry
    fun addVerifaceData(
        namaKonsumen: RequestBody,
        notes: RequestBody,
        result: RequestBody,
        fotoKtp: MultipartBody.Part,
        fotoSelfie: MultipartBody.Part
    ) {
        NetworkConfig().getVerifaceService().addData(namaKonsumen, notes, result, fotoKtp, fotoSelfie)
            .enqueue(object : Callback<ResponseVeriface> {
                override fun onResponse(call: Call<ResponseVeriface>, response: Response<ResponseVeriface>) {
                    if (response.isSuccessful) {
                        _operationStatus.postValue("Data added successfully")
                        getVerifaceData() // Refresh data
                    } else {
                        _operationStatus.postValue("Failed to add data")
                    }
                }

                override fun onFailure(call: Call<ResponseVeriface>, t: Throwable) {
                    _operationStatus.postValue("Error: ${t.message}")
                }
            })
    }

    // Update an existing data entry
    fun updateVerifaceData(
        id: Int,
        namaKonsumen: RequestBody,
        notes: RequestBody,
        result: RequestBody,
        fotoKtp: MultipartBody.Part?,
        fotoSelfie: MultipartBody.Part?
    ) {
        NetworkConfig().getVerifaceService().updateData(id, namaKonsumen, notes, result, fotoKtp, fotoSelfie)
            .enqueue(object : Callback<ResponseVeriface> {
                override fun onResponse(call: Call<ResponseVeriface>, response: Response<ResponseVeriface>) {
                    if (response.isSuccessful) {
                        _operationStatus.postValue("Data updated successfully")
                        getVerifaceData() // Refresh data
                    } else {
                        _operationStatus.postValue("Failed to update data")
                    }
                }

                override fun onFailure(call: Call<ResponseVeriface>, t: Throwable) {
                    _operationStatus.postValue("Error: ${t.message}")
                }
            })
    }

    // Delete a data entry by ID
    fun deleteVerifaceData(id: Int) {
        NetworkConfig().getVerifaceService().deleteData(id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _operationStatus.postValue("Data deleted successfully")
                        getVerifaceData() // Refresh data
                    } else {
                        _operationStatus.postValue("Failed to delete data")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _operationStatus.postValue("Error: ${t.message}")
                }
            })
    }
}
