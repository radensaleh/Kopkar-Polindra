package com.proyek3.kopkar_polindra.network

import com.proyek3.kopkar_polindra.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email : String,
        @Field("password") password : String
    ) : Call<Login>

    @FormUrlEncoded
    @POST("data-member")
    fun dataMember(
        @Field("no_anggota") noAnggota : String
    ) : Call<Login>

    @FormUrlEncoded
    @POST("lupa-password")
    fun lupaPassword(
        @Field("email") email : String
    ) : Call<Respon>

//    @FormUrlEncoded
//    @POST("data-transaksi")
//    fun dataTransaksi(
//        @Field("no_anggota") noAnggota: String
//    ) : Call<Transaksi>

    @FormUrlEncoded
    @POST("data-simpanan")
    fun dataSimpanan(
        @Field("no_anggota") noAnggota: String,
        @Field("id") id : Int
    ) : Call<Simpanan>

    @FormUrlEncoded
    @POST("data-simpanan-peryear")
    fun dataSimpananPerYear(
        @Field("no_anggota") noAnggota: String,
        @Field("id") id : Int,
        @Field("tahun") tahun : String
    ) : Call<Simpanan>

    @Multipart
    @POST("edit-photo")
    fun editPhoto(
        @Part("no_anggota") no_anggota: RequestBody,
        @Part file : MultipartBody.Part
    ): Call<Respon>

    @FormUrlEncoded
    @POST("edit-password")
    fun editPassword(
        @Field("no_anggota") no_anggota : String,
        @Field("password_lama") password_lama : String,
        @Field("password_baru") password_baru : String
    ) : Call<Respon>

    @FormUrlEncoded
    @POST("edit-akun")
    fun editAkun(
        @Field("no_anggota") no_anggota : String,
        @Field("nama_lengkap") nama_lengkap : String,
        @Field("nama_inisial") nama_inisial : String,
        @Field("no_hp") no_hp : String,
        @Field("tgl_lahir") tgl_lahir : String,
        @Field("email") email : String,
        @Field("alamat") alamat : String
    ) : Call<Respon>

    @FormUrlEncoded
    @POST("pinjaman")
    fun pinjaman(
        @Field("no_anggota") no_anggota : String,
        @Field("ket") ket : String,
        @Field("jumlah") jumlah : Int,
        @Field("status") status : Int
    ) : Call<Respon>

    @FormUrlEncoded
    @POST("data-pinjaman")
    fun dataPinjaman(
        @Field("no_anggota") noAnggota: String
    ) : Call<Pinjaman>

}