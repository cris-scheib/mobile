package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream


class ActionActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    private lateinit var button_camera: Button
    private lateinit var button_camera_sec: Button
    private lateinit var button_save: Button
    private lateinit var text_name: EditText
    private var image: String? = null
    private var image_sec: String? = null
    private var is_image_sec: Boolean = false
    private lateinit var text_description: EditText
    private lateinit var db: DBHelper
    val CAMERA_REQUEST_CODE = 0
    private lateinit var photo: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action)

        val bundle = intent.extras
        val latitude = bundle!!.getString("latitude")
        val longitude = bundle!!.getString("longitude")

        db = DBHelper(this, null)

        button_save = findViewById(R.id.button_save)
        button_camera = findViewById(R.id.button_camera)
        button_camera_sec = findViewById(R.id.button_camera_sec)


        button_save.setOnClickListener {
            text_name = findViewById(R.id.text_name) as EditText
            text_description = findViewById(R.id.text_description) as EditText

            if(isEmpty(text_name) || isEmpty(text_description)){
                Toast.makeText(this@ActionActivity, "O nome e descrição não podems estar vazios", Toast.LENGTH_SHORT).show()
            }else{
                db.addDataComplaint(getString(text_name), getString(text_description), latitude!!.toDouble(), longitude!!.toDouble() ,image, image_sec);
                Toast.makeText(this@ActionActivity, "Denúncia criada com sucesso", Toast.LENGTH_SHORT).show()
                finish();
            }
        }

        button_camera.setOnClickListener {
            val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }else{
                is_image_sec = false
                getImage()

            }
            button_camera.setText("Atualizar imagem");
            button_camera.setBackgroundColor(Color.parseColor("#bdbdbd"))
        }

        button_camera_sec.setOnClickListener {
            val permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }else{
                is_image_sec = true
                getImage()
            }
            button_camera_sec.setText("Atualizar outra imagem");
            button_camera_sec.setBackgroundColor(Color.parseColor("#bdbdbd"))
            button_camera_sec.setTextColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    private fun getImage() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photo = data.extras!!.get("data") as Bitmap
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    if(is_image_sec){
                        image_sec = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    }else{
                        image = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Erro ao requisitar a camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getImage()
            }
        }
    }


    private fun isEmpty(etText: EditText): Boolean {
        return etText.text.toString().trim { it <= ' ' }.isEmpty()
    }

    private fun getString(etText: EditText): String {
        return etText.text.toString()
    }

    /*
    fun logData(){
        val cursor: Cursor = db.getData("alerts_table");
        var data = cursor.moveToFirst()
        while (data) {
            val name = cursor.getString(cursor.getColumnIndex("name"))
            Log.i("LOG:name", name)
            data = cursor.moveToNext()
        }
        cursor.close()
    }*/
}