package com.spudg.pique

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.pique.databinding.BlockRowBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class BlockAdapter(private val context: Context, private val blocks: ArrayList<BlockModel>) :
    RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    inner class BlockViewHolder(val binding: BlockRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val binding = BlockRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {

        with(holder) {

            val format = DecimalFormat("#,###.##")

            Constants.getTimeAgo(blocks[position].timestamp)
            binding.tvTimestamp.text = Constants.getTimeAgo(blocks[position].timestamp)
            binding.tvHeight.text = "#" + format.format(blocks[position].height.toDouble())
            binding.tvSize.text = (BigDecimal(blocks[position].size.toDouble() / 1000000).setScale(
                2,
                RoundingMode.HALF_UP
            )).toString() + " MB"
            binding.tvTxCount.text = format.format(blocks[position].txCount.toFloat()) + " txs"
            binding.tvAveRate.text = "~" + blocks[position].aveRate + " sat/vB"
            binding.tvId.text = "..." + blocks[position].id.takeLast(5)

            binding.clBlock.setOnClickListener {
                if (context is MainActivity) {
                    context.showBlock(blocks[position].id)
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return blocks.size
    }


}