package com.example.expensemanager

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.expensemanager.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
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
    private val noWhiteSpace = "\\A[.\\- \\w]{4,20}\\z"
    private val validationResults = mutableListOf<String>()
    private var isValid = true

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
        binding.validationList.visibility = View.GONE

        binding.signupPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val password = charSequence.toString()

                validatePassword(password)

                // Si alguna validación contiene "Fallido"
                if (validationResults.any { it.contains("Fallido") }) {
                    binding.validationList.visibility = View.VISIBLE
                    isValid = false
                } else {
                    binding.validationList.visibility = View.GONE
                }
                // Actualizar los TextViews de validación con los resultados
                updateValidationUI(validationResults)
            }
            override fun afterTextChanged(editable: Editable?) {}
        })

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
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // Crear un Handler para habilitar el botón después del tiempo de espera
            Handler(Looper.getMainLooper()).postDelayed({
                binding.signupBtn.isEnabled = true // Volver a habilitar el botón después del tiempo de espera
            }, delayTime)
        }
        binding.loginRedirectTxt.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(loginIntent)
            this.finish()
        }
    }

    private fun validateSignIn(): Boolean{
        isValid = true
        if (TextUtils.isEmpty(username)) {
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.error = "Username confirmation is required"
        } else if (username.length < 4){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.error = "Username too short"
        } else if (username.length > 15){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.error = "Username too long"
        } else if(!username.matches(noWhiteSpace.toRegex())){
            isValid = false
            progressDialog.dismiss()
            binding.signupUsername.error = "No whitespaces allowed"
        }
        if (TextUtils.isEmpty(email)){
            isValid = false
            progressDialog.dismiss()
            binding.signupEmail.error = "Email is required"
        }
        if (TextUtils.isEmpty(password)){
            isValid = false
            progressDialog.dismiss()
            binding.signupPassword.error = "Password confirmation is required"
        } else if (validationResults.any { it.contains("Fallido") }) {
            isValid =false
            progressDialog.dismiss()
            binding.signupPassword.error = validationResults.find { it.contains("Fallido") }
        }
        if (TextUtils.isEmpty(confirmPassword)){
            isValid = false
            progressDialog.dismiss()
            binding.signUpConfirm.error = "Password confirmation is required"
        } else if (confirmPassword != password){
            isValid = false
            progressDialog.dismiss()
            binding.signUpConfirm.error = "Password does not match"
        }
        return isValid
    }

    fun validatePassword(password: String): List<String> {
        validationResults.clear()

        // Validar longitud mínima
        if (password.length >= 8) {
            validationResults.add("Longitud mínima de 8 caracteres: OK")
        } else {
            validationResults.add("Longitud mínima de 8 caracteres: Fallido")
        }

        // Validar al menos una letra mayúscula
        if (password.any { it.isUpperCase() }) {
            validationResults.add("Contiene al menos una letra mayúscula: OK")
        } else {
            validationResults.add("Contiene al menos una letra mayúscula: Fallido")
        }

        // Validar al menos una letra minúscula
        if (password.any { it.isLowerCase() }) {
            validationResults.add("Contiene al menos una letra minúscula: OK")
        } else {
            validationResults.add("Contiene al menos una letra minúscula: Fallido")
        }

        // Validar al menos un carácter especial
        val caracteresEspeciales = "^\$*.[]{}()?\"!@#%&/\\,><':;|_~`"
        if (password.any { it in caracteresEspeciales }) {
            validationResults.add("Contiene al menos un carácter especial: OK")
        } else {
            validationResults.add("Contiene al menos un carácter especial: Fallido")
        }

        // Validar al menos una carácter numérico
        if (password.any { it.isDigit() }) {
            validationResults.add("Contiene al menos un carácter numérico: OK")
        } else {
            validationResults.add("Contiene al menos un carácter numérico: Fallido")
        }

        return validationResults
    }
    // Función para actualizar los TextViews con los resultados de la validación
    fun updateValidationUI(validationResults: List<String>) {
        // Uso de la función para actualizar los íconos
        updateValidationIcon(binding.validationLength, validationResults[0])
        updateValidationIcon(binding.validationUpperCase, validationResults[1])
        updateValidationIcon(binding.validationLowerCase, validationResults[2])
        updateValidationIcon(binding.validationSpecialChar, validationResults[3])
        updateValidationIcon(binding.validationNumericChar, validationResults[4])
    }
    // Función para actualizar los íconos de validación
    fun updateValidationIcon(view: TextView, validationResult: String) {
        val drawable = if (validationResult.contains("OK")) {
            R.drawable.baseline_check_circle_24
        } else {
            R.drawable.baseline_cancel_24
        }
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
    }

}