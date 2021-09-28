package com.proyek3.kopkar_polindra.model

data class Simpanan(
    val status_code : Int? = null,
    val message : String? = null,
    val data : List<DataSimpanan>? = null,
    val total_simpanan : String? = null,
    val tahun : List<String>? = null,
    val pertahun : String? = null,
    val jenis_simpanan : String? = null
)