package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val arrayAdapter: ArrayAdapter<*>
        var routes = arrayOf<String>()
        var routeLatLong = ""

        val db = DBHelper(this, null)

        val cursorRoutes: Cursor = db.getData("routes_table");
        var route = cursorRoutes.moveToFirst()
        while (route) {
            val routeName = cursorRoutes.getString(cursorRoutes.getColumnIndex("name"))
            /*
            val cursorPath: Cursor = db.getPositionsData(routeId);
            var path = cursorPath.moveToFirst()
            while (path) {
                val lat = cursorPath.getDouble(cursorPath.getColumnIndex("latitude"))
                val lon = cursorPath.getDouble(cursorPath.getColumnIndex("longitude"))
                routeLatLong += "[$lat, $lon],"
                path = cursorPath.moveToNext()
            }
            cursorPath.close()
            routeLatLong = ""
            */
            routes += routeName

            route = cursorRoutes.moveToNext()
        }


        cursorRoutes.close()

        val mListView = findViewById<ListView>(R.id.list_item)
        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, routes
        )
        mListView.adapter = arrayAdapter

        /*mListView.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter.getPosition(position) // The item that was clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }*/
    }
}