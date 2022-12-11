package com.spudg.pique

class TransactionModel(
    val txid: String,
    val size: String,
    val weight: String,
    val fee: String,
    val confirmed: String,
    val blockHeight: String,
    val blockTime: String,
    val vin: ArrayList<IOModel>,
    val vout: ArrayList<IOModel>
)