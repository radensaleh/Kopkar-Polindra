package com.proyek3.kopkar_polindra.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var context : Context? = null
    private var appDB : AppDataBase? = null
    private var member : MemberEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        appDB = AppDataBase.getInstance(this)
        member = appDB?.memberDao()?.getMember()

        if(member != null){
            //loading ke dashboard
            Handler().postDelayed({
                intent = Intent(context, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }, 3500)
        }else{
            //loading ke login
            Handler().postDelayed({
                intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 3500)
        }

        //animation
        val uptoDown  = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        cvMain.startAnimation(uptoDown)

    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
