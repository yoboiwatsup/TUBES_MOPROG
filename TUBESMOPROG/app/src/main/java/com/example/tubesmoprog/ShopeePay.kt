package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ShopeePay : AppCompatActivity() {

    private lateinit var editTextShopeePayNumber: EditText
    private lateinit var buttonPay: Button
    private lateinit var textViewUserPhoneNumber: TextView
    private lateinit var textViewSelectedPulsa: TextView
    private lateinit var textViewPaymentMethod: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopee_pay)

        editTextShopeePayNumber = findViewById(R.id.editTextShopeePayNumber)
        buttonPay = findViewById(R.id.buttonPay)
        textViewUserPhoneNumber = findViewById(R.id.textViewUserPhoneNumber)
        textViewSelectedPulsa = findViewById(R.id.textViewSelectedPulsa)
        textViewPaymentMethod = findViewById(R.id.textViewPaymentMethod)
        database = FirebaseDatabase.getInstance().reference

        // Mendapatkan transactionId dari intent
        val transactionId = intent.getStringExtra("TRANSACTION_ID")

        if (transactionId != null) {
            // Mengambil data transaksi dari Firebase
            database.child("transactions").child(transactionId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userPhoneNumber = dataSnapshot.child("userPhoneNumber").getValue(String::class.java) ?: "Not Available"
                    val selectedPulsa = dataSnapshot.child("selectedPulsa").getValue(String::class.java) ?: "Not Available"
                    val paymentMethod = dataSnapshot.child("paymentMethod").getValue(String::class.java) ?: "Not Available"

                    textViewUserPhoneNumber.text = "User Phone Number: $userPhoneNumber"
                    textViewSelectedPulsa.text = "Selected Pulsa: $selectedPulsa"
                    textViewPaymentMethod.text = "Payment Method: $paymentMethod"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ShopeePay, "Failed to retrieve transaction data", Toast.LENGTH_SHORT).show()
                }
            })
        }

        buttonPay.setOnClickListener {
            val shopeePayNumber = editTextShopeePayNumber.text.toString().trim()
            if (TextUtils.isEmpty(shopeePayNumber)) {
                Toast.makeText(this, "Please enter Shopee Pay Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update detail transaksi dengan nomor Shopee Pay
            val updates = hashMapOf<String, Any>(
                "shopeePayNumber" to shopeePayNumber,
                "status" to "completed"  // asumsikan transaksi selesai setelah tombol bayar ditekan
            )

            transactionId?.let {
                database.child("transactions").child(it).updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        proceedToShopeePayDone(it)
                    } else {
                        Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun proceedToShopeePayDone(transactionId: String) {
        val intent = Intent(this, ShopeePayDone::class.java)
        intent.putExtra("TRANSACTION_ID", transactionId)
        startActivity(intent)
        finish()
    }
}
