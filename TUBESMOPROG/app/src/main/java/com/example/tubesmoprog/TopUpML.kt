package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TopUpML : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up_ml)

        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productNameTextView: TextView = findViewById(R.id.productNameTextView)
        val userIdEditText: EditText = findViewById(R.id.userIdEditText)
        val serverIdEditText: EditText = findViewById(R.id.serverIdEditText)
        val spinnerDiamonds: Spinner = findViewById(R.id.spinnerDiamonds)
        val spinnerPaymentMethod: Spinner = findViewById(R.id.spinnerPaymentMethod)
        val btnConfirmTopUp: Button = findViewById(R.id.btnConfirmTopUp)

        productNameTextView.text = productName

        // Mengisi Spinner dengan pilihan diamond
        val diamondOptions = arrayOf(
            "12 Diamonds - Rp. 3.500",
            "50 Diamonds - Rp. 15.000",
            "150 Diamonds - Rp. 45.000",
            "250 Diamonds - Rp. 75.000",
            "500 Diamonds - Rp. 149.000",
            "1000 Diamonds - Rp. 299.000",
            "1500 Diamonds - Rp. 439.000",
            "2500 Diamonds - Rp. 739.000",
            "5000 Diamonds - Rp. 1.499.000"

        )
        val diamondAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diamondOptions)
        diamondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiamonds.adapter = diamondAdapter

        // Mengisi Spinner dengan pilihan metode pembayaran
        val paymentMethods = arrayOf("QRIS", "Virtual Account Mandiri", "Virtual Account BNI", "Virtual Account BCA", "Virtual Account BRI", "Shopee Pay", "OVO", "DANA")
        val paymentMethodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentMethodAdapter

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        btnConfirmTopUp.setOnClickListener {
            val userId = userIdEditText.text.toString()
            val serverId = serverIdEditText.text.toString()
            val selectedDiamond = spinnerDiamonds.selectedItem.toString()
            val paymentMethod = spinnerPaymentMethod.selectedItem.toString()

            if (userId.isEmpty() || serverId.isEmpty()) {
                Toast.makeText(this, "Please enter User ID and Server ID", Toast.LENGTH_SHORT).show()
            } else {
                if (paymentMethod == "QRIS") {
                    // Simpan detail transaksi ke Firebase
                    val transactionId = database.push().key
                    if (transactionId != null) {
                        val transaction = mapOf(
                            "productName" to productName,
                            "userId" to userId,
                            "serverId" to serverId,
                            "diamondPackage" to selectedDiamond,
                            "paymentMethod" to paymentMethod,
                            "status" to "pending"
                        )
                        database.child("transactions").child(transactionId).setValue(transaction).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, QRIS::class.java)
                                intent.putExtra("QR_CODE_DATA", transactionId)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Failed to create transaction", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Top up $selectedDiamond using $paymentMethod successful!", Toast.LENGTH_SHORT).show()
                    // Tambahkan logika untuk memproses top-up di sini
                }
            }
        }
    }
}