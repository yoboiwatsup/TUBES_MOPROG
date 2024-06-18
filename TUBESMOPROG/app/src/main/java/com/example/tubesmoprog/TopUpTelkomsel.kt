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

class TopUpTelkomsel : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up_telkomsel)

        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productNameTextView: TextView = findViewById(R.id.productNameTextView)
        val userPhoneNumberEditText: EditText = findViewById(R.id.PhoneNumberText)
        val spinnerPulsa: Spinner = findViewById(R.id.spinnerPulsa)
        val spinnerPaymentMethod: Spinner = findViewById(R.id.spinnerPaymentMethod)
        val btnConfirmTopUp: Button = findViewById(R.id.btnConfirmTopUp)

        productNameTextView.text = productName

        // Mengisi Spinner dengan pilihan plan Pulsa
        val pulsaOptions = arrayOf(
            "Pulsa 15.000 - Rp. 17.000",
            "Pulsa 25.000 - Rp. 27.000",
            "Pulsa 40.000 - Rp. 42.000",
            "Pulsa 50.000 - Rp. 52.000",
            "Pulsa 75.000 - Rp. 77.000",
            "Pulsa 100.000 - Rp. 102.000",
            "Pulsa 150.000 - Rp. 152.000",
            "Pulsa 200.000 - Rp. 200.000",
            "Pulsa 300.000 - Rp. 300.000",
            "Kuota 10 GB/7 hari - Rp. 22.000",
            "Kuota 3 GB/30 hari - Rp. 25.000",
            "Kuota 13 GB/30 hari - Rp. 75.000",
            "Kuota 28 GB/30 hari - Rp. 89.000",
            "Kuota 40 GB/30 hari - Rp. 126.000",
            "Kuota 110 GB/30 hari - Rp. 320.000"
        )
        val planAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pulsaOptions)
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPulsa.adapter = planAdapter

        // Mengisi Spinner dengan pilihan metode pembayaran yang sama
        val paymentMethods = arrayOf(
            "QRIS",
            "Virtual Account Mandiri",
            "Virtual Account BNI",
            "Virtual Account BCA",
            "Virtual Account BRI",
            "Shopee Pay",
            "OVO",
            "DANA",
            "Credit Card"
        )
        val paymentMethodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentMethodAdapter

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        btnConfirmTopUp.setOnClickListener {
            val userPhoneNumber = userPhoneNumberEditText.text.toString()
            val selectedPulsa = spinnerPulsa.selectedItem.toString()
            val paymentMethod = spinnerPaymentMethod.selectedItem.toString()

            if (userPhoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan detail transaksi ke Firebase
                val transactionId = database.child("transactions").push().key
                if (transactionId != null) {
                    val transaction = mapOf(
                        "productName" to productName,
                        "userPhoneNumber" to userPhoneNumber,
                        "selectedPulsa" to selectedPulsa,
                        "paymentMethod" to paymentMethod,
                        "status" to "pending"
                    )
                    database.child("transactions").child(transactionId).setValue(transaction).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Panggil method untuk lanjut ke ShopeePay
                            proceedToShopeePay(transactionId)
                        } else {
                            Toast.makeText(this, "Failed to create transaction", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun proceedToShopeePay(transactionId: String) {
        val intent = Intent(this, ShopeePay::class.java)
        intent.putExtra("TRANSACTION_ID", transactionId)
        startActivity(intent)
    }

}

