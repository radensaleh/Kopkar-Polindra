package com.proyek3.kopkar_polindra.model

data class Member(
    var no_anggota : String? = null,
    var nama_lengkap : String? = null,
    var nama_inisial : String? = null,
    var tgl_lahir : String? = null,
    var tgl_gabung : String? = null,
    var nama_unit_kerja : String? = null,
    var alamat : String? = null,
    var no_hp : String? = null,
    var email : String? = null,
    var role : Int? = null
)