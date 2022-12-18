package com.spudg.pique

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.spudg.pique.databinding.ActivityMainBinding
import com.spudg.pique.databinding.DialogRefreshInfoBinding
import com.spudg.pique.databinding.DialogViewBlockBinding
import okhttp3.*
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var bindingDialogViewBlock: DialogViewBlockBinding
    private lateinit var bindingDialogRefreshInfo: DialogRefreshInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        val view = bindingMain.root
        setContentView(view)

        setPrice()
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
                val imm =
                    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
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
                val imm =
                    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
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
                val imm =
                    ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }

        bindingMain.btnSearchAddress.setOnClickListener {
            val input = bindingMain.etSearchAddress.text.toString()
            if (input.isNotEmpty()) {
                Constants.SELECTED_ADDRESS = input
                val intent = Intent(this, ViewAddress::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Enter an address.", Toast.LENGTH_SHORT).show()
            }
        }

        bindingMain.btnSearchBlock.setOnClickListener {
            val input = bindingMain.etSearchBlock.text.toString()
            if (input.isNotEmpty()) {
                if (input.length == 64) {
                    showBlock(input)
                } else {
                    showBlockUsingHeight(input)
                }
            } else {
                Toast.makeText(this, "Enter a block height or hash.", Toast.LENGTH_SHORT).show()
            }
        }

        bindingMain.btnSearchTx.setOnClickListener {
            val input = bindingMain.etSearchTx.text.toString()
            if (input.isNotEmpty()) {
                if (input.length == 64) {
                    Constants.SELECTED_TX = input
                    val intent = Intent(this, ViewTransaction::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Invalid transaction ID.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter a transaction ID (64 chars).", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        bindingMain.btnInfo.setOnClickListener {
            showInfoDialog()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            getBlockList()
        }, 30000)

    }

    private fun showBlockUsingHeight(height: String) {
        val url = "https://mempool.space/api/block-height/$height"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to get block details.")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code().toString() == "200") {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        showBlock(response.body()!!.string())
                    })
                } else {
                    Log.e("Pique", "API returned code " + response.code().toString())
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Toast.makeText(
                            this@MainActivity,
                            "Block height not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        })
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
                        bindingMain.clBlocks.visibility = View.VISIBLE
                        bindingMain.tvNoConnection.visibility = View.GONE
                        val blockInfo: Array<Constants.Companion.JsonInfo.BlockSummary> =
                            gson.fromJson(
                                response.body()?.string(),
                                Array<Constants.Companion.JsonInfo.BlockSummary>::class.java
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
                                    block.extras.avgFeeRate,
                                    block.extras.reward,
                                    (block.extras.reward.toFloat() - block.extras.totalFees.toFloat()).toString(),
                                    block.extras.totalFees,
                                    block.extras.avgFee,
                                    block.extras.feeRange[block.extras.feeRange.size - 1],
                                    block.extras.feeRange[0],
                                    block.previousblockhash
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
                    Handler(Looper.getMainLooper()).post(Runnable {
                        bindingMain.tvNoConnection.text = "Error returned from API."
                    })
                }
            }
        })

    }

    fun showBlock(hash: String) {
        val url = "https://mempool.space/api/v1/block/$hash"
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
                        val blockInfo: Constants.Companion.JsonInfo.BlockSummary =
                            gson.fromJson(
                                response.body()?.string(),
                                Constants.Companion.JsonInfo.BlockSummary::class.java
                            )

                        var prevHash = blockInfo.previousblockhash
                        if (blockInfo.height == "0") {
                            prevHash = "N/A - Genesis"
                        }

                        val block = BlockModel(
                            blockInfo.timestamp,
                            blockInfo.height,
                            blockInfo.tx_count,
                            blockInfo.size,
                            blockInfo.id,
                            blockInfo.extras.avgFeeRate,
                            blockInfo.extras.reward,
                            (blockInfo.extras.reward.toFloat() - blockInfo.extras.totalFees.toFloat()).toString(),
                            blockInfo.extras.totalFees,
                            blockInfo.extras.avgFee,
                            blockInfo.extras.feeRange[blockInfo.extras.feeRange.size - 1],
                            blockInfo.extras.feeRange[0],
                            prevHash
                        )

                        val blockDialog = Dialog(this@MainActivity, R.style.Theme_Dialog)
                        blockDialog.setCancelable(false)
                        bindingDialogViewBlock = DialogViewBlockBinding.inflate(layoutInflater)
                        val view = bindingDialogViewBlock.root
                        blockDialog.setContentView(view)
                        blockDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        val formatRounded = DecimalFormat("#,###")
                        val formatUSD = DecimalFormat("$#,###")

                        if (blockInfo.height == "0") {
                            bindingDialogViewBlock.tvBlockTitle.text =
                                "Block #" + formatRounded.format(block.height.toFloat()) + " (Genesis)"
                        } else {
                            bindingDialogViewBlock.tvBlockTitle.text =
                                "Block #" + formatRounded.format(block.height.toFloat())
                        }

                        bindingDialogViewBlock.tvGeneralInfo.text =
                            "Mined " + Constants.getTimeAgo(block.timestamp) + ". This block contains " + formatRounded.format(
                                block.txCount.toFloat()
                            ) + " transactions, an average fee rate of ~" + block.aveRate + " sat/vB and has a size of " + (BigDecimal(
                                block.size.toDouble() / 1000000
                            ).setScale(
                                2,
                                RoundingMode.HALF_UP
                            )).toString() + " MB."
                        bindingDialogViewBlock.tvReward.text =
                            formatRounded.format(block.reward.toFloat()) + " sats (" + formatUSD.format(
                                Constants.PRICE.toFloat() * (block.reward.toFloat() / 100000000)
                            ) + ")"
                        bindingDialogViewBlock.tvSubsidy.text =
                            formatRounded.format(block.subsidy.toFloat()) + " sats (" + formatUSD.format(
                                Constants.PRICE.toFloat() * (block.subsidy.toFloat() / 100000000)
                            ) + ")"
                        bindingDialogViewBlock.tvFees.text =
                            formatRounded.format(block.fees.toFloat()) + " sats (" + formatUSD.format(
                                Constants.PRICE.toFloat() * (block.fees.toFloat() / 100000000)
                            ) + ")"
                        bindingDialogViewBlock.tvAveFee.text =
                            formatRounded.format(block.aveFee.toFloat()) + " sats (" + formatUSD.format(
                                Constants.PRICE.toFloat() * (block.aveFee.toFloat() / 100000000)
                            ) + ")"
                        bindingDialogViewBlock.tvFeeRange.text =
                            formatRounded.format(block.lowFee.toFloat()) + " - " + formatRounded.format(
                                block.highFee.toFloat()
                            ) + " sat/vB"
                        bindingDialogViewBlock.tvId.text = block.id
                        bindingDialogViewBlock.tvPrevHash.text = block.prevHash

                        bindingDialogViewBlock.btnClose.setOnClickListener {
                            blockDialog.dismiss()
                        }

                        blockDialog.show()

                    })
                } else {
                    Handler(Looper.getMainLooper()).post(Runnable {
                        Log.e("Pique", "API returned code " + response.code().toString())

                        Toast.makeText(
                            this@MainActivity,
                            "Blockhash not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        })
    }

    private fun showInfoDialog() {
        val infoDialog = Dialog(this@MainActivity, R.style.Theme_Dialog)
        infoDialog.setCancelable(false)
        bindingDialogRefreshInfo = DialogRefreshInfoBinding.inflate(layoutInflater)
        val view = bindingDialogRefreshInfo.root
        infoDialog.setContentView(view)
        infoDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDialogRefreshInfo.infoText.movementMethod = LinkMovementMethod.getInstance()

        bindingDialogRefreshInfo.btnClose.setOnClickListener {
            infoDialog.dismiss()
        }

        infoDialog.show()
    }

    private fun setPrice() {
        val urlPrice =
            "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd"
        val requestPrice = Request.Builder().url(urlPrice).build()
        val client = OkHttpClient()
        client.newCall(requestPrice).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", "Failed to get price details.")
            }

            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val gson = Gson()
                    val priceInfo = gson.fromJson(
                        response.body()?.string(),
                        Constants.Companion.JsonInfo.Price::class.java
                    )
                    Constants.PRICE = priceInfo.bitcoin.usd
                })
            }
        })
    }


}