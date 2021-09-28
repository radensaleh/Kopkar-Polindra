package com.proyek3.kopkar_polindra.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MemberEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun memberDao() : MemberDAO
    companion object {
        private  var INSTANCE : AppDataBase? = null

        fun getInstance(context : Context) : AppDataBase? {
            if(INSTANCE == null){
                synchronized(AppDataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, "dbMember").allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
