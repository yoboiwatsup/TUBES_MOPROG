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

class TopUpNetflix : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up_netflix)

        val subscriptionName = intent.getStringExtra("SUBSCRIPTION_NAME")
        val subscriptionNameTextView: TextView = findViewById(R.id.productNameTextView)
        val userEmailEditText: EditText = findViewById(R.id.emailText)
        val spinnerPlans: Spinner = findViewById(R.id.spinnerPlans)
        val spinnerPaymentMethod: Spinner = findViewById(R.id.spinnerPaymentMethod)
        val btnConfirmTopUp: Button = findViewById(R.id.btnConfirmTopUp)

        subscriptionNameTextView.text = subscriptionName

        // Mengisi Spinner dengan pilihan plan Netflix
        val planOptions = arrayOf(
            "Mobile - Rp. 55.000",
            "Basic - Rp. 65.000",
            "Standard - Rp. 125.000",
            "Premium - Rp. 185.000"
        )
        val planAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, planOptions)
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlans.adapter = planAdapter

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
        val paymentMethodAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentMethodAdapter

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        btnConfirmTopUp.setOnClickListener {
            val userEmail = userEmailEditText.text.toString()
            val selectedPlan = spinnerPlans.selectedItem.toString()
            val paymentMethod = spinnerPaymentMethod.selectedItem.toString()

            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                when (paymentMethod) {
                    "QRIS" -> {
                        // Simpan detail transaksi ke Firebase
                        val transactionId = database.push().key
                        if (transactionId != null) {
                            val transaction = mapOf(
                                "subscriptionName" to subscriptionName,
                                "userEmail" to userEmail,
                                "planPackage" to selectedPlan,
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
                    }
                    "Virtual Account Mandiri" -> {
                        // Simpan detail transaksi ke Firebase
                        val transactionId = database.push().key
                        if (transactionId != null) {
                            val transaction = mapOf(
                                "subscriptionName" to subscriptionName,
                                "userEmail" to userEmail,
                                "planPackage" to selectedPlan,
                                "paymentMethod" to paymentMethod,
                                "status" to "pending"
                            )
                            database.child("transactions").child(transactionId).setValue(transaction).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(this, VirtualAccountMandiri::class.java)
                                    intent.putExtra("TRANSACTION_ID", transactionId)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Failed to create transaction", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Top up $selectedPlan using $paymentMethod successful!", Toast.LENGTH_SHORT).show()
                        // Tambahkan logika untuk memproses top-up di sini
                    }
                }
            }
        }
    }
}
