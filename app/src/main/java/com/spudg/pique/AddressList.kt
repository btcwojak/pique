package com.spudg.pique

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.spudg.pique.databinding.ActivityAddressListBinding
import com.spudg.pique.databinding.DialogAddAddressBinding
import com.spudg.pique.databinding.DialogDeleteAddressBinding
import okhttp3.*
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat

class AddressList : AppCompatActivity() {

    private lateinit var bindingAddresses: ActivityAddressListBinding
    private lateinit var bindingAddAddress: DialogAddAddressBinding
    private lateinit var bindingDeleteAddress: DialogDeleteAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingAddresses = ActivityAddressListBinding.inflate(layoutInflater)
        val view = bindingAddresses.root
        setContentView(view)

        setUpTotal()
        setUpAddressList()

        bindingAddresses.btnAddAddress.setOnClickListener {
            val addDialog = Dialog(this, R.style.Theme_Dialog)
            addDialog.setCancelable(false)
            bindingAddAddress = DialogAddAddressBinding.inflate(layoutInflater)
            val view = bindingAddAddress.root
            addDialog.setContentView(view)
            addDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            bindingAddAddress.btnAdd.setOnClickListener {
                var address = bindingAddAddress.etAddress.text.toString()

                if (address.isNotEmpty()) {
                    val dbHandler = AddressHandler(this, null)
                    dbHandler.addAddress(address)
                    dbHandler.close()
                    setUpTotal()
                    setUpAddressList()
                    addDialog.dismiss()
                } else {
                    Toast.makeText(this, "Address can't be empty.", Toast.LENGTH_SHORT).show()
                }


            }

            bindingAddAddress.btnClose.setOnClickListener {
                addDialog.dismiss()
            }

            addDialog.show()


        }
    }

    private fun setUpTotal() {
        val dbHandler = AddressHandler(this, null)
        val addresses = dbHandler.getAddresses()

        var balanceTotal = 0F
        var inputTotal = 0F
        var outputTotal = 0F
        var addressNo = 0F

        for (address in addresses) {
            val url = "https://mempool.space/api/address/${address.address}"
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
                            addressNo += 1
                            balanceTotal += ((addressInfo.chain_stats.funded_txo_sum.toFloat() - addressInfo.chain_stats.spent_txo_sum.toFloat()))
                            inputTotal += addressInfo.chain_stats.funded_txo_sum.toFloat()
                            outputTotal += addressInfo.chain_stats.spent_txo_sum.toFloat()
                            if (addresses.size <= addressNo) {
                                var format = DecimalFormat("#.####")
                                format.roundingMode = RoundingMode.HALF_UP
                                bindingAddresses.tvBalance.text =
                                    format.format(balanceTotal.toDouble() / 100000000) + " BTC"
                                bindingAddresses.tvInputs.text =
                                    format.format(inputTotal.toDouble() / 100000000) + " BTC"
                                bindingAddresses.tvOutputs.text =
                                    format.format(outputTotal.toDouble() / 100000000) + " BTC"

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
                                            bindingAddresses.tvValue.text =
                                                formatUSD.format((balanceTotal.toDouble() / 100000000) * priceInfo.bitcoin.usd.toDouble()) + "\u0020"
                                        })
                                    }
                                })
                            }
                        })
                    } else {
                        Log.e("Pique", "API returned code " + response.code().toString())
                        Handler(Looper.getMainLooper()).post(Runnable {
                            addressNo += 1
                            if (addresses.size <= addressNo) {
                                var format = DecimalFormat("#.####")
                                format.roundingMode = RoundingMode.HALF_UP
                                bindingAddresses.tvBalance.text =
                                    format.format(balanceTotal.toDouble() / 100000000) + " BTC"
                                bindingAddresses.tvInputs.text =
                                    format.format(inputTotal.toDouble() / 100000000) + " BTC"
                                bindingAddresses.tvOutputs.text =
                                    format.format(outputTotal.toDouble() / 100000000) + " BTC"

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
                                            bindingAddresses.tvValue.text =
                                                formatUSD.format((balanceTotal.toDouble() / 100000000) * priceInfo.bitcoin.usd.toDouble()) + "\u0020"
                                        })
                                    }
                                })
                            }
                        })
                    }
                }
            })
        }


    }

    fun deleteAddress(address: AddressModel) {
        val deleteDialog = Dialog(this, R.style.Theme_Dialog)
        deleteDialog.setCancelable(false)
        bindingDeleteAddress = DialogDeleteAddressBinding.inflate(layoutInflater)
        val view = bindingDeleteAddress.root
        deleteDialog.setContentView(view)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindingDeleteAddress.btnDelete.setOnClickListener {
            val dbHandler = AddressHandler(this, null)
            dbHandler.deleteAddress(
                AddressModel(
                    address.id,
                    address.address,
                )
            )

            Toast.makeText(this, "Address deleted.", Toast.LENGTH_LONG).show()
            setUpAddressList()
            dbHandler.close()
            deleteDialog.dismiss()
        }

        bindingDeleteAddress.btnClose.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()

    }

    private fun getAddressList(): ArrayList<AddressModel> {
        val dbHandler = AddressHandler(this, null)
        val result = dbHandler.getAddresses()
        dbHandler.close()
        return result
    }

    private fun setUpAddressList() {
        if (getAddressList().size > 0) {
            bindingAddresses.rvAddresses.visibility = View.VISIBLE
            bindingAddresses.tvNoAddresses.visibility = View.GONE
            val manager = LinearLayoutManager(this)
            bindingAddresses.rvAddresses.layoutManager = manager
            val addressAdapter = AddressAdapter(this, getAddressList())
            bindingAddresses.rvAddresses.adapter = addressAdapter
        } else {
            bindingAddresses.rvAddresses.visibility = View.GONE
            bindingAddresses.tvNoAddresses.visibility = View.VISIBLE
        }
    }
}