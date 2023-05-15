package com.example.webserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnGet: Button
    private lateinit var btnPost: Button
    private lateinit var txtReq: TextView
    private lateinit var txtResp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGet = findViewById(R.id.btn_get)
        btnPost = findViewById(R.id.btn_post)
        txtReq = findViewById(R.id.text_req)
        txtResp = findViewById(R.id.text_resp)

        btnGet.setOnClickListener(this)
        btnPost.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_post -> sendPost()
            R.id.btn_get -> sendGet()
        }
    }

    fun sendGet(){
        val queue = Volley.newRequestQueue(this)
        val url = "http://177.44.248.10:8080/Teste/index.jsp"

        val stringReq = StringRequest(Request.Method.GET, url,
            { response ->
                txtResp.text = "Response is: ${response}"
            },
            {  txtResp.text = "That didn't work!" })
        queue.add(stringReq)
    }

    fun sendPost(){
        val queue = Volley.newRequestQueue(this)
        val url = "https://private-4c0e8-simplestapi3.apiary-mock.com/message"

        val requestBody = "id=1" + "&msg=test_msg"
        val stringReq : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    txtResp.text = "Response is: ${response}"
                },
                Response.ErrorListener { error ->
                    txtResp.text = "That didn't work!"
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }
}