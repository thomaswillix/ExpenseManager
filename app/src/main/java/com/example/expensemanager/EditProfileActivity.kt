package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EditProfileActivity : AppCompatActivity() {
    // Firebase
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        getUserData()

    }
    fun getUserData(){
        user = FirebaseAuth.getInstance().currentUser!!
        val mName = findViewById<EditText>(R.id.name_profile)
        val mEmail = findViewById<EditText>(R.id.email_profile)
        val profilePic = findViewById<ImageView>(R.id.imageView2)

        var name = ""
        val email = user.email
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email?.substring(0, email.indexOf("@")).toString()
        }
        mName.setText(name, TextView.BufferType.EDITABLE)
        mEmail.setText(email, TextView.BufferType.EDITABLE)

        // TODO: HACER QUE LA FOTO QUE SE MUESTRE SEA DE LA BASE DE DATOS Y SI NO EXISTE PONER LA DE POR DEFECTO
        val btnConfirm = findViewById<Button>(R.id.confirm_info_btn)
        manageConfirmation(btnConfirm, user)

    }
    fun manageConfirmation(btn: Button, user:FirebaseUser){

    }
}