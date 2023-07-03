package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import kotlinx.android.synthetic.main.activity_action.*

class DetailActivity : AppCompatActivity() {

    private lateinit var complaintName: TextView
    private lateinit var complaintDescription: TextView
    private lateinit var complaintImage: ImageView
    private lateinit var complaintImageSec: ImageView
    private lateinit var buttonBack: Button
    private lateinit var buttonDelete: Button
    private var complaitId: Int = 0
    private var external_id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val db = DBHelper(this, null)

        complaintName = findViewById(R.id.compaint_name)
        complaintDescription = findViewById(R.id.compaint_description)
        complaintImage = findViewById(R.id.compaint_image)
        complaintImageSec = findViewById(R.id.compaint_image_sec)

        buttonBack = findViewById(R.id.button_new_back)
        buttonBack.setOnClickListener {
            finish();
        }

        val bundle = intent.extras
        if (bundle != null) {
            val latitude = bundle.getString("latitude")
            val longitude = bundle.getString("longitude")

            val cursorComplaint: Cursor =
                db.getDataByPosition(latitude!!.toDouble(), longitude!!.toDouble());
            var complaint = cursorComplaint.moveToFirst()
            while (complaint) {
                complaitId = cursorComplaint.getInt(cursorComplaint.getColumnIndex("id"))
                external_id =
                    cursorComplaint.getIntOrNull(cursorComplaint.getColumnIndex("external_id"))
                val lat = cursorComplaint.getDouble(cursorComplaint.getColumnIndex("longitude"))
                val long = cursorComplaint.getDouble(cursorComplaint.getColumnIndex("latitude"))
                val name = cursorComplaint.getString(cursorComplaint.getColumnIndex("name"))
                val description =
                    cursorComplaint.getString(cursorComplaint.getColumnIndex("description"))
                val image = cursorComplaint.getStringOrNull(cursorComplaint.getColumnIndex("image"))
                val imageSec =
                    cursorComplaint.getStringOrNull(cursorComplaint.getColumnIndex("image_sec"))
                complaintName.setText(name);
                complaintDescription.setText(description);

                Log.i("LOG:response", "That didn't work! $image")
                try {
                    if (image != null) complaintImage.setImageBitmap(decodeBase64(image));
                } catch (e: Exception) {
                    Log.i("LOG:response", "Image invalid! $e")
                }

                try {
                    if (imageSec != null) complaintImageSec.setImageBitmap(decodeBase64(imageSec));
                } catch (e: Exception) {
                    Log.i("LOG:response", "Image 2 invalid! $e")
                }

                complaint = cursorComplaint.moveToNext()
            }
            if (external_id == null) {
                buttonDelete = findViewById(R.id.button_delete)
                buttonDelete.setOnClickListener {
                    db.delete(complaitId)
                    finish();
                }
            } else {
                markButtonDisable(findViewById(R.id.button_delete));
            }
        }
    }

    private fun decodeBase64(image: String?): Bitmap? {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(Color.parseColor("#bdbdbd"))
        button.setTextColor(Color.parseColor("#FFFFFFFF"))
    }


}