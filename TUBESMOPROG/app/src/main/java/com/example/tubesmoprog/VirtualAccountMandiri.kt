package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlin.random.Random

class VirtualAccountMandiri : AppCompatActivity() {

    private lateinit var countdownTimer: TextView
    private lateinit var virtualAccountNumberTextView: TextView
    private lateinit var productDetailsTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var btnCekStatusBayar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_account_mandiri)

        countdownTimer = findViewById(R.id.countdown_timer)
        virtualAccountNumberTextView = findViewById(R.id.virtual_account_number)
        productDetailsTextView = findViewById(R.id.product_details)
        btnCekStatusBayar = findViewById(R.id.btn_cek_status_bayar)
        database = FirebaseDatabase.getInstance().reference

        // Mengambil data dari Intent
        val transactionId = intent.getStringExtra("TRANSACTION_ID")

        if (transactionId != null) {
            // Ambil detail transaksi dari Firebase
            database.child("transactions").child(transactionId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val subscriptionName = snapshot.child("subscriptionName").getValue(String::class.java) ?: ""
                    val planPackage = snapshot.child("planPackage").getValue(String::class.java) ?: ""
                    val paymentMethod = snapshot.child("paymentMethod").getValue(String::class.java) ?: ""

                    // Menampilkan detail produk di TextView
                    productDetailsTextView.text = "Detail Produk: ${nonNullValue(subscriptionName)}, Paket: ${nonNullValue(planPackage)}, Metode Pembayaran: ${nonNullValue(paymentMethod)}"

                    // Menghasilkan nomor virtual account acak
                    val virtualAccountNumber = generateRandomVirtualAccountNumber()
                    virtualAccountNumberTextView.text = "Nomor Virtual Account: $virtualAccountNumber"

                    // Simpan nomor virtual account ke Firebase
                    database.child("transactions").child(transactionId).child("virtualAccountNumber").setValue(virtualAccountNumber)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Tangani kesalahan
                }
            })
        }

        // Atur hitungan mundur selama 30 detik
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTimer.text = "Batas waktu bayar ${millisUntilFinished / 1000} detik"
            }

            override fun onFinish() {
                countdownTimer.text = "Waktu pembayaran habis!"
                // Tambahkan jeda 5 detik sebelum kembali ke TopUpNetflix
                object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Tidak melakukan apa-apa setiap detik
                    }

                    override fun onFinish() {
                        // Kembali ke TopUpNetflix setelah jeda 5 detik
                        val intent = Intent(this@VirtualAccountMandiri, TopUpNetflix::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.start()
            }
        }.start()

        btnCekStatusBayar.setOnClickListener {
            if (transactionId != null) {
                database.child("transactions").child(transactionId).child("status").setValue("completed")
                Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TransaksiBerhasilMandiri::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun nonNullValue(value: String?): String {
        return value ?: "Tidak tersedia"
    }

    private fun generateRandomVirtualAccountNumber(): String {
        val chars = "0123456789"
        return (1..16)
            .map { chars.random() }
            .joinToString("")
    }
}
