package com.example.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.expensemanager.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.userProfileChangeRequest

class SignupActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    //Binding
    private lateinit var binding: ActivitySignupBinding
    private lateinit var progressDialog : AlertDialog
    private lateinit var email : String
    private lateinit var password: String
    private lateinit var confirmPassword: String
    private lateinit var username : String
    val noWhiteSpace = "\\A[.\\- \\w]{4,20}\\z"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        progressDialog = AlertDialog.
        Builder(this).
        setView(ProgressBar(this)).
        setCancelable(false).
        setTitle("Registering user").
        setIcon(R.drawable.edit)
            .create()

        registration()
    }
    private fun registration(){
        val delayTime: Long = 2000 // 2 segundos de tiempo de espera (puedes ajustarlo)

        binding.signupBtn.setOnClickListener{
            binding.signupBtn.isEnabled = false
            progressDialog.show()
            email = binding.signupEmail.text.toString()
            password = binding.signupPassword.text.toString()
            confirmPassword = binding.signUpConfirm.text.toString()
            username = binding.signupUsername.text.toString()

            if (validateSignIn()){
                if (password == confirmPassword){
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            val profileUpdates = userProfileChangeRequest {
                                displayName = username
                            }
                            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
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
            // Crear un Handler para habilitar el botón después del tiempo de espera
            Handler(Looper.getMainLooper()).postDelayed({
                binding.signupBtn.isEnabled = true // Volver a habilitar el botón después del tiempo de espera
            }, delayTime)
        }
        binding.loginRedirectTxt.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(loginIntent)
            this.finish()
        }
    }
    private fun validateSignIn(): Boolean{
        var isValid = true
        if (TextUtils.isEmpty(username)) {
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.setError("Username confirmation is required")
        } else if (username.length < 4){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.setError("Username too short")
        } else if (username.length > 15){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.setError("Username too long")
        } else if(!username.matches(noWhiteSpace.toRegex())){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.setError("No whitespaces allowed")
        }
        if (TextUtils.isEmpty(email)){
            isValid = false
            progressDialog.dismiss()
            binding.signupEmail.setError("Email is required")
        }
        if (TextUtils.isEmpty(password)){
            isValid = false
            progressDialog.dismiss()
            binding.signupPassword.setError("Password is required")
        }
        if (TextUtils.isEmpty(confirmPassword)){
            isValid = false
            progressDialog.dismiss()
            binding.signUpConfirm.setError("Password confirmation is required")
        }
        return isValid
    }
}