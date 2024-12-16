package com.makeeasy.rozfun.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeeasy.rozfun.R

class HomeAdapter(private val type: Int, private val onSelectListener: OnSelectListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnSelectListener {
        fun onSelect(position: Int, title: String)
    }
    private val names = arrayListOf("Admin Access","Spin Points","User List","Transactions","Withdraw Req","Settings")
    private val images = intArrayOf(R.drawable.adm,R.drawable.spn,R.drawable.usr,R.drawable.txn,R.drawable.wit,R.drawable.set)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(type == 0) {
            ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false))
        } else {
            ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemHolder
        itemHolder.title.text = names[position]
        itemHolder.image.setImageResource(images[position])
        itemHolder.itemView.setOnClickListener {
            onSelectListener.onSelect(position,names[position])
        }
    }

    class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val image: ImageView
        init {
            title = itemView.findViewById(R.id.title)
            image = itemView.findViewById(R.id.img)
        }
    }
}