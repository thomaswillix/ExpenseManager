package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.expensemanager.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    //Binding
    private lateinit var binding: ActivitySignupBinding
    private lateinit var progressDialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        progressDialog = AlertDialog.
        Builder(this).
        setView(ProgressBar(this)).
        setCancelable(false).
        setTitle("Please wait").
        create()

        registration()
    }
    private fun registration(){
        binding.signupBtn.setOnClickListener{
            progressDialog.show()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signUpConfirm.text.toString()
            if (TextUtils.isEmpty(email)){
                progressDialog.dismiss()
                binding.signupEmail.setError("Email is required")
            }
            if (TextUtils.isEmpty(password)){
                progressDialog.dismiss()
                binding.signupPassword.setError("Password is required")
            }
            if (TextUtils.isEmpty(confirmPassword)){
                progressDialog.dismiss()
                binding.signUpConfirm.setError("Password confirmation is required")
            }
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            progressDialog.dismiss()
                            Toast.makeText(this, "Registration was completed successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.loginRedirectTxt.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(loginIntent)
            this.finish()
        }
    }
}