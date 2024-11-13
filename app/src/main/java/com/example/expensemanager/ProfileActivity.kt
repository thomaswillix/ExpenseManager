package com.example.expensemanager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File

class ProfileActivity : AppCompatActivity() {
    // Firebase
    private lateinit var user: FirebaseUser
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        user = FirebaseAuth.getInstance().currentUser!!
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())

        getProfileData()

        val btnProfile = findViewById<Button>(R.id.button)
        btnProfile.setOnClickListener {
            this.finish()
        }
        val info = findViewById<Button>(R.id.editBtn)
        info.setOnClickListener {
            startActivity(Intent(applicationContext, EditProfileActivity::class.java))
        }
    }
    fun getProfileData(){
        val profilePic = findViewById<ImageView>(R.id.imageView1)
        val userName = findViewById<TextView>(R.id.textView1)
        val emailTv = findViewById<TextView>(R.id.textView2)

        var name = ""
        val email = user.email
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email?.substring(0, email.indexOf("@")).toString()
        }
        userName.text = "$name"
        emailTv.text = "$email"

        // First we create a temp file
        val localFile : File = File.createTempFile("pfp", "jpg")
        // Then we get the File from the storage reference
        try {
            storageReference.getFile(localFile).addOnSuccessListener { // if it succeeds we set it to the imageView
                val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                profilePic.setImageBitmap(bitmap)
            }
        }catch (e : StorageException){}
    }

    override fun onResume() {
        super.onResume()
        getProfileData()
    }
}