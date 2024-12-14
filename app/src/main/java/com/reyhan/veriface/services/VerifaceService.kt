package com.reyhan.veriface.services

import com.reyhan.veriface.model.ResponseVeriface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

import retrofit2.http.*

interface VerifaceServices {

    // Add a new data entry
    @Multipart
    @POST("data")
    fun addData(
        @Part("namaKonsumen") namaKonsumen: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part("result") result: RequestBody,
        @Part fotoKtp: MultipartBody.Part,
        @Part fotoSelfie: MultipartBody.Part
    ): Call<ResponseVeriface>

    // Update an existing data entry
    @Multipart
    @PUT("data/{id}")
    fun updateData(
        @Path("id") id: Int,
        @Part("namaKonsumen") namaKonsumen: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part("result") result: RequestBody,
        @Part fotoKtp: MultipartBody.Part?,
        @Part fotoSelfie: MultipartBody.Part?
    ): Call<ResponseVeriface>

    // Delete a data entry by ID
    @DELETE("data/{id}")
    fun deleteData(@Path("id") id: Int): Call<Void>

    // Get all data entries
    @GET("data")
    fun getAllData(): Call<List<ResponseVeriface>>
}
