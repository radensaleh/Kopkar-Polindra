package com.proyek3.kopkar_polindra.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.proyek3.kopkar_polindra.BuildConfig
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.model.Login
import com.proyek3.kopkar_polindra.model.Member
import com.proyek3.kopkar_polindra.model.Respon
import com.proyek3.kopkar_polindra.network.NetworkConfig
import com.proyek3.kopkar_polindra.room.AppDataBase
import com.proyek3.kopkar_polindra.room.MemberEntity
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private var context : Context? = null
    private lateinit var alertDialog : Dialog
    private lateinit var pinjamDialog : Dialog
    private lateinit var editDialog : Dialog
    private lateinit var editDialogAkun : Dialog
    private lateinit var editDialogPswd : Dialog
    private lateinit var editDialogPhotos : Dialog
    private var appDB : AppDataBase? = null
    private var member : MemberEntity? = null
    private lateinit var loading : Dialog
    private var tglLahir : String? = null
    private val SELECT_PICTURE = 2
    private var tap = 0
    private var bundleID : Int? = null
    private var bundleSimpanan : Int? = null
//    private var bundlePinjaman : Int? = null

    private var validationNoHP = false
    private var validationEmail = false
    private var validPassword : Boolean = false
    private var validPasswordBaru : Boolean = false
    private var validVerifPassword : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        context = this
        appDB = context?.let { AppDataBase.getInstance(it) }
        member = appDB?.memberDao()?.getMember()
        alertDialog = context?.let { Dialog(it) }!!
        loading = Dialog(this)
        pinjamDialog = Dialog(this)
        editDialog = Dialog(this)
        editDialogAkun = Dialog(this)
        editDialogPswd = Dialog(this)
        editDialogPhotos = Dialog(this)

        setData(member)

        val bundle = intent.extras
        bundleID = bundle?.getInt("id")
        bundleSimpanan = bundle?.getInt("simpanan")
