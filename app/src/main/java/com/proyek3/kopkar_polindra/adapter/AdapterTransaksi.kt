package com.proyek3.kopkar_polindra.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyek3.kopkar_polindra.R
import com.proyek3.kopkar_polindra.model.DataTransaksi
import kotlinx.android.synthetic.main.items.view.*

class AdapterTransaksi(transaksi : List<DataTransaksi>) : RecyclerView.Adapter<AdapterTransaksi.ViewHolder>() {

    private var transaksi = ArrayList(transaksi)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transaksi.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(transaksi[position])
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindView(dataTransaksi : DataTransaksi){
            itemView.tvJudulItems.text = dataTransaksi.no_transaksi
            itemView.tvTglItems.text = dataTransaksi.tgl_transaksi
            itemView.tvNominalItems.text = dataTransaksi.nominal_transaksi
        }
    }

}