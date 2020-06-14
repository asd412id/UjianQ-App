package com.asd412id.ujianqapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val error = intent.getStringExtra("error")
        val url = getUrl()
        if (url=="")
            btnscan.setText("Masukkan Alamat Server")

        if (error==null){
            if (!url.equals(""))
                hint.text = Html.fromHtml("Tekan <b>\"Mulai Ujian\"</b> untuk membuka halaman ujian " +
                        "atau tekan dan tahan untuk mengubah alamat server!")
            else
                hint.text = Html.fromHtml("Tekan <b>\"Masukkan Alamat Server\"</b> untuk mengubah alamat server!")
        }else{
            val statusText: String
            if (error=="network"){
                statusText = "Server ujian tidak ditemukan! Tekan dan tahan tombol di bawah untuk mengubah alamat server."
            }else{
                statusText = "Perangkat tidak terhubung ke jaringan! Tekan dan tahan tombol di bawah untuk mengubah alamat server."
            }
            hint.setTextColor(Color.RED)
            hint.text = statusText
        }

        btnscan.setOnClickListener({
            val url = getUrl()
            if (url!=""){
                val intent = Intent(this,UjianWebViewActivity::class.java)
                intent.putExtra("url",url)
                startActivity(intent)
                finishAffinity()
            }else{
                setServerAddress()
            }
        })

        btnscan.setOnLongClickListener(View.OnLongClickListener {
            setServerAddress()
            true
        })

    }

    fun getConfig(): SharedPreferences {
        return getSharedPreferences("configs", 0)
    }

    private fun getUrl(): String? {
        return getConfig().getString("url","")
    }

    private fun setServerAddress() {
        val view = layoutInflater.inflate(R.layout.address_dialog,null)
        val builder = AlertDialog.Builder(this)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val addr = view.findViewById<EditText>(R.id.addr)
        addr.setText(getUrl())
        builder.apply {
            setCancelable(false)
            setTitle("Alamat Server")
            setView(view)
        }
        val dialog = builder.create()
        dialog.show()
        btnSimpan.setOnClickListener(View.OnClickListener {
            if (URLUtil.isValidUrl(addr.text.toString()) && !addr.text.equals("")){
                with(getConfig().edit()){
                    putString("url",addr.text.toString())
                    commit()
                }
                if (!addr.text.equals("")) {
                    hint.text = Html.fromHtml(
                        "Tekan <b>\"Mulai Ujian\"</b> untuk membuka halaman ujian " +
                                "atau tekan dan tahan untuk mengubah alamat server!"
                    )
                    btnscan.setText("Mulai Ujian")
                }else {
                    hint.text = Html.fromHtml(
                        "Tekan <b>\"Masukkan Alamat Server\"</b> untuk membuka halaman ujian " +
                                "atau tekan dan tahan untuk mengubah alamat server!"
                    )
                    btnscan.setText("Masukkan Alamat Server")
                }
                dialog.dismiss()
                Toast.makeText(applicationContext,"Alamat Server berhasil diubah!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"Alamat Server tidak sesuai!",Toast.LENGTH_SHORT).show()
            }
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }
}
