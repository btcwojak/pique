package com.spudg.pique

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.pique.databinding.TransactionRowBinding

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

            binding.tvTxid.text = transactions[position].txid
            binding.tvSize.text = transactions[position].size
            binding.tvFee.text = transactions[position].fee
            binding.tvBlockHeight.text = transactions[position].block_height
            binding.tvBlockTime.text = transactions[position].block_time

        }

    }

    override fun getItemCount(): Int {
        return transactions.size
    }


}