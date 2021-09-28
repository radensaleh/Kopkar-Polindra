package com.proyek3.kopkar_polindra.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemberDAO {
    @Query("SELECT * from member")
    fun getAll() : List<MemberEntity>

    @Insert
    fun insert(member: MemberEntity)

    @Delete
    fun delete(member: MemberEntity)

    @Query("SELECT * FROM member WHERE id=1")
    fun getMember() : MemberEntity

}