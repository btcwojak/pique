package com.spudg.pique

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.spudg.pique.databinding.ActivityViewAddressBinding
import okhttp3.*
import java.io.IOException
import java.text.DecimalFormat

class ViewAddress : AppCompatActivity() {

    private lateinit var bindingViewAddress: ActivityViewAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingViewAddress = ActivityViewAddressBinding.inflate(layoutInflater)
        val view = bindingViewAddress.root
        setContentView(view)

        getAddress(Constants.SELECTED_ADDRESS)
        getTransactions(Constants.SELECTED_ADDRESS)

        bindingViewAddress.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun getAddress(address: String) {
        val url = "https://mempool.space/api/address/$address"
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
                        val addressInfo: MainActivity.JsonInfo.AddressSummary =
                            gson.fromJson(
                                response.body()?.string(),
                                MainActivity.JsonInfo.AddressSummary::class.java
                            )

                        val address = AddressModel(
                            addressInfo.address,
                            addressInfo.chain_stats.funded_txo_count,
                            addressInfo.chain_stats.funded_txo_sum,
                            addressInfo.chain_stats.spent_txo_count,
                            addressInfo.chain_stats.spent_txo_sum,
                            addressInfo.chain_stats.tx_count,
                            addressInfo.mempool_stats.funded_txo_count,
                            addressInfo.mempool_stats.funded_txo_sum,
                            addressInfo.mempool_stats.spent_txo_count,
                            addressInfo.mempool_stats.spent_txo_sum,
                            addressInfo.mempool_stats.tx_count,
                        )

                        val formatRounded = DecimalFormat("#,###")

                        bindingViewAddress.tvAddressInfo.text =
                            "This address, ${address.address}, has ${address.fundedCount} inputs and ${address.spentCount} outputs across ${address.txCount} transactions."

                        if ((address.fundedCountMem.toFloat() + address.spentCountMem.toFloat() + address.txCountMem.toFloat()) > 0F) {
                            bindingViewAddress.tvMempool.text =
                                "There are also ${address.fundedCountMem} inputs and ${address.spentCountMem} outputs across ${address.txCountMem} transactions in the mempool for this address (not included below)."
                        } else {
                            bindingViewAddress.tvMempool.text =
                                "There are no transactions currently in the mempool for this address."
                        }

                        bindingViewAddress.tvFunded.text =
                            formatRounded.format(address.fundedSum.toFloat()) + " sats"
                        bindingViewAddress.tvSpent.text =
                            formatRounded.format(address.spentSum.toFloat()) + " sats"
                        bindingViewAddress.tvBalance.text =
                            formatRounded.format(address.fundedSum.toFloat() - address.spentSum.toFloat()) + " sats"

                    })
                } else {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Log.e("Pique", "API returned code " + response.code().toString())

                        Toast.makeText(
                            this@ViewAddress,
                            "Address not recognised.",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    })
                }
            }
        })
    }

    private fun getTransactions(address: String) {
        val url = "https://mempool.space/api/address/$address/txs"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to get transaction details.")
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                if (response.code().toString() == "200") {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        val txInfo: Array<MainActivity.JsonInfo.TransactionSummary> =
                            gson.fromJson(
                                response.body()?.string(),
                                Array<MainActivity.JsonInfo.TransactionSummary>::class.java
                            )

                        val transactions: ArrayList<TransactionModel> = ArrayList()

                        for (tx in txInfo) {
                            val inputs: ArrayList<IOModel> = ArrayList()
                            val outputs: ArrayList<IOModel> = ArrayList()
                            for (input in tx.vin) {
                                inputs.add(
                                    IOModel(
                                        input.prevout.scriptpubkey_address,
                                        input.prevout.value
                                    )
                                )
                            }

                            for (output in tx.vout) {
                                outputs.add(IOModel(output.scriptpubkey_address, output.value))
                            }

                            transactions.add(
                                TransactionModel(
                                    tx.txid,
                                    tx.size,
                                    tx.weight,
                                    tx.fee,
                                    tx.status.confirmed,
                                    tx.status.block_height,
                                    tx.status.block_time,
                                    inputs,
                                    outputs
                                )
                            )
                        }

                        if (transactions.size > 0) {
                            bindingViewAddress.rvTransactions.visibility = View.VISIBLE
                            val manager = LinearLayoutManager(this@ViewAddress)
                            bindingViewAddress.rvTransactions.layoutManager = manager
                            val txAdapter = TransactionAdapter(this@ViewAddress, transactions)
                            bindingViewAddress.rvTransactions.adapter = txAdapter
                        } else {
                            bindingViewAddress.rvTransactions.visibility = View.GONE
                        }


                    })
                } else {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Log.e("Pique", "API returned code " + response.code().toString())

                        Toast.makeText(this@ViewAddress, "Address not found.", Toast.LENGTH_SHORT)
                            .show()

                        finish()
                    })
                }
            }
        })
    }


}