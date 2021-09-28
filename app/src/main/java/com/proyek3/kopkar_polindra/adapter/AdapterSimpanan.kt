package com.proyek3.kopkar_polindra.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.model.DataSimpanan
import kotlinx.android.synthetic.main.items.view.*

class AdapterSimpanan(simpanan : List<DataSimpanan>, private var id: Int, private val jenis_simpanan : String) : RecyclerView.Adapter<AdapterSimpanan.ViewHolder>() {

    private var array = ArrayList(simpanan)
    private var simpanan = ArrayList(array)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_simpanan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return simpanan.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(simpanan[position], id, jenis_simpanan)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindView(dataSimpanan : DataSimpanan, id : Int, jenis_simpanan: String){
            when(id) {
                1 -> {
                    // simpanan pokok
                    itemView.cvItems.setCardBackgroundColor(Color.parseColor("#fa7470"))
                    itemView.cvRpItems.setCardBackgroundColor(Color.parseColor("#ef5350"))
                    itemView.imgItems.setBackgroundResource(R.drawable.pokok)
                    itemView.tvJudulItems.text = jenis_simpanan
                    itemView.tvTglItems.text = dataSimpanan.tanggal
                    itemView.tvNominalItems.text = dataSimpanan.jumlah
                }
                2 -> {
                    // simpanan sukarela
                    itemView.cvItems.setCardBackgroundColor(Color.parseColor("#4fc3f7"))
                    itemView.cvRpItems.setCardBackgroundColor(Color.parseColor("#83aae5"))
                    itemView.imgItems.setBackgroundResource(R.drawable.sukarela)
                    itemView.tvJudulItems.text = jenis_simpanan
                    itemView.tvTglItems.text = dataSimpanan.tanggal
                    itemView.tvNominalItems.text = dataSimpanan.jumlah
                }
                3 -> {
                    // simpnanan wajib
                    itemView.cvItems.setCardBackgroundColor(Color.parseColor("#f7ba7b"))
                    itemView.cvRpItems.setCardBackgroundColor(Color.parseColor("#f9a825"))
                    itemView.imgItems.setBackgroundResource(R.drawable.wajib)
                    itemView.tvJudulItems.text = jenis_simpanan
                    itemView.tvTglItems.text = dataSimpanan.tanggal
                    itemView.tvNominalItems.text = dataSimpanan.jumlah
                }
                4 -> {
                    // simpanan shu
                    itemView.cvItems.setCardBackgroundColor(Color.parseColor("#c7bad9"))
                    itemView.cvRpItems.setCardBackgroundColor(Color.parseColor("#a997c4"))
                    itemView.imgItems.setBackgroundResource(R.drawable.shu)
                    itemView.tvJudulItems.text = jenis_simpanan
                    itemView.tvTglItems.text = "Tahun ${dataSimpanan.tahun}"
                    itemView.tvNominalItems.text = dataSimpanan.jumlah
                }
            }

        }
    }

    @SuppressLint("DefaultLocale")
    fun filter(charText : String){
        var charText = charText
        charText = charText.toLowerCase()
        array.clear()

        if(charText.isEmpty()){
            array.addAll(simpanan)
        }else{
            for(i in simpanan.indices){
                if(simpanan[i].tanggal!!.toLowerCase().contains(charText) || simpanan[i].jumlah!!.toLowerCase().contains(charText)){
                    array.add(simpanan[i])
                }
            }
        }
        notifyDataSetChanged()
    }
}