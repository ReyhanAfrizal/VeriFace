package com.reyhan.veriface.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ResponseVeriface(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("createdDataAt")
	val createdDataAt: String? = null,

	@field:SerializedName("fotoSelfie")
	val fotoSelfie: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("modifiedDataAt")
	val modifiedDataAt: String? = null,

	@field:SerializedName("fotoKtp")
	val fotoKtp: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("modifiedDataBy")
	val modifiedDataBy: String? = null,

	@field:SerializedName("namaKonsumen")
	val namaKonsumen: String? = null,

	@field:SerializedName("createdDataBy")
	val createdDataBy: String? = null,

	@field:SerializedName("user")
	val user: User? = null
) : Parcelable

@Parcelize
data class User(

	@field:SerializedName("idUser")
	val idUser: Int? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("nip")
	val nip: String? = null,

	@field:SerializedName("activeUser")
	val activeUser: Boolean? = null,

	@field:SerializedName("createdUserAt")
	val createdUserAt: String? = null,

	@field:SerializedName("modifiedUserAt")
	val modifiedUserAt: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("namaUser")
	val namaUser: String? = null
) : Parcelable
