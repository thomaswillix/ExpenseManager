package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {
    // Firebase
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        user = FirebaseAuth.getInstance().currentUser!!
        val userName = findViewById<TextView>(R.id.textView1)
        val emailTv = findViewById<TextView>(R.id.textView2)
        val profilePic = findViewById<ImageView>(R.id.imageView1)


        var name = ""
        val email = user.email
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email?.substring(0, email.indexOf("@")).toString()
        }
        userName.text = "$name"
        emailTv.text = "$email"
        // TODO: HACER QUE LA FOTO QUE SE MUESTRE SEA DE LA BASE DE DATOS Y SI NO EXISTE PONER LA DE POR DEFECTO
        val btnProfile = findViewById<Button>(R.id.button)
        btnProfile.setOnClickListener {
            this.finish()
        }
        val info = findViewById<Button>(R.id.editBtn)
        info.setOnClickListener {
            startActivity(Intent(applicationContext, EditProfileActivity::class.java))
        }

    }
}