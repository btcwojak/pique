package com.spudg.pique

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spudg.pique.databinding.BlockRowBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt

class BlockAdapter(private val context: Context, private val blocks: ArrayList<BlockModel>) :
    RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    private fun getTimeAgo(date: String): String {
        val now = Calendar.getInstance().timeInMillis
        val then = date + "000"
        val diff = now.toFloat() - then.toFloat()
        val formatted = (diff / 1000 / 60).roundToInt()
        return if (formatted == 0) {
            "just now"
        } else if (formatted == 1) {
            "1 minute ago"
        } else if (formatted in 2..59) {
            "$formatted minutes ago"
        } else if (formatted in 60..89) {
            "1 hour ago"
        } else if ((formatted.toDouble() / 60) >= 1.5) {
            (formatted.toDouble() / 60).roundToInt().toString() + " hours ago"
        } else {
            "$formatted minutes ago"
        }
    }

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

            getTimeAgo(blocks[position].timestamp)
            binding.tvTimestamp.text = getTimeAgo(blocks[position].timestamp)
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