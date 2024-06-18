package com.example.tubesmoprog

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class QRIS : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var qrCodeImageView: ImageView
    private lateinit var scanQRCodeButton: Button
    private lateinit var timerTextView: TextView
    private var transactionId: String? = null
    private var countDownTimer: CountDownTimer? = null
    private val paymentTimeout: Long = 30000 // 30 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qris)

        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        scanQRCodeButton = findViewById(R.id.scanQRCodeButton)
        timerTextView = findViewById(R.id.timerTextView)
        transactionId = intent.getStringExtra("QR_CODE_DATA")

        if (transactionId != null) {
            generateQRCode(transactionId!!)
            startPaymentTimer()
        } else {
            Toast.makeText(this, "Transaction ID is missing", Toast.LENGTH_SHORT).show()
        }

        scanQRCodeButton.setOnClickListener {
            transactionId?.let {
                completeTransaction(it)
            }
        }
    }

    private fun generateQRCode(data: String) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }

            qrCodeImageView.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun completeTransaction(transactionId: String) {
        countDownTimer?.cancel()
        database = FirebaseDatabase.getInstance().reference
        database.child("transactions").child(transactionId).child("status").setValue("completed").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Transaction completed successfully", Toast.LENGTH_SHORT).show()
                // Mulai SuccessActivity
                val intent = Intent(this, TransaksiBerhasil::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to complete transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startPaymentTimer() {
        countDownTimer = object : CountDownTimer(paymentTimeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                Toast.makeText(this@QRIS, "Payment time expired", Toast.LENGTH_SHORT).show()
                // Kembali ke TopUpML
                val intent = Intent(this@QRIS, TopUpML::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}
