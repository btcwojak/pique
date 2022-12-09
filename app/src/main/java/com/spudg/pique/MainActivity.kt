package com.spudg.pique

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.gson.Gson
import com.spudg.pique.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    class JsonInfo {

        data class Address(
            val address: String,
            val chain_stats: ChainStats,
            val mempool_stats: MempoolStats
        )

        data class ChainStats(
            val funded_txo_count: String,
            val funded_txo_sum: String,
            val spent_txo_count: String,
            val spent_txo_sum: String,
            val tx_count: String,
        )

        data class MempoolStats(
            val funded_txo_count: String,
            val funded_txo_sum: String,
            val spent_txo_count: String,
            val spent_txo_sum: String,
            val tx_count: String,
        )

        data class Price(
            val bitcoin: Currency
        )

        data class Currency(
            val usd: String
        )

        data class Transaction(
            val txid: String,
            val size: String,
            val fee: String,
            val status: TransactionBlockInfo
        )

        data class TransactionBlockInfo(
            val block_height: String,
            val block_time: String
        )

        data class BlockSummary(
            val timestamp: String,
            val height: String,
            val tx_count: String,
            val size: String,
            val id: String,
            val extras: BlockExtras
        )

        data class BlockExtras(
            val avgFeeRate: String
        )

    }

    private lateinit var bindingMain: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingMain.root
        setContentView(view)

        getBlockList()

        bindingMain.tvSearchAddress.setOnClickListener {
            if (bindingMain.llSearchAddress.visibility == View.VISIBLE) {
                bindingMain.llSearchAddress.visibility = View.GONE
                bindingMain.tvSearchAddress.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(1F).duration = 75
            } else {
                bindingMain.llSearchAddress.visibility = View.VISIBLE
                bindingMain.tvSearchAddress.animate().scaleX(1.1F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(1.1F).duration = 75
                bindingMain.llSearchBlock.visibility = View.GONE
                bindingMain.tvSearchBlock.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(.9F).duration = 75
                bindingMain.llSearchTx.visibility = View.GONE
                bindingMain.tvSearchTx.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(.9F).duration = 75
            }

            if (bindingMain.llSearchAddress.visibility == View.GONE && bindingMain.llSearchBlock.visibility == View.GONE && bindingMain.llSearchTx.visibility == View.GONE) {
                val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

        bindingMain.tvSearchBlock.setOnClickListener {
            if (bindingMain.llSearchBlock.visibility == View.VISIBLE) {
                bindingMain.llSearchBlock.visibility = View.GONE
                bindingMain.tvSearchAddress.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(1F).duration = 75
            } else {
                bindingMain.llSearchAddress.visibility = View.GONE
                bindingMain.tvSearchAddress.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(.9F).duration = 75
                bindingMain.llSearchBlock.visibility = View.VISIBLE
                bindingMain.tvSearchBlock.animate().scaleX(1.1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(1.1F).duration = 75
                bindingMain.llSearchTx.visibility = View.GONE
                bindingMain.tvSearchTx.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(.9F).duration = 75

            }

            if (bindingMain.llSearchAddress.visibility == View.GONE && bindingMain.llSearchBlock.visibility == View.GONE && bindingMain.llSearchTx.visibility == View.GONE) {
                val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

        bindingMain.tvSearchTx.setOnClickListener {
            if (bindingMain.llSearchTx.visibility == View.VISIBLE) {
                bindingMain.llSearchTx.visibility = View.GONE
                bindingMain.tvSearchAddress.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleX(1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(1F).duration = 75
            } else {
                bindingMain.llSearchAddress.visibility = View.GONE
                bindingMain.tvSearchAddress.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchAddress.animate().scaleY(.9F).duration = 75
                bindingMain.llSearchBlock.visibility = View.GONE
                bindingMain.tvSearchBlock.animate().scaleX(.9F).duration = 75
                bindingMain.tvSearchBlock.animate().scaleY(.9F).duration = 75
                bindingMain.llSearchTx.visibility = View.VISIBLE
                bindingMain.tvSearchTx.animate().scaleX(1.1F).duration = 75
                bindingMain.tvSearchTx.animate().scaleY(1.1F).duration = 75
            }

            if (bindingMain.llSearchAddress.visibility == View.GONE && bindingMain.llSearchBlock.visibility == View.GONE && bindingMain.llSearchTx.visibility == View.GONE) {
                val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

    }

    private fun getBlockList() {
        val url = "https://mempool.space/api/v1/blocks/"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to get address details.")
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                if (response.code().toString() == "200") {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        val blockInfo: Array<JsonInfo.BlockSummary> =
                            gson.fromJson(
                                response.body()?.string(),
                                Array<JsonInfo.BlockSummary>::class.java
                            )
                        val blocks: ArrayList<BlockModel> = ArrayList()
                        for (block in blockInfo) {
                            blocks.add(
                                BlockModel(
                                    block.timestamp,
                                    block.height,
                                    block.tx_count,
                                    block.size,
                                    block.id,
                                    block.extras.avgFeeRate
                                )
                            )
                        }
                        if (blocks.size > 0) {
                            bindingMain.rvBlocks.visibility = View.VISIBLE
                            bindingMain.tvNoBlocks.visibility = View.GONE
                            val manager = LinearLayoutManager(this@MainActivity)
                            bindingMain.rvBlocks.layoutManager = manager
                            val blockAdapter = BlockAdapter(this@MainActivity, blocks)
                            bindingMain.rvBlocks.adapter = blockAdapter
                        } else {
                            bindingMain.rvBlocks.visibility = View.GONE
                            bindingMain.tvNoBlocks.visibility = View.VISIBLE
                        }
                    })
                } else {
                    Log.e("Pique", "API returned code " + response.code().toString())


                }
            }
        })

    }


}