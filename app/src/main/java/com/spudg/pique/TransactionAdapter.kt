package com.spudg.pique

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.pique.databinding.IoRowBinding
import com.spudg.pique.databinding.TransactionRowBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val context: Context, private val transactions: ArrayList<TransactionModel>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: TransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {

        with(holder) {

            val tx = transactions[position]

            var totalInputs = 0F
            var totalOutputs = 0F

            for (input in tx.vin) {
                totalInputs += input.amount.toFloat()
            }

            for (output in tx.vout) {
                totalOutputs += output.amount.toFloat()
            }

            val formatRounded = DecimalFormat("#,###")
            binding.tvTxId.text = tx.txid
            binding.tvInputs.text = totalInputs.toString()
            binding.tvOutputs.text = totalOutputs.toString()
            binding.tvFee.text = tx.fee
            binding.tvConfirmed.text = "Confirmed in block " + tx.blockHeight + " on " + getDate(tx.blockTime, "dd MMMM yyyy, hh:mm") + " UTC."

        }

    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    private fun getDate(ms: String, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat);
        val calendar = Calendar.getInstance();
        calendar.timeInMillis = (ms + "000").toLong();
        return formatter.format(calendar.time);
    }


}