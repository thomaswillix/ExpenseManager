package com.example.expensemanager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    // Binding
    private lateinit var binding: ActivityProfileBinding
    // Listener
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        // Configura el AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener {
            updateToolbarTitle(user)
        }
        // Registrar el listener en onStart()
        auth.addAuthStateListener(authStateListener)
        storageReference = FirebaseStorage.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())

        setSupportActionBar(binding.myToolbarPr)
        supportActionBar!!.show()
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.logoutButton.setOnClickListener {showDialog()}
        binding.editBtn.setOnClickListener {
            startActivity(Intent(applicationContext, PersonalDataActivity::class.java))
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_logout, null)
        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReturn)
            .setOnClickListener { dialog.dismiss() }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout)
            .setOnClickListener {
                dialog.dismiss()
                logoutUser()
            }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        finish()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }
    private fun updateToolbarTitle(user: FirebaseUser?) {
        if (user != null) {
            val name: String
            // Si el usuario tiene un nombre de display, lo usamos
            if (user.displayName != null) {
                name = user.displayName!!
            } else {
                // Si no tiene nombre de display, usamos el email
                val email = user.email
                name = email?.substring(0, email.indexOf("@")) ?: "User"
            }

            // Actualizamos el título del toolbar
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
    }
}