//        bundlePinjaman = bundle?.getInt("pinjaman")

        when (bundleID) {
            1 -> {
                cvKembali.setOnClickListener {
                    intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            2 -> {
                cvKembali.setOnClickListener {
                    intent = Intent(this, AllSimpananActivity::class.java)
                    intent.putExtra("simpanan", bundleSimpanan)
                    startActivity(intent)
                    finish()
                }
            }
            3 -> {
                cvKembali.setOnClickListener {
                    intent = Intent(this, PinjamanActivity::class.java)
//                    intent.putExtra("pinjaman", bundlePinjaman)
                    startActivity(intent)
                    finish()
                }
            }
        }

        cvHome.setOnClickListener {
            intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        cvLogout.setOnClickListener {
            alert()
        }

        cvPinjaman.setOnClickListener {
            dialogPinjam()
        }

    }

    private fun dialogPinjam(){
        pinjamDialog.setContentView(R.layout.pinjaman_dialog)
        val judul = pinjamDialog.findViewById<TextView>(R.id.tvJudul)
        val btnClose = pinjamDialog.findViewById<Button>(R.id.btnClose)
        val btnPinjam = pinjamDialog.findViewById<Button>(R.id.btnPinjam)
        val etKet = pinjamDialog.findViewById<EditText>(R.id.etKeterangan)
        val etNominal = pinjamDialog.findViewById<EditText>(R.id.etNominal)

        judul.text = "PENGAJUAN PINJAMAN"
        btnClose.setOnClickListener { pinjamDialog.dismiss() }
        btnPinjam.setOnClickListener {
            when{
                etKet.text.isEmpty() -> etKet.error = "Keterangan Pengajuan Kosong"
                etNominal.text.isEmpty() -> etNominal.error = "Jumlah Nominal Kosong"
                else -> {
                    val ket = etKet.text.toString()
                    val nominal = etNominal.text.toString()

                    loading()
                    Handler().postDelayed({
                        NetworkConfig().api().pinjaman(member?.no_anggota!!, ket, nominal.toInt(), 0).enqueue(object : Callback<Respon>{
                            override fun onFailure(call: Call<Respon>, t: Throwable) {
                                loading.dismiss()
                                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<Respon>, response: Response<Respon>) {
                                loading.dismiss()
                                if(response.isSuccessful){
                                    val data = response.body()
                                    alertDialog(data?.status_code, data?.message, pinjamDialog)
                                }else{
                                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                    }, 2000)
                }
            }
        }

        pinjamDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pinjamDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        pinjamDialog.setCancelable(false)
        pinjamDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setData(member : MemberEntity?){
        loading()
        NetworkConfig().api().dataMember(member?.no_anggota!!).enqueue(object : Callback<Login>{
            override fun onFailure(call: Call<Login>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                loading.dismiss()
                if(response.isSuccessful){
                    val data = response.body()?.data
                    tvNoAnggota.text = data?.get(0)?.no_anggota
                    tvEmail.text = data?.get(0)?.email
                    tvTTL.text = "Tanggal Lahir ${data?.get(0)?.tgl_lahir}"
                    tvUnitKerja.text = "Unit Kerja ${data?.get(0)?.nama_unit_kerja}"
                    tvNoHP.text = data?.get(0)?.no_hp
                    tvTglGabung.text = "Bergabung pada ${data?.get(0)?.tgl_gabung}"
                    tvAlamat.text = data?.get(0)?.alamat
                    tvNamaLengkap.text = data?.get(0)?.nama_lengkap

                    context.let {
                        Glide.with(it!!)
                            .load(BuildConfig.IMAGE + data?.get(0)?.no_anggota + ".jpg")
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .error(R.drawable.noimage)
//                            .listener(object : RequestListener<Drawable> {
//                                override fun onLoadFailed(
//                                    e: GlideException?,
//                                    model: Any?,
//                                    target: Target<Drawable>?,
//                                    isFirstResource: Boolean
//                                ): Boolean {
//                                    loading.dismiss()
//                                    //Toast.makeText(context, "failed load image", Toast.LENGTH_LONG).show()
//                                    return false
//                                }
//
//                                override fun onResourceReady(
//                                    resource: Drawable?,
//                                    model: Any?,
//                                    target: Target<Drawable>?,
//                                    dataSource: DataSource?,
//                                    isFirstResource: Boolean
//                                ): Boolean {
//                                    loading.dismiss()
//                                    //Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
//                                    return false
//                                }
//
//                            })
                            .into(imgFoto)
                    }

                    cvEdit.setOnClickListener {
                        editDialog.setContentView(R.layout.edit_dialog)
                        val judul = editDialog.findViewById<TextView>(R.id.tvJudul)
                        val btnClose = editDialog.findViewById<Button>(R.id.btnClose)
                        val LLAkun = editDialog.findViewById<LinearLayout>(R.id.LLProfile)
                        val LLPassword = editDialog.findViewById<LinearLayout>(R.id.LLPassword)
                        val LLPhotos = editDialog.findViewById<LinearLayout>(R.id.LLPhotos)

                        LLAkun.setOnClickListener {
                            akun(data)
                            editDialog.dismiss()
                        }
                        LLPassword.setOnClickListener {
                            password()
                            editDialog.dismiss()
                        }
                        LLPhotos.setOnClickListener {
                            photos()
                            editDialog.dismiss()
                        }

                        judul.text = "EDIT DATA"
                        btnClose.setOnClickListener { editDialog.dismiss() }

                        editDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        editDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                        editDialog.setCancelable(false)
                        editDialog.show()
                    }

                }else{
                    loading.dismiss()
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun akun(data: List<Member>?) {
        editDialogAkun.setContentView(R.layout.edit_akun)
        val judul = editDialogAkun.findViewById<TextView>(R.id.tvJudul)
        val btnClose = editDialogAkun.findViewById<Button>(R.id.btnClose)
        val btnUpdate = editDialogAkun.findViewById<Button>(R.id.btnUpdate)
        val nama = editDialogAkun.findViewById<EditText>(R.id.etNamaLengkap)
        val inisial = editDialogAkun.findViewById<EditText>(R.id.etNamaInisial)
        val noHp = editDialogAkun.findViewById<EditText>(R.id.etNoHP)
        val email = editDialogAkun.findViewById<EditText>(R.id.etEmail)
        val alamat = editDialogAkun.findViewById<EditText>(R.id.etAlamat)
        val tgl_lahir = editDialogAkun.findViewById<TextView>(R.id.tvTglLahir)
        val btn_tglLahir = editDialogAkun.findViewById<Button>(R.id.btnTglLahir)

        nama.setText(data?.get(0)?.nama_lengkap)
        inisial.setText(data?.get(0)?.nama_inisial)
        noHp.setText(data?.get(0)?.no_hp)
        email.setText(data?.get(0)?.email)
        alamat.setText(data?.get(0)?.alamat)
        tgl_lahir.text = data?.get(0)?.tgl_lahir
        tglLahir = data?.get(0)?.tgl_lahir

        btn_tglLahir.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, years, monthOfYear, dayOfMonth ->
                c.set(Calendar.YEAR, years)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tgl_lahir.text = sdf.format(c.time)
                tglLahir = sdf.format(c.time)
            }, year, month, day).show()
        }

        validationNoHp(noHp)
        validationEmail(email)

        validationNoHP = true
        validationEmail = true

        judul.text = "EDIT AKUN"
        btnClose.setOnClickListener { editDialogAkun.dismiss() }
        btnUpdate.setOnClickListener {
            when {
                nama.text!!.isEmpty() -> nama.error = "Nama Lengkap Masih Kosong"
                inisial.text!!.isEmpty() -> inisial.error = "Nama Inisial Masih Kosong"
                noHp.text!!.isEmpty() -> noHp.error = "Nomer HP Masih Kosong"
                !validationNoHP -> validationNoHp(noHp)
                tgl_lahir.text == "yyyy-mm-dd" -> Toast.makeText(context, "Tanggal Lahir Masih Kosong!", Toast.LENGTH_SHORT).show()
                email.text!!.isEmpty() -> email.error = "Email Masih Kosong"
                !validationEmail -> validationEmail(email)
                alamat.text!!.isEmpty() -> alamat.error = "Alamat Masih Kosong"
                else -> {
                    val nama : String = nama.text.toString()
                    val inisial : String = inisial.text.toString()
                    val noHp : String = noHp.text.toString()
                    val email : String = email.text.toString()
                    val alamat : String = alamat.text.toString()

                    loading()
                    Handler().postDelayed({
                        editAkun(member?.no_anggota, nama, inisial, noHp, tglLahir, email, alamat, editDialogAkun)
                    }, 2000)
                }
            }
        }

        editDialogAkun.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editDialogAkun.window!!.attributes.windowAnimations = R.style.DialogAnimation
        editDialogAkun.setCancelable(false)
        editDialogAkun.show()
    }

    private fun editAkun(noAnggota: String?, nama: String, inisial: String, noHp: String, tglLahir: String?, email: String, alamat: String, editDialogAkun: Dialog) {
        NetworkConfig().api().editAkun(noAnggota!!, nama, inisial, noHp, tglLahir!!, email, alamat).enqueue(object : Callback<Respon>{
            override fun onFailure(call: Call<Respon>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Respon>, response: Response<Respon>) {
                loading.dismiss()
                if(response.isSuccessful){
                    val body = response.body()
                    alertDialog(body?.status_code, body?.message, editDialogAkun)
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun validationNoHp(noHp : EditText){
        noHp.doAfterTextChanged {
            when {
                noHp.text.toString().length < 10 -> {
                    noHp.error = "Kurang dari 10"
                    validationNoHP = false
                }
                noHp.text.toString().length >= 15 -> {
                    noHp.error = "Lebih dari 15"
                    validationNoHP = false
                }
                else -> validationNoHP = true
            }
        }
    }

    private fun validationEmail(email : EditText){
        email.doAfterTextChanged {
            when {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() -> validationEmail = true
                else -> {
                    email.error = "Email Tidak Sesuai"
                    validationEmail = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun password() {
        editDialogPswd.setContentView(R.layout.edit_password)
        val judul = editDialogPswd.findViewById<TextView>(R.id.tvJudul)
        val btnClose = editDialogPswd.findViewById<Button>(R.id.btnClose)
        val btnUpdate = editDialogPswd.findViewById<Button>(R.id.btnUpdate)
        val pswdLama = editDialogPswd.findViewById<EditText>(R.id.etPassword)
        val pswdBaru = editDialogPswd.findViewById<EditText>(R.id.etPasswordBaru)
        val pswdVerif = editDialogPswd.findViewById<EditText>(R.id.etVerifPasswordBaru)

        validationPassword(pswdLama, pswdBaru)
        validationPasswordBaru(pswdBaru, pswdVerif)
        verificationPassword(pswdVerif, pswdBaru)

        judul.text = "EDIT PASSWORD"
        btnClose.setOnClickListener { editDialogPswd.dismiss() }
        btnUpdate.setOnClickListener {
            when {
                pswdLama.text!!.isEmpty() -> pswdLama.error = "Password Lama Masih Kosong"
                pswdBaru.text!!.isEmpty() -> pswdBaru.error = "Password Baru Masih Kosong"
                pswdVerif.text!!.isEmpty() -> pswdVerif.error = "Password Baru Masih Kosong"
                !validPassword -> validationPassword(pswdLama, pswdBaru)
                !validPasswordBaru -> validationPasswordBaru(pswdBaru, pswdVerif)
                !validVerifPassword -> verificationPassword(pswdVerif, pswdBaru)
                else -> {
                    val passwordLama = pswdLama.text.toString()
                    val passwordBaru = pswdBaru.text.toString()

                    loading()
                    Handler().postDelayed({
                        changePassword(passwordLama, passwordBaru, editDialogPswd)
                    }, 2000)
                }
            }
        }

        editDialogPswd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editDialogPswd.window!!.attributes.windowAnimations = R.style.DialogAnimation
        editDialogPswd.setCancelable(false)
        editDialogPswd.show()
    }

    private fun changePassword(passwordLama: String, passwordBaru: String, editDialogPswd: Dialog) {
        NetworkConfig().api().editPassword(member?.no_anggota!!, passwordLama, passwordBaru).enqueue(object : Callback<Respon>{
            override fun onFailure(call: Call<Respon>, t: Throwable) {
                loading.dismiss()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Respon>, response: Response<Respon>) {
                loading.dismiss()
                if(response.isSuccessful){
                    val body = response.body()
                    alertDialog(body?.status_code, body?.message, editDialogPswd)
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun validationPassword(pswdLama: EditText, pswdBaru: EditText) {
        pswdLama.doAfterTextChanged {
            if(pswdLama.text.toString().length < 6){
                pswdLama.error = "Password Minimal 6 Karakter"
                validPassword = false
            }else{ validPassword = true }
        }

        pswdBaru.doAfterTextChanged {
            if(pswdBaru.text.toString().length < 6){
                pswdBaru.error = "Password Baru Minimal 6 Karakter"
                validPasswordBaru = false
            }else{ validPasswordBaru = true }
        }
    }

    private fun validationPasswordBaru(pswdBaru : EditText, pswdVerif : EditText){
        pswdBaru.doAfterTextChanged {
            if(pswdVerif.text.toString() != pswdBaru.text.toString()){
                pswdBaru.error = "Password Baru Tidak Sama"
                validPasswordBaru = false
            }else{
                validPasswordBaru = true
                validVerifPassword = true
                pswdVerif.error = null
            }
        }
    }

    private fun verificationPassword(pswdVerif : EditText, pswdBaru : EditText){
        pswdVerif.doAfterTextChanged {
            if(pswdVerif.text.toString() != pswdBaru.text.toString()){
                pswdVerif.error = "Password Baru Tidak Sama"
                validVerifPassword = false
            }else{
                validVerifPassword = true
                validPasswordBaru = true
                pswdBaru.error = null
            }
        }
    }

    private fun photos() {
        tap += 1
        //check permission at runtime
        val checkSelfPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            //Requests permissions to be granted to this application at runtime
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }else{
            dispathGalleryIntent()
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK){
            try{
                var imagePath: String? = null
                val uri = data!!.data

                if (DocumentsContract.isDocumentUri(this, uri)){
                    val docId = DocumentsContract.getDocumentId(uri)
                    if ("com.android.providers.media.documents" == uri?.authority){
                        val id = docId.split(":")[1]
                        val selsetion = MediaStore.Images.Media._ID + "=" + id
                        imagePath = getImagePath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            selsetion)
                        //Toast.makeText(context, imagePath, Toast.LENGTH_LONG).show()
                    }
                    else if ("com.android.providers.downloads.documents" == uri?.authority){
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse(
                            "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                        imagePath = getImagePath(contentUri, null)
                        //Toast.makeText(context, imagePath, Toast.LENGTH_LONG).show()
                    }
                }
                else if ("content".equals(uri?.scheme, ignoreCase = true)){
                    //Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show()
                    imagePath = when (uri?.authority) {
                        "com.google.android.apps.docs.storage" -> {
                            getDriveFilePath(uri)
                        }
                        "com.google.android.apps.photos.content" -> {
                            uri.lastPathSegment
                        }
                        else -> {
                            getImagePathFromContent(uri!!)
                        }
                    }
                } else if ("file".equals(uri?.scheme, ignoreCase = true)){
                    imagePath = uri?.path
                }
                //Toast.makeText(context, imagePath, Toast.LENGTH_LONG).show()
                editFoto(member, imagePath!!)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun editFoto(member: MemberEntity?, path : String){
        loading()
        Handler().postDelayed({
            val file = File(path)

            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            //RequestBody.create(MediaType.parse("multipart/form-data"), uri.path!!)

            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)

            val noAnggota: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), member?.no_anggota.toString())

            NetworkConfig().api().editPhoto(noAnggota, body).enqueue(object : Callback<Respon>{
                override fun onFailure(call: Call<Respon>, t: Throwable) {
                    loading.dismiss()
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Respon>, response: Response<Respon>) {
                    loading.dismiss()
                    if(response.isSuccessful){
                        val res = response.body()
                        alertDialog(res?.status_code, res?.message, editDialog)
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }, 2000)
    }


    @SuppressLint("Recycle")
    private fun getDriveFilePath(uri: Uri): String? {
        val returnCursor =
            context!!.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        //val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        //val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context!!.cacheDir, name)
        try {
            val inputStream: InputStream? = context!!.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            //Log.e("File Size", "Size " + file.length())
            inputStream.close()
            outputStream.close()
            //Log.e("File Path", "Path " + file.path)
            //Log.e("File Size", "Size " + file.length())
        } catch (e: java.lang.Exception) {
            //Log.e("Exception", e.message)
        }
        return file.path
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                @Suppress("DEPRECATION")
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    @SuppressLint("Recycle")
    @Suppress("DEPRECATION")
    private fun getImagePathFromContent(uri: Uri) : String {
        var path : String? = null

        val isKitKat = Build.VERSION.SDK_INT > 19
        if (isKitKat) {
            path = generateFromKitkat(uri, context!!)
            //Toast.makeText(context, path, Toast.LENGTH_LONG).show()
        }

        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor?

        try {
            cursor =
                context!!.contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }
        return path!!
    }

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun generateFromKitkat(uri: Uri, context: Context): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":").toTypedArray()[1]
            val column =
                arrayOf(MediaStore.Video.Media.DATA)
            val sel = MediaStore.Video.Media._ID + "=?"
            val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )
            val columnIndex = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
    }


    private fun dispathGalleryIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PICTURE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when (requestCode) {
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    dispathGalleryIntent()
                } else {
                    Toast.makeText(context, "Unfortunately You are Denied Permission to Perform this Operation", Toast.LENGTH_LONG).show()
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alert(){
        alertDialog.setContentView(R.layout.alert_logout)

        val btnYa : Button = alertDialog.findViewById(R.id.btnYa)
        val btnNo : Button = alertDialog.findViewById(R.id.btnTidak)
        val tvIsi : TextView = alertDialog.findViewById(R.id.tvIsi)
        val tvJudul : TextView = alertDialog.findViewById(R.id.tvJudul)

        tvJudul.text = "Peringatan!"
        tvIsi.text = "Anda yakin ingin keluar akun ?"
        btnYa.setOnClickListener {
            alertDialog.dismiss()
            logout(member)

        }

        btnNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun logout(member : MemberEntity?){
        member.let { appDB?.memberDao()?.delete(it!!) }

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun alertDialog(status_code: Int?, message: String?, dialog: Dialog){

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
                    dialog.dismiss()

                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("id", bundleID)
                    intent.putExtra("simpanan", bundleSimpanan)
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
