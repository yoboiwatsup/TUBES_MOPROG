package com.example.tubesmoprog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private var backPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val backPressRunnable = Runnable { backPressedOnce = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Product 1
        val productImage1: ImageView = findViewById(R.id.productImage1)
        val productName1: TextView = findViewById(R.id.productName1)
        val btnTopUp1: Button = findViewById(R.id.btnTopUp1)

        btnTopUp1.setOnClickListener {
            val intent = Intent(this, TopUpML::class.java)
            intent.putExtra("PRODUCT_NAME", productName1.text.toString())
            startActivity(intent)
        }

        // Product 6
        val productImage6: ImageView = findViewById(R.id.productImage6)
        val productName6: TextView = findViewById(R.id.productName6)
        val btnTopUp6: Button = findViewById(R.id.btnTopUp6)

        btnTopUp6.setOnClickListener {
            val intent = Intent(this, TopUpNetflix::class.java)
            intent.putExtra("PRODUCT_NAME", productName6.text.toString())
            startActivity(intent)
        }
        // Product 9
        val productImage9: ImageView = findViewById(R.id.productImage9)
        val productName9: TextView = findViewById(R.id.productName9)
        val btnTopUp9: Button = findViewById(R.id.btnTopUp9)

        btnTopUp9.setOnClickListener {
            val intent = Intent(this, TopUpTelkomsel::class.java)
            intent.putExtra("PRODUCT_NAME", productName9.text.toString())
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (backPressedOnce) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }

        this.backPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        handler.postDelayed(backPressRunnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(backPressRunnable)
    }
}
