package com.example.expensemanager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.expensemanager.databinding.ActivityProfileBinding
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

    // Binding
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance().currentUser!!

        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())

        setSupportActionBar(binding.myToolbarPr)
        supportActionBar!!.show()
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        getProfileData()

        binding.settingsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ConfigurationActivity::class.java))
        }
        binding.editBtn.setOnClickListener {
            startActivity(Intent(applicationContext, PersonalDataActivity::class.java))
        }
    }
    private fun getProfileData(){
        val name:String
        val email = user.email
        name = if (user.displayName != null) {
            user.displayName!!
        } else{
            email?.substring(0, email.indexOf("@")).toString()
        }
        binding.textView1.text = name
        // First we create a temp file
        val localFile : File = File.createTempFile("pfp", "jpg")
        // Then we get the File from the storage reference
        try {
            storageReference.getFile(localFile).addOnSuccessListener { // if it succeeds we set it to the imageView
                val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.imageView1.setImageBitmap(bitmap)
            }
        }catch (_: StorageException){
        }
    }

    override fun onResume() {
        super.onResume()
        getProfileData()
    }
}