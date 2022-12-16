package com.spudg.pique

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class Constants {

    companion object {

        val formatRounded = DecimalFormat("#,###")

        var SELECTED_TX: String = "0"
        var SELECTED_ADDRESS: String = "0"
        var PRICE: String = "0"

        fun getTimeAgo(date: String): String {
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
            } else if ((formatted.toDouble() / 60) in 1.5..23.0) {
                (formatted.toDouble() / 60).roundToInt().toString() + " hours ago"
            } else if (((formatted.toDouble() / 60) > 23.0) && ((formatted.toDouble() / 60) < 36.0)) {
                "1 day ago"
            } else if ((formatted.toDouble() / 60) >= 36.0) {
                "${formatRounded.format(((formatted.toDouble() / 60)/24).roundToInt())} days ago"
            } else {
                "$formatted minutes ago"
            }
        }

        fun getDate(ms: String, dateFormat: String): String {
            val formatter = SimpleDateFormat(dateFormat);
            val calendar = Calendar.getInstance();
            calendar.timeInMillis = (ms + "000").toLong();
            return formatter.format(calendar.time);
        }

        class JsonInfo {

            data class BlockSummary(
                val timestamp: String,
                val height: String,
                val tx_count: String,
                val size: String,
                val id: String,
                val extras: BlockExtras,
                val previousblockhash: String
            )

            data class BlockExtras(
                val avgFeeRate: String,
                val feeRange: Array<String>,
                val avgFee: String,
                val reward: String,
                val totalFees: String,
            )

            data class TransactionSummary(
                val txid: String,
                val size: String,
                val weight: String,
                val fee: String,
                val vin: Array<InputInfo>,
                val vout: Array<OutputInfo>,
                val status: TransactionExtras
            )

            data class TransactionExtras(
                val confirmed: String,
                val block_height: String,
                val block_time: String
            )

            data class InputInfo(
                val prevout: InputExtras
            )

            data class InputExtras(
                val scriptpubkey_address: String,
                val value: String
            )

            data class OutputInfo(
                val scriptpubkey_address: String,
                val value: String
            )

            data class AddressSummary(
                val address: String,
                val chain_stats: ChainInfo,
                val mempool_stats: MempoolInfo
            )

            data class ChainInfo(
                val funded_txo_count: String,
                val funded_txo_sum: String,
                val spent_txo_count: String,
                val spent_txo_sum: String,
                val tx_count: String
            )

            data class MempoolInfo(
                val funded_txo_count: String,
                val funded_txo_sum: String,
                val spent_txo_count: String,
                val spent_txo_sum: String,
                val tx_count: String
            )

            data class Price(
                val bitcoin: Currency
            )

            data class Currency(
                val usd: String
            )

        }

    }





}
