package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ShopeePayDone : AppCompatActivity() {

    private lateinit var countdownTimer: TextView
    private lateinit var shopeePayNumberTextView: TextView
    private lateinit var productDetailsTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var btnCekStatusBayar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopee_pay_done)

        countdownTimer = findViewById(R.id.countdown_timer)
        shopeePayNumberTextView = findViewById(R.id.shopee_pay_number)
        productDetailsTextView = findViewById(R.id.product_details)
        btnCekStatusBayar = findViewById(R.id.btn_cek_status_bayar)
        database = FirebaseDatabase.getInstance().reference

        val transactionId = intent.getStringExtra("TRANSACTION_ID")

        if (transactionId == null) {
            Toast.makeText(this, "Error: No transaction ID provided.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Mengambil detail transaksi dari Firebase
        database.child("transactions").child(transactionId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userPhoneNumber = snapshot.child("userPhoneNumber").getValue(String::class.java) ?: "Unknown"
                val selectedPulsa = snapshot.child("selectedPulsa").getValue(String::class.java) ?: "Unknown"
                val paymentMethod = snapshot.child("paymentMethod").getValue(String::class.java) ?: "Unknown"
                val shopeePayNumber = snapshot.child("shopeePayNumber").getValue(String::class.java) ?: "Unknown"

                productDetailsTextView.text = "Detail Produk: Nomor HP: $userPhoneNumber, Pulsa: $selectedPulsa, Metode Pembayaran: $paymentMethod"
                shopeePayNumberTextView.text = "Nomor Shopee Pay: $shopeePayNumber"

                startCountdownTimer(30 * 1000)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShopeePayDone, "Failed to retrieve transaction details: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        btnCekStatusBayar.setOnClickListener {
            database.child("transactions").child(transactionId).child("status").setValue("completed").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TransaksiBerhasilMandiri::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memperbarui status transaksi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startCountdownTimer(timeInMillis: Long) {
        object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                countdownTimer.text = String.format("Batas waktu bayar: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                countdownTimer.text = "Waktu habis!"
                // Tambahkan logika tambahan jika waktu habis
            }
        }.start()
    }
}
