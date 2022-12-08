package com.spudg.pique

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AddressHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "Addresses.db"
        private const val TABLE_ADDRESSES = "addresses"

        private const val KEY_ID = "_id"
        private const val KEY_ADDRESS = "address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createAccountsTable =
            ("CREATE TABLE $TABLE_ADDRESSES($KEY_ID INTEGER PRIMARY KEY,$KEY_ADDRESS TEXT)")
        db?.execSQL(createAccountsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADDRESSES")
        onCreate(db)
    }

    fun addAddress(address: String): Long {
        val values = ContentValues()
        values.put(KEY_ADDRESS, address)

        val db = this.writableDatabase

        val success = db.insert(TABLE_ADDRESSES, null, values)

        db.close()

        return success

    }

    fun deleteAddress(address: AddressModel): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_ADDRESSES, KEY_ID + "=" + address.id, null)
        db.close()
        return success
    }

    fun getAddresses(): ArrayList<AddressModel> {
        val list = ArrayList<AddressModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ADDRESSES",
            null
        )

        var id: Int
        var address: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS))
                val address = AddressModel(id, address)
                list.add(address)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return list

    }

}