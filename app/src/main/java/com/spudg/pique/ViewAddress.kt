package com.spudg.pique

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.spudg.pique.databinding.ActivityViewAddressBinding
import okhttp3.*
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat


class ViewAddress : AppCompatActivity() {

    private lateinit var bindingViewAddress: ActivityViewAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingViewAddress = ActivityViewAddressBinding.inflate(layoutInflater)
        val view = bindingViewAddress.root
        setContentView(view)

        bindingViewAddress.tvAddress.text = Constants.ADDRESS

        setUpTotal()
        getTransactionList()

    }

    private fun setUpTotal() {
        var balanceTotal = 0F

        val url = "https://mempool.space/api/address/${Constants.ADDRESS}"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to get address details.")
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                if (response.code().toString() == "200") {
                    val addressInfo = gson.fromJson(
                        response.body()?.string(),
                        MainActivity.JsonInfo.Address::class.java
                    )
                    Handler(Looper.getMainLooper()).post(Runnable {
                        balanceTotal =
                            ((addressInfo.chain_stats.funded_txo_sum.toFloat() - addressInfo.chain_stats.spent_txo_sum.toFloat()))
                        var format = DecimalFormat("#.####")
                        format.roundingMode = RoundingMode.HALF_UP
                        bindingViewAddress.tvBalance.text =
                            format.format(balanceTotal.toDouble() / 100000000) + " BTC"

                        val urlPrice =
                            "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"
                        val requestPrice = Request.Builder().url(urlPrice).build()
                        client.newCall(requestPrice).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                Log.e("ERROR", "Failed to get price details.")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                Handler(Looper.getMainLooper()).post(Runnable {
                                    val formatUSD = DecimalFormat("$#,###")
                                    val priceInfo = gson.fromJson(
                                        response.body()?.string(),
                                        MainActivity.JsonInfo.Price::class.java
                                    )
                                    bindingViewAddress.tvValue.text =
                                        formatUSD.format((balanceTotal.toDouble() / 100000000) * priceInfo.bitcoin.usd.toDouble()) + "\u0020"
                                })
                            }
                        })
                    })
                } else {
                    Log.e("Pique", "API returned code " + response.code().toString())
                    Handler(Looper.getMainLooper()).post(Runnable {
                        bindingViewAddress.tvBalance.text =
                            "0.0000 BTC"
                        bindingViewAddress.tvValue.text = "$0.00 " + "\u0020"

                    })
                }
            }
        })

    }

    private fun getTransactionList() {
        val url = "https://mempool.space/api/address/${Constants.ADDRESS}/txs/chain"
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
                        val transactionInfo: Array<MainActivity.JsonInfo.Transaction> =
                            gson.fromJson(
                                response.body()?.string(),
                                Array<MainActivity.JsonInfo.Transaction>::class.java
                            )
                        val transactions: ArrayList<TransactionModel> = ArrayList()
                        for (transaction in transactionInfo) {
                            transactions.add(
                                TransactionModel(
                                    transaction.txid,
                                    transaction.size,
                                    transaction.fee,
                                    transaction.status.block_height,
                                    transaction.status.block_time
                                )
                            )
                        }
                        if (transactions.size > 0) {
                            bindingViewAddress.rvTransactions.visibility = View.VISIBLE
                            bindingViewAddress.tvNoTransactions.visibility = View.GONE
                            val manager = LinearLayoutManager(this@ViewAddress)
                            bindingViewAddress.rvTransactions.layoutManager = manager
                            val transactionAdapter =
                                TransactionAdapter(this@ViewAddress, transactions)
                            bindingViewAddress.rvTransactions.adapter = transactionAdapter
                        } else {
                            bindingViewAddress.rvTransactions.visibility = View.GONE
                            bindingViewAddress.tvNoTransactions.visibility = View.VISIBLE
                        }
                    })
                } else {
                    Log.e("Pique", "API returned code " + response.code().toString())


                }
            }
        })

    }
}