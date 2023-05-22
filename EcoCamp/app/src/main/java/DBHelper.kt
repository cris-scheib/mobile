package com.google.codelabs.buildyourfirstmap

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE $COMPLAINTS_TABLE_NAME (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                IMAGE + " TEXT, " +
                IMAGE_SEC + " TEXT, " +
                LONG_COl + " DECIMAL, " +
                LAT_COL + " DECIMAL" + ");")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $COMPLAINTS_TABLE_NAME")
        onCreate(db)
    }

    fun addDataComplaint(name: String, description: String, latitude: Double, longitude: Double, image: String?, image_sec: String?) {
        val values = ContentValues()
        values.put(NAME, name)
        values.put(DESCRIPTION, description)
        values.put(IMAGE, image)
        values.put(IMAGE_SEC, image_sec)
        values.put(LONG_COl, longitude)
        values.put(LAT_COL, latitude)

        val db = this.writableDatabase
        db.insert(COMPLAINTS_TABLE_NAME, null, values)
        db.close()
    }

    fun getData(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $COMPLAINTS_TABLE_NAME", null)
    }

    fun getGroupedData(table: String, column: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table GROUP BY $column", null)
    }

    fun getDataByPosition(latitude: Double, longitude: Double): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $COMPLAINTS_TABLE_NAME WHERE  $LAT_COL = $latitude AND $LONG_COl = $longitude", null)
    }

    fun getLastData(table: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table ORDER BY $ID_COL DESC LIMIT 1", null)
    }

    companion object {
        private const val DATABASE_NAME = "COMPLAINTS"
        private const val DATABASE_VERSION = 1
        const val COMPLAINTS_TABLE_NAME = "complaints_table"
        const val ID_COL = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val LONG_COl = "longitude"
        const val LAT_COL = "latitude"
        const val IMAGE = "image"
        const val IMAGE_SEC = "image_sec"
    }
}

