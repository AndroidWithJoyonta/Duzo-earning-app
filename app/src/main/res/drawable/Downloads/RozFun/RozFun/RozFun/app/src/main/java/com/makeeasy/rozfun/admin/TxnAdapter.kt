package com.makeeasy.rozfun.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.model.Txn

class TxnAdapter(private val ctx: Context, private val txnList: MutableList<Txn>, private val isWithdraw: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(LayoutInflater.from(ctx).inflate(R.layout.txn, parent, false))
    }

    override fun getItemCount(): Int {
        return txnList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemHolder
        itemHolder.amount.text = txnList[position].amount
        itemHolder.order.text = txnList[position].orderId
        itemHolder.txn.text = txnList[position].txnId
        if(txnList[position].type == "0") {
            itemHolder.type.text = "(Credit Request)"
        } else {
            itemHolder.type.text = "(Debit Request)"
        }
        when (txnList[position].status) {
            "1" -> {
                itemHolder.status.text = "success"
                itemHolder.status.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            }
            "2" -> {
                itemHolder.status.text = "reject"
                itemHolder.status.backgroundTintList = ColorStateList.valueOf(Color.RED)
            }
            else -> {
                itemHolder.status.text = "pending"
                itemHolder.status.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            }
        }
        itemHolder.itemView.setOnClickListener {
            if(isWithdraw) {
                StatusSheet(ctx,txnList[position])
                    .show((ctx as AppCompatActivity).supportFragmentManager,"Edit Transaction")
            }
        }
    }

    class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val amount: TextView
        val type: TextView
        val order: TextView
        val txn: TextView
        val status: TextView
        init {
            amount = itemView.findViewById(R.id.amount)
            type = itemView.findViewById(R.id.type)
            order = itemView.findViewById(R.id.order)
            txn = itemView.findViewById(R.id.txn)
            status = itemView.findViewById(R.id.status)
        }
    }
}