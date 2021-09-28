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
import com.proyek3.kopkar_polindra.model.Respon
import com.proyek3.kopkar_polindra.network.NetworkConfig
import kotlinx.android.synthetic.main.activity_lupa_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPasswordActivity : AppCompatActivity() {

    private var context : Context? = null
    private lateinit var alertDialog : Dialog
    private lateinit var loading : Dialog
    private var validEmail : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        context = this
        alertDialog = Dialog(this)
        loading = Dialog(this)

        //animation
        val uptoDown  = AnimationUtils.loadAnimation(context, R.anim.uptodown)
        cvLupaPassword.startAnimation(uptoDown)

        tvLogin.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        validation()
    }

    private fun validation(){
        validationEmail()

        fbLupaPswd.setOnClickListener {
            when {
                etEmail.text!!.isEmpty() -> etEmail.error = "Email Masih Kosong"
                !validEmail -> validationEmail()
                else -> lupaPswd()
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

    private fun lupaPswd(){
        val email : String = etEmail.text.toString()

        loading()
        Handler().postDelayed({
//            alertDialog(0, "Berhasil")

            NetworkConfig().api().lupaPassword(email).enqueue(object : Callback<Respon>{
                override fun onFailure(call: Call<Respon>, t: Throwable) {
                    loading.dismiss()
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Respon>, response: Response<Respon>) {
                    loading.dismiss()
                    if(response.isSuccessful){
                        val data = response.body()
                        alertDialog(data?.status_code, data?.message)
                    }else{
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }, 2000)
    }

    @SuppressLint("SetTextI18n")
    private fun alertDialog(status_code: Int?, message: String?) {

        when (status_code) {
            0 -> alertDialog.setContentView(R.layout.alert_success)
            else -> alertDialog.setContentView(R.layout.alert_danger)
        }

        val btnYa: Button = alertDialog.findViewById(R.id.btnYa)
        val tvIsi: TextView = alertDialog.findViewById(R.id.tvIsi)
        val tvJudul: TextView = alertDialog.findViewById(R.id.tvJudul)

        tvJudul.text = "Peringatan!"
        tvIsi.text = message

        btnYa.setOnClickListener {
            alertDialog.dismiss()
            when (status_code) {
                0 -> {
                    val intent = Intent(context, LoginActivity::class.java)
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
