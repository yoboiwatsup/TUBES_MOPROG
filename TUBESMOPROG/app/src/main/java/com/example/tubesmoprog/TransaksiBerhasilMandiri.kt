package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TransaksiBerhasilMandiri : AppCompatActivity() {

    private lateinit var btnBackToTopUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi_berhasil_mandiri)

        btnBackToTopUp = findViewById(R.id.btn_back_to_topup)

        btnBackToTopUp.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
