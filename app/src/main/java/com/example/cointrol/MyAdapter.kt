package com.example.cointrol

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cointrol.database.Transaction

class MyAdapter(
    private val transactionsArrayList: ArrayList<Transaction>,
    private val context: Context
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.single_transaction, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transactionsArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transactionsArrayList[position]
        val color = if (currentItem.type.toString() == "income") Color.GREEN else Color.RED

        holder.desc.text = currentItem.desc
        holder.desc.setTextColor(color)
        holder.amount.text = currentItem.amount.toString()
        holder.amount.setTextColor(color)
        holder.date.text = currentItem.date.toString()
        holder.date.setTextColor(color)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val desc: TextView = itemView.findViewById(R.id.typeTextView)
        val amount: TextView = itemView.findViewById(R.id.amountTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
    }
}
