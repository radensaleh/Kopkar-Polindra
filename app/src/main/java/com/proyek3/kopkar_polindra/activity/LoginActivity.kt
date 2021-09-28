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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.model.Login
import com.proyek3.kopkar_polindra.model.Member
import com.proyek3.kopkar_polindra.network.NetworkConfig
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var context : Context? = null
    private lateinit var alertDialog : Dialog
    private lateinit var loading : Dialog
    private var db : AppDataBase? = null

    private var validEmail : Boolean = false
    private var validPassword : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this
        alertDialog = Dialog(this)
        loading = Dialog(this)
        db = AppDataBase.getInstance(this)

        //animation
        val uptoDown  = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        cvLogin.startAnimation(uptoDown)

        validationLogin()

        tvLupaPassword.setOnClickListener {
            val intent = Intent(context, LupaPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validationLogin(){
        validationEmail()
        validationPassword()

        fbLogin.setOnClickListener {
            when {
                etEmail.text!!.isEmpty() -> etEmail.error = "Email Masih Kosong"
                etPassword.text!!.isEmpty() -> etPassword.error = "Password Masih Kosong"
                !validEmail -> validationEmail()
                !validPassword -> validationPassword()
                else -> login()
            }
        }
    }

    private fun validationEmail(){
        etEmail.doAfterTextChanged {
            when {
                android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches() -> validEmail = true
                else -> {
                    etEmail.error = "Email Tidak Sesuai"
                    validEmail = false
                }
            }
        }
    }

    private fun validationPassword(){
        etPassword.doAfterTextChanged {
            if(etPassword.text.toString().length < 6){
                etPassword.error = "Password Minimal 6 Karakter"
                validPassword = false
            }else{ validPassword = true }
        }
    }

    private fun login(){
        val email : String = etEmail.text.toString()
        val password : String = etPassword.text.toString()

        loading()
        Handler().postDelayed({
//            alertDialog(0, "Berhasil")
            loading.dismiss()

            NetworkConfig().api().login(email, password).enqueue(object : Callback<Login> {
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    loading.dismiss()
                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    loading.dismiss()
                    if(response.isSuccessful){
                        val data = response.body()
                        alertDialog(data?.status_code, data?.message, data?.data!!)
                    }else{
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }, 2000)
    }

    @SuppressLint("SetTextI18n")
    private fun alertDialog(status_code: Int?, message: String?, list: List<Member>){

        when (status_code) {
            0 -> alertDialog.setContentView(R.layout.alert_success)
            else -> alertDialog.setContentView(R.layout.alert_danger)
        }

        val btnYa : Button = alertDialog.findViewById(R.id.btnYa)
        val tvIsi : TextView = alertDialog.findViewById(R.id.tvIsi)
        val tvJudul : TextView = alertDialog.findViewById(R.id.tvJudul)

        tvJudul.text = "Peringatan!"
        tvIsi.text = message

        btnYa.setOnClickListener {
            alertDialog.dismiss()
            when (status_code){
                0 -> {
                    storeSQL(list)
                    val intent = Intent(context, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun storeSQL(list : List<Member>){
        val noAnggota : String = list[0].no_anggota!!
//        val namaLengkap : String = list[0].nama_lengkap!!
//        val namaInisial : String = list[0].nama_inisial!!
//        val tempatLahir : String = list[0].tempat_lahir!!
//        val tglLahir : String = list[0].tgl_lahir!!
//        val tglGabung : String = list[0].tgl_gabung!!
//        val unitKerja : String = list[0].unit_kerja!!
//        val alamat : String = list[0].alamat!!
//        val noHp : String = list[0].no_hp!!
//        val email : String = list[0].email!!
//        val password : String = etPassword.text.toString()

        //val member = MemberEntity(1, noAnggota, namaLengkap, namaInisial, tempatLahir, tglLahir, tglGabung, unitKerja ,alamat, noHp, email, password)
        val member = MemberEntity(1, noAnggota)
        db?.memberDao()?.insert(member)

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
