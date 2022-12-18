package com.spudg.pique

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.spudg.pique.databinding.TransactionRowBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val context: Context,
    private val transactions: ArrayList<TransactionModel>
) :
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

            val formatUSD = DecimalFormat("$#,###")
            val formatRounded = DecimalFormat("#,###")
            binding.tvTxId.text = tx.txid
            binding.tvInputs.text = formatRounded.format(totalInputs.toString().toFloat()) + " sats (" + formatUSD.format(Constants.PRICE.toFloat()*(totalInputs.toString().toFloat()/100000000)) + ")"
            binding.tvOutputs.text =
                formatRounded.format(totalOutputs.toString().toFloat()) + " sats (" + formatUSD.format(Constants.PRICE.toFloat()*(totalOutputs.toString().toFloat()/100000000)) + ")"
            binding.tvFee.text = formatRounded.format(tx.fee.toFloat()) + " sats (" + formatUSD.format(Constants.PRICE.toFloat()*(tx.fee.toFloat()/100000000)) + ")"
            binding.tvConfirmed.text =
                "Confirmed in block #" + formatRounded.format(tx.blockHeight.toFloat()) + " on " + Constants.getDate(
                    tx.blockTime,
                    "dd MMMM yyyy, HH:mm"
                ) + " UTC."

            binding.llTxRow.setOnClickListener {
                Constants.SELECTED_TX = tx.txid
                val intent = Intent(context, ViewTransaction::class.java)
                startActivity(context, intent, null)
            }

        }

    }

    override fun getItemCount(): Int {
        return transactions.size
    }


}