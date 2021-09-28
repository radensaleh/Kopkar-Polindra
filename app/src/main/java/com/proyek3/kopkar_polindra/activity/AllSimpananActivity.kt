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
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.adapter.AdapterSimpanan
import com.proyek3.kopkar_polindra.model.Simpanan
import com.proyek3.kopkar_polindra.network.NetworkConfig
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_all_simpanan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllSimpananActivity : AppCompatActivity() {

    private var context : Context? = null
    private var appDB : AppDataBase? = null
    private var member : MemberEntity? = null
    private lateinit var loading : Dialog
    private var selected : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_simpanan)

        context = this
        loading = Dialog(this)
        appDB = context?.let { AppDataBase.getInstance(it) }
        member = appDB?.memberDao()?.getMember()

        val bundle = intent.extras

        loading()
        animationUtils()
        Handler().postDelayed({
            when (bundle?.getInt("simpanan")) {
                1 -> simpananPokok(member)
                2 -> simpananSukarela(member)
                3 -> simpananWajib(member)
//                4 -> simpananSHU(member)
            }
        }, 1500)

        cvKembali.setOnClickListener {
            intent = Intent(context, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        cvProfile.setOnClickListener {
            intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", 2)
            intent.putExtra("simpanan", bundle?.getInt("simpanan"))
            startActivity(intent)
            finish()
        }

        imgKosong.isVisible = false
        tvKosong.isVisible = false

    }

    private fun animationUtils(){
        val uptoDown  = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        val leftRight = AnimationUtils.loadAnimation(context, R.anim.lefttoright)
        val rightLeft = AnimationUtils.loadAnimation(context, R.anim.righttoleft)
        val downtoUp  = AnimationUtils.loadAnimation(context, R.anim.downtoup)

        cvKembali.startAnimation(leftRight)
        cvProfile.startAnimation(uptoDown)
        tvJudulSimpanan.startAnimation(leftRight)
        imgSimpanan.startAnimation(leftRight)
        tvJudulTotal.startAnimation(rightLeft)
        cvRp.startAnimation(rightLeft)
        tvTotalSimpanan.startAnimation(rightLeft)

        //svSearch.startAnimation(downtoUp)
        rvSimpanan.startAnimation(downtoUp)
    }

    @SuppressLint("SetTextI18n")
    fun simpananPokok(member : MemberEntity?){
        tvJudulSimpanan.text = "Simpanan Pokok"
        cvRp.setCardBackgroundColor(Color.parseColor("#ef5350"))

        getSimpanan(member, 1)
    }

    @SuppressLint("SetTextI18n")
    fun simpananSukarela(member: MemberEntity?){
        tvJudulSimpanan.text = "Simpanan Sukarela"
        cvRp.setCardBackgroundColor(Color.parseColor("#83aae5"))

        getSimpanan(member, 2)
    }

    @SuppressLint("SetTextI18n")
    fun simpananWajib(member: MemberEntity?){
        tvJudulSimpanan.text = "Simpanan Wajib"
        cvRp.setCardBackgroundColor(Color.parseColor("#f9a825"))

        getSimpanan(member, 3)
    }

//    @SuppressLint("SetTextI18n")
//    fun simpananSHU(member: MemberEntity?){
//        tvJudulSimpanan.text = "Simpanan SHU"
//        cvRp.setCardBackgroundColor(Color.parseColor("#a997c4"))
//
//        getSimpanan(member, 4)
//    }

    private fun getSimpanan(member: MemberEntity?, id : Int){
        NetworkConfig().api().dataSimpanan(member?.no_anggota!!, id).enqueue(object : Callback<Simpanan>{
            override fun onFailure(call: Call<Simpanan>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<Simpanan>, response: Response<Simpanan>) {
                loading.dismiss()
                if(response.isSuccessful){
                    val data = response.body()

                    if(data?.data!!.isEmpty()){
                        imgKosong.isVisible = true
                        tvKosong.isVisible = true
                    }else{
                        imgKosong.isVisible = false
                        tvKosong.isVisible = false
                    }

                    tvTotalSimpanan.text = data.total_simpanan
                    rvSimpanan.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                    val adapter = AdapterSimpanan(data.data, id, data.jenis_simpanan!!)
                    rvSimpanan.adapter = adapter
                    adapter.notifyDataSetChanged()

//                    svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
//                        override fun onQueryTextSubmit(p0: String?): Boolean {
//                            p0?.let { adapter?.filter(it) }
//                            return false
//                        }
//
//                        override fun onQueryTextChange(p0: String?): Boolean {
//                            p0?.let { adapter?.filter(it) }
//                            return false
//                        }
//
//                    })

                    //spinner
                    //val a = arrayOf("Semua Data", "Tahun 2019")
                    val listTahun = data.tahun?.toTypedArray()
                    val tahun = ArrayAdapter(context?.applicationContext!!, R.layout.support_simple_spinner_dropdown_item, listTahun!!)
                    tahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spTahun.setAdapter(tahun)

                    selected = listTahun[0]
                    spTahun.setOnItemSelectedListener { _, position, _, _ ->
                        selected = listTahun[position]
                        getSimpananPerYear(member.no_anggota, id, selected!!)
                    }

                }else{
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun getSimpananPerYear(no_anggota : String, id : Int, tahun : String){
        loading()
        NetworkConfig().api().dataSimpananPerYear(no_anggota, id, tahun).enqueue(object : Callback<Simpanan>{
            override fun onFailure(call: Call<Simpanan>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<Simpanan>, response: Response<Simpanan>) {
                loading.dismiss()
                if(response.isSuccessful){
                    val data = response.body()

                    if(data?.data!!.isEmpty()){
                        imgKosong.isVisible = true
                        tvKosong.isVisible = true
                    }else{
                        imgKosong.isVisible = false
                        tvKosong.isVisible = false
                    }

                    tvTotalSimpanan.text = data.total_simpanan
                    rvSimpanan.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                    val adapter = AdapterSimpanan(data.data, id, data.jenis_simpanan!!)
                    rvSimpanan.adapter = adapter
                    adapter.notifyDataSetChanged()

//                    svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
//                        override fun onQueryTextSubmit(p0: String?): Boolean {
//                            p0?.let { adapter?.filter(it) }
//                            return false
//                        }
//
//                        override fun onQueryTextChange(p0: String?): Boolean {
//                            p0?.let { adapter?.filter(it) }
//                            return false
//                        }
//
//                    })

                    //spinner
                    //val a = arrayOf("Semua Data", "Tahun 2019")
                    val listTahun = data.tahun?.toTypedArray()
                    val tahunAdapter = ArrayAdapter(context?.applicationContext!!, R.layout.support_simple_spinner_dropdown_item, listTahun!!)
                    tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spTahun.setAdapter(tahunAdapter)

                    selected = data.pertahun
                    for(i in listTahun.indices){
                        if(listTahun[i] == selected){
                            spTahun.selectedIndex = i
                        }
                    }

                    spTahun.setOnItemSelectedListener { _, position, _, _ ->
                        selected = listTahun[position]
                        if(selected == "Semua Data"){
                            getSimpanan(member, id)
                        }else{
                            getSimpananPerYear(member?.no_anggota!!, id, selected!!)
                        }
                    }

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
