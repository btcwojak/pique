package com.spudg.pique

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.spudg.pique.databinding.AddressRowBinding
import okhttp3.*
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat

class AddressAdapter(private val context: Context, private val items: ArrayList<AddressModel>) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {


    inner class AddressViewHolder(val binding: AddressRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = AddressRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        with(holder) {
            val address = items[position]

            val url = "https://mempool.space/api/address/${address.address}"

            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ERROR", "Failed to get address details.")
                }

                override fun onResponse(call: Call, response: Response) {
                    var gson = Gson()

                    if (response.code().toString() == "200") {
                        var addressInfo = gson.fromJson(
                            response.body()?.string(),
                            MainActivity.JsonInfo.Address::class.java
                        )
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.llAddress.setOnClickListener {
                                if (context is AddressList) {
                                    Constants.ADDRESS = address.address
                                    val intent = Intent(context, ViewAddress::class.java)
                                    ContextCompat.startActivity(context, intent, null)
                                }
                            }
                            var balance =
                                (addressInfo.chain_stats.funded_txo_sum.toDouble() / 100000000) - (addressInfo.chain_stats.spent_txo_sum.toDouble() / 100000000)
                            var format = DecimalFormat("#.####")
                            format.roundingMode = RoundingMode.HALF_UP
                            binding.tvAddress.text = addressInfo.address
                            binding.tvBalance.text = format.format(balance) + " BTC"
                        })
                    } else {
                        Log.e("Pique", "API returned code " + response.code().toString())
                        Handler(Looper.getMainLooper()).post(Runnable {
                            binding.llAddress.setOnClickListener {
                                if (context is AddressList) {
                                    context.deleteAddress(address)
                                }
                            }
                            binding.tvAddress.text = address.address
                            binding.tvBalance.text = "Invalid address"
                        })
                    }

                }
            })

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


}