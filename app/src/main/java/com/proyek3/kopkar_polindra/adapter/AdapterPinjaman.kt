package com.proyek3.kopkar_polindra.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.model.DataPinjaman
import kotlinx.android.synthetic.main.items_pinjaman.view.*

class AdapterPinjaman (pinjaman : List<DataPinjaman>) : RecyclerView.Adapter<AdapterPinjaman.ViewHolder>(){

    private var pinjaman = ArrayList(pinjaman)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_pinjaman, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pinjaman.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(pinjaman[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bindView(dataPinjaman: DataPinjaman) {
            itemView.tvNominal.text = "Rp. ${dataPinjaman.jumlah}"
            itemView.tvTglItems.text = dataPinjaman.created_at

            if(dataPinjaman.status == 0){
                itemView.cvStatus.setCardBackgroundColor(Color.parseColor("#e57373"))
                itemView.tvStatus.text = "Belum Disetujui"
            }else{
                itemView.tvStatus.text = "Sudah Disetujui"
                itemView.cvStatus.setCardBackgroundColor(Color.parseColor("#92e500"))
            }
        }
    }

}