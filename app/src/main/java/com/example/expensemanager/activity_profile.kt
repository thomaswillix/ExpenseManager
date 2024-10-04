package com.example.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class activity_profile : AppCompatActivity() {
    // Firebase
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        user = FirebaseAuth.getInstance().currentUser!!
        val userName = findViewById<TextView>(R.id.user_profile)
        val emailTv = findViewById<TextView>(R.id.email_profile)

        var name = "Name"
        val email = user.email
        if(user.displayName != null) {
            name = user.displayName!!
        }
        userName.text = "$name"
        emailTv.text = "$email"
    }
}