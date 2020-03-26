package com.asd412id.ujianqapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val error = intent.getStringExtra("error")

        if (error==null){
            hint.text = Html.fromHtml("Sentuh <b>\"Scan QR Ujian\"</b> untuk membuka halaman ujian!")
        }else{
            val statusText: String
            if (error=="network"){
                statusText = "Perangkat tidak terhubung ke jaringan!"
            }else if (error=="url"){
                statusText = "Kode QR tidak dikenal!"
            }else{
                statusText = "Server ujian tidak ditemukan!"
            }
            hint.setTextColor(Color.RED)
            hint.text = statusText;
        }

        btnscan.setOnClickListener({
            startActivity(Intent(this,QrScanActivity::class.java))
            finishAffinity()
        })

    }
}
