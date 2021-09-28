package com.proyek3.kopkar_polindra.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
//import org.jetbrains.annotations.Nullable

@Entity(tableName = "member")
data class MemberEntity (
    @NotNull @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int,
    @NotNull @ColumnInfo(name = "no_anggota") var no_anggota : String
//    @NotNull @ColumnInfo(name = "nama_lengkap") var nama_lengkap : String,
//    @NotNull @ColumnInfo(name = "nama_inisial") var nama_inisial : String,
//    @NotNull @ColumnInfo(name = "tempat_lahir") var tempat_lahir : String,
//    @NotNull @ColumnInfo(name = "tgl_lahir") var tgl_lahir : String,
//    @NotNull @ColumnInfo(name = "tgl_gabung") var tgl_gabung : String,
//    @NotNull @ColumnInfo(name = "unit_kerja") var unit_kerja : String,
//    @NotNull @ColumnInfo(name = "alamat") var alamat : String,
//    @NotNull @ColumnInfo(name = "no_hp") var no_hp : String,
//    @NotNull @ColumnInfo(name = "email") var email : String,
//    @NotNull @ColumnInfo(name = "password") var password : String
)
