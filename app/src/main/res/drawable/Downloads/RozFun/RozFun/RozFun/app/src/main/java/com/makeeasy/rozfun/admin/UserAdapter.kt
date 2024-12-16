package com.makeeasy.rozfun.admin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.model.User

class UserAdapter(private val ctx: Context, private val userList: MutableList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(LayoutInflater.from(ctx).inflate(R.layout.user, parent, false))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemHolder
        itemHolder.name.text = userList[position].name
        itemHolder.device.text = "(" + userList[position].device + ")"
        itemHolder.deposit.text = "DEPOSITED : " + userList[position].point
        itemHolder.withdraw.text = "REDEEMABLE : " + userList[position].redeem
        if(userList[position].status == "1") {
            itemHolder.status.text = "STATUS : ACTIVE"
        } else {
            itemHolder.status.text = "STATUS : BLOCK"
        }
        itemHolder.itemView.setOnClickListener {
            UserSheet(ctx,userList[position])
                .show((ctx as AppCompatActivity).supportFragmentManager,"Edit User")
        }
    }

    class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val device: TextView
        val deposit: TextView
        val withdraw: TextView
        val status: TextView
        init {
            name = itemView.findViewById(R.id.name)
            device = itemView.findViewById(R.id.device)
            deposit = itemView.findViewById(R.id.deposit)
            withdraw = itemView.findViewById(R.id.withdraw)
            status = itemView.findViewById(R.id.status)
        }
    }
}