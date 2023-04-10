package com.google.codelabs.buildyourfirstmap

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                ROUT_COL + " INTEGER," +
                LONG_COl + " DECIMAL," +
                LAT_COL + " DECIMAL" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(lat: Double, long: Double, routeId: Int) {

        val values = ContentValues()
        values.put(LONG_COl, long)
        values.put(LAT_COL, lat)
        values.put(ROUT_COL, routeId)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getData(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getGroupedData(column: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME GROUP BY $column", null)
    }

    fun getRouteData(routeId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE routeId = $routeId ", null)
    }

    fun getLastData(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY id DESC LIMIT 1", null)
    }

    companion object {
        private const val DATABASE_NAME = "MAPS"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "routes_table"
        const val ID_COL = "id"
        const val LONG_COl = "longitude"
        const val LAT_COL = "latitude"
        const val ROUT_COL = "routeId"
    }
}

/*
*  rota, lat, long
* */