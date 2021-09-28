package com.proyek3.kopkar_polindra.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.adapter.AdapterPinjaman
import com.proyek3.kopkar_polindra.model.Pinjaman
import com.proyek3.kopkar_polindra.network.NetworkConfig
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_pinjaman.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinjamanActivity : AppCompatActivity() {

    private var context : Context? = null
    private lateinit var loading : Dialog
    private var appDB : AppDataBase? = null
    private var member : MemberEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjaman)

        context = this
        loading = Dialog(this)
        appDB = context?.let { AppDataBase.getInstance(it) }
        member = appDB?.memberDao()?.getMember()

//        val bundle = intent.extras

        loading()
        animationUtils()
        Handler().postDelayed({
            getPinjaman(member)
        }, 1500)

        cvKembali.setOnClickListener {
            intent = Intent(context, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        cvProfile.setOnClickListener {
            intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", 3)
//            intent.putExtra("pinjaman", bundle?.getInt("pinjaman"))
            startActivity(intent)
            finish()
        }

        imgKosong.isVisible = false
        tvKosong.isVisible = false

    }

    private fun getPinjaman(member: MemberEntity?) {
        NetworkConfig().api().dataPinjaman(member?.no_anggota!!).enqueue(object : Callback<Pinjaman>{
            override fun onFailure(call: Call<Pinjaman>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<Pinjaman>, response: Response<Pinjaman>) {
                loading.dismiss()
                if(response.isSuccessful) {
                    val data = response.body()

                    if (data?.data!!.isEmpty()) {
                        imgKosong.isVisible = true
                        tvKosong.isVisible = true
                    } else {
                        imgKosong.isVisible = false
                        tvKosong.isVisible = false
                    }

                    rvPinjaman.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                    val adapter = AdapterPinjaman(data.data)
                    rvPinjaman.adapter = adapter
                    adapter.notifyDataSetChanged()

                }

            }

        })
    }

    private fun animationUtils() {
        val uptoDown = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        val leftRight = AnimationUtils.loadAnimation(context, R.anim.lefttoright)
        val downtoUp = AnimationUtils.loadAnimation(context, R.anim.downtoup)

        cvKembali.startAnimation(leftRight)
        cvProfile.startAnimation(uptoDown)
        tvPinjaman.startAnimation(leftRight)
        rvPinjaman.startAnimation(downtoUp)

    }

    private fun loading(){
        loading.setContentView(R.layout.loading)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.window!!.attributes.windowAnimations = R.style.DialogAnimation
        loading.setCancelable(false)
        loading.show()
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
