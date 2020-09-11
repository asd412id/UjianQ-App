package com.asd412id.ujianqapp

import android.annotation.SuppressLint
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
        val username = getUsername()
        val password = getPassword()
        if (username == "" || password == "" || url == "")
            btnscan.setText("Masukkan Data Ujian")

        if (error==null){
            if (!username.equals("") && !password.equals("") && !url.equals(""))
                hint.text = Html.fromHtml("Tekan <b>\"Mulai Ujian\"</b> untuk membuka halaman ujian " +
                        "atau tekan dan tahan untuk mengubah data ujian!")
            else
                hint.text = Html.fromHtml("Tekan <b>\"Masukkan Data Ujian\"</b> untuk mengubah data ujian!")
        }else{
            val statusText: String
            if (error=="network"){
                statusText = "Server ujian tidak ditemukan! Tekan dan tahan tombol di bawah untuk mengubah data ujian."
            }else{
                statusText = "Perangkat tidak terhubung ke jaringan! Tekan dan tahan tombol di bawah untuk mengubah data ujian."
            }
            hint.setTextColor(Color.RED)
            hint.text = statusText
        }

        btnscan.setOnClickListener {
            val url = getUrl()
            val username = getUsername()
            val password = getPassword()
            if (username != "" && password != "" && url != "") {
                val intent = Intent(this, UjianWebViewActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                startActivity(intent)
                finishAffinity()
            } else {
                setServerAddress()
            }
        }

        btnscan.setOnLongClickListener(View.OnLongClickListener {
            setServerAddress()
            true
        })

    }

    private fun getConfig(): SharedPreferences {
        return getSharedPreferences("configs", 0)
    }

    private fun getUrl(): String? {
        return getConfig().getString("url","https://ujian.smpn39sinjai.sch.id")
    }

    private fun getUsername(): String? {
        return getConfig().getString("username","")
    }

    private fun getPassword(): String? {
        return getConfig().getString("password","")
    }

    @SuppressLint("InflateParams")
    private fun setServerAddress() {
        val view = layoutInflater.inflate(R.layout.address_dialog,null)
        val builder = AlertDialog.Builder(this)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val addr = view.findViewById<EditText>(R.id.addr)
        val editusername = view.findViewById<EditText>(R.id.edit_username)
        val editpassword = view.findViewById<EditText>(R.id.edit_password)
        editusername.requestFocus()
        addr.setText(getUrl())
        editusername.setText(getUsername())
        editpassword.setText(getPassword())
        builder.apply {
            setCancelable(false)
            setTitle("Data Ujian")
            setView(view)
        }
        val dialog = builder.create()
        dialog.show()
        btnSimpan.setOnClickListener(View.OnClickListener {
            if (URLUtil.isValidUrl(addr.text.toString()) && editusername?.text.toString().trim().isNotEmpty() && editpassword?.text.toString().isNotEmpty() && addr?.text.toString().trim().isNotEmpty()){
                with(getConfig().edit()){
                    putString("url",addr?.text.toString().trim())
                    putString("username",editusername?.text.toString().trim())
                    putString("password",editpassword?.text.toString().trim())
                    commit()
                }
                if (editusername?.text.toString().trim() != "" && editpassword?.text.toString().trim() != "" && addr?.text.toString().trim() != "") {
                    hint.text = Html.fromHtml(
                        "Tekan <b>\"Mulai Ujian\"</b> untuk membuka halaman ujian " +
                                "atau tekan dan tahan untuk mengubah data ujian!"
                    )
                    btnscan.setText("Mulai Ujian")
                }else {
                    hint.text = Html.fromHtml(
                        "Tekan <b>\"Masukkan Alamat Server\"</b> untuk membuka halaman ujian " +
                                "atau tekan dan tahan untuk mengubah data ujian!"
                    )
                    btnscan.setText("Masukkan Data Ujian")
                }
                dialog.dismiss()
                Toast.makeText(applicationContext,"Data Ujian berhasil diubah!",Toast.LENGTH_SHORT).show()
            }else{
                if (editusername?.text.toString().trim() == ""){
                    Toast.makeText(applicationContext,"Username tidak boleh kosong!",Toast.LENGTH_SHORT).show()
                }else if (editpassword?.text.toString().trim() == ""){
                    Toast.makeText(applicationContext,"Password tidak boleh kosong!",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Alamat server tidak boleh kosong!",Toast.LENGTH_SHORT).show()
                }
            }
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }
}
