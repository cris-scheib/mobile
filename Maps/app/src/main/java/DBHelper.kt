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
        val routesQuery = ("CREATE TABLE $ROUTES_TABLE_NAME (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT" + ");")

        val positionsQuery = ("CREATE TABLE $POSITIONS_TABLE_NAME  (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                ROUT_COL + " INTEGER, " +
                LONG_COl + " DECIMAL, " +
                LAT_COL + " DECIMAL, " +
                "FOREIGN KEY ($ROUT_COL) REFERENCES $ROUTES_TABLE_NAME ($ID_COL));")

        val alertsQuery = ("CREATE TABLE $ALERTS_TABLE_NAME (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                POSIT_COL + " INTEGER, " +
                NAME + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                IMAGE + " TEXT, " +
                "FOREIGN KEY ($POSIT_COL) REFERENCES $POSITIONS_TABLE_NAME ($ID_COL));")

        db.execSQL(routesQuery)
        db.execSQL(positionsQuery)
        db.execSQL(alertsQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $ROUTES_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $POSITIONS_TABLE_NAME")
        onCreate(db)
    }

    fun addDataRoute(id: Int, name: String) {
        val values = ContentValues()
        values.put(ID_COL, id)
        values.put(NAME, name)

        val db = this.writableDatabase
        db.insert(ROUTES_TABLE_NAME, null, values)
        db.close()
    }

    fun addDataPosition(lat: Double, long: Double, routeId: Int) {
        val values = ContentValues()
        values.put(LONG_COl, long)
        values.put(LAT_COL, lat)
        values.put(ROUT_COL, routeId)

        val db = this.writableDatabase
        db.insert(POSITIONS_TABLE_NAME, null, values)
        db.close()
    }

    fun addDataAlert(name: String, description: String, image: String, positionId: Int) {
        val values = ContentValues()
        values.put(NAME, name)
        values.put(DESCRIPTION, description)
        values.put(IMAGE, image)
        values.put(POSIT_COL, positionId)

        val db = this.writableDatabase
        db.insert(ALERTS_TABLE_NAME, null, values)
        db.close()
    }

    fun getData(table: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table", null)
    }

    fun getGroupedData(table: String, column: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table GROUP BY $column", null)
    }

    fun getPositionsData(routeId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $POSITIONS_TABLE_NAME WHERE $ROUT_COL = $routeId ", null)
    }

    fun getDataWhere(table: String, where:String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table WHERE $where ", null)
    }

    fun getLastData(table: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $table ORDER BY $ID_COL DESC LIMIT 1", null)
    }

    companion object {
        private const val DATABASE_NAME = "MAPS"
        private const val DATABASE_VERSION = 1
        const val ROUTES_TABLE_NAME = "routes_table"
        const val POSITIONS_TABLE_NAME = "positions_table"
        const val ALERTS_TABLE_NAME = "alerts_table"
        const val ID_COL = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val LONG_COl = "longitude"
        const val LAT_COL = "latitude"
        const val IMAGE = "image"
        const val ROUT_COL = "routeId"
        const val POSIT_COL = "positionId"
    }
}

