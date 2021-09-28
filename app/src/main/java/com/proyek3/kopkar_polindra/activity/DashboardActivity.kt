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
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.adapter.AdapterSimpanan
import com.proyek3.kopkar_polindra.model.Login
import com.proyek3.kopkar_polindra.model.Simpanan
import com.proyek3.kopkar_polindra.network.NetworkConfig
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private var context : Context? = null
    private var appDB : AppDataBase? = null
    private var member : MemberEntity? = null
    private lateinit var loading : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        context = this
        loading = Dialog(this)
        appDB = AppDataBase.getInstance(this)
        member = appDB?.memberDao()?.getMember()

        loading()
        animationUtils()

        Handler().postDelayed({
            setData(member)
        }, 1500)

        cvProfile.setOnClickListener {
            intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", 1)
            startActivity(intent)
            finish()
        }

        cvPokok.setOnClickListener {
            intent = Intent(context, AllSimpananActivity::class.java)
            intent.putExtra("simpanan", 1)
            startActivity(intent)
            finish()
        }

        cvSukarela.setOnClickListener {
            intent = Intent(context, AllSimpananActivity::class.java)
            intent.putExtra("simpanan", 2)
            startActivity(intent)
            finish()
        }

        cvWajib.setOnClickListener {
            intent = Intent(context, AllSimpananActivity::class.java)
            intent.putExtra("simpanan", 3)
            startActivity(intent)
            finish()
        }

        cvPinjaman.setOnClickListener {
            intent = Intent(context, PinjamanActivity::class.java)
            startActivity(intent)
            finish()
        }

//        cvSHU.setOnClickListener {
//            intent = Intent(context, AllSimpananActivity::class.java)
//            intent.putExtra("simpanan", 4)
//            startActivity(intent)
//            finish()
//        }

    }

    private fun animationUtils(){
        val uptoDown   = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        val downtoUp   = AnimationUtils.loadAnimation(context, R.anim.downtoup)
        val leftRight  = AnimationUtils.loadAnimation(context, R.anim.lefttoright)
        val rightLeft  = AnimationUtils.loadAnimation(context, R.anim.righttoleft)

        cvHi.startAnimation(leftRight)
        tvSelamatDatang.startAnimation(rightLeft)
        tvInisial.startAnimation(rightLeft)
        cvProfile.startAnimation(uptoDown)

        tvSimpanan.startAnimation(leftRight)

        cvPokok.startAnimation(leftRight)
        tvPokok.startAnimation(leftRight)
        cvSukarela.startAnimation(leftRight)
        tvSukarela.startAnimation(leftRight)
        cvWajib.startAnimation(rightLeft)
        tvWajib.startAnimation(rightLeft)
        cvPinjaman.startAnimation(rightLeft)
        tvPinjaman.startAnimation(rightLeft)

        tvSHU.startAnimation(leftRight)
        rvSHU.startAnimation(downtoUp)

    }

    private fun setData(member : MemberEntity?){
        NetworkConfig().api().dataMember(member?.no_anggota!!).enqueue(object : Callback<Login>{
            override fun onFailure(call: Call<Login>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if(response.isSuccessful){
                    tvInisial.text = response.body()?.data?.get(0)?.nama_inisial
                    getDataSHU(member)
                }else{
                    loading.dismiss()
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })


    }

    private fun getDataSHU(member : MemberEntity?){
        NetworkConfig().api().dataSimpanan(member?.no_anggota!!, 4).enqueue(object : Callback<Simpanan>{
            override fun onFailure(call: Call<Simpanan>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<Simpanan>, response: Response<Simpanan>) {
                loading.dismiss()

                if(response.isSuccessful){
                    val data = response.body()
                    rvSHU.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                    val adapter = data?.data?.let { AdapterSimpanan(it, 4, data.jenis_simpanan!!) }
                    rvSHU.adapter = adapter
                    adapter?.notifyDataSetChanged()
                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
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
