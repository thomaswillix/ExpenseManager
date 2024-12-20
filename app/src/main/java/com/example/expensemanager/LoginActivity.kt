package com.example.expensemanager

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.expensemanager.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityLoginBinding
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        progressDialog = AlertDialog.
        Builder(this).
        setView(ProgressBar(this)).
        setCancelable(false).
        setTitle("Logging in").
        setIcon(R.drawable.baseline_login_24)
            .create()

        login()
    }
    private fun login(){
        //Delay
        val delayTime: Long = 2000 // 2 segundos de tiempo de espera (puedes ajustarlo)

        binding.loginBtn.setOnClickListener {
            // Deshabilitar el botón al hacer clic para evitar múltiples clics
            binding.loginBtn.isEnabled = false
            progressDialog.show()
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (TextUtils.isEmpty(email)){
                progressDialog.dismiss()
                binding.loginEmail.setError("Email is required")
            }
            if (TextUtils.isEmpty(password)){
                progressDialog.dismiss()
                binding.loginPassword.setError("Password is required")
            }
            if (email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        progressDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // Crear un Handler para habilitar el botón después del tiempo de espera
            Handler(Looper.getMainLooper()).postDelayed({
                binding.loginBtn.isEnabled = true // Volver a habilitar el botón después del tiempo de espera
            }, delayTime)
        }
        binding.forgotPassword.setOnClickListener {
            binding.forgotPassword.isEnabled = false
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)
            builder.setView(view)
            val dialog = builder.create()
            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            if (dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
            // Crear un Handler para habilitar el botón después del tiempo de espera
            Handler(Looper.getMainLooper()).postDelayed({
                binding.forgotPassword.isEnabled = true // Volver a habilitar el botón después del tiempo de espera
            }, delayTime)
        }
        binding.signupRedirectTxt.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
    private fun compareEmail(email: EditText){
        if (email.text.toString().isEmpty()){
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        auth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        binding.loginEmail.text.clear()
        binding.loginPassword.text.clear()
    }
}