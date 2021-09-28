package com.proyek3.kopkar_polindra.model

data class Transaksi(
    val status_code : Int? = null,
    val message : String? = null,
    val data : List<DataTransaksi>? = null
)