package com.example.expensemanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.FragmentProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File


class ProfileFragment : Fragment() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri
    private lateinit var myView: View
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var isEditMode = false
    private lateinit var progressDialog : AlertDialog
    // Profile Pic
    private lateinit var profilePic: ImageView
    // Binding
    private lateinit var binding: FragmentProfileBinding
    // Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val readStorageGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

        if (cameraGranted || readStorageGranted) {
            // Permitir acceso a la cámara o galería dependiendo de los permisos concedidos
            openImagePicker(cameraGranted, readStorageGranted)
        } else {
            toastMessage("Permisos denegados. No se puede acceder a la cámara ni a la galería")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding inflation
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        myView  = binding.root
        super.onViewCreated(myView, savedInstanceState)
        //Firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().reference.child("Users").child(
            FirebaseAuth.getInstance().uid.toString())
        // Default value
        uri = Uri.parse("android.resource://${activity?.packageName}/drawable/pfp")

        // Show view mode by default
        updateViewMode()
        getUserData()

        binding.editProfileBtn.setOnClickListener {
            isEditMode = true
            updateViewMode()
        }
        binding.cancelViewBtn.setOnClickListener {
            isEditMode = false
            getUserData()
            updateViewMode()
        }
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && user.isEmailVerified) {
                updateViewMode() // Cambiar el modo de edición a vista
                Log.d("FirebaseAuth", "User email updated: ${user.email}")
            }
        }
        return  myView
    }
    private fun updateViewMode() {
        // Cambiar el modo del fragment
        if (isEditMode){
            binding.viewModeLayout.visibility = View.GONE
            binding.editModeLayout.visibility = View.VISIBLE
        }else{
            binding.viewModeLayout.visibility = View.VISIBLE
            binding.editModeLayout.visibility = View.GONE
        }
        // Actualizar datos de la interfaz con el nuevo correo
        val user = FirebaseAuth.getInstance().currentUser
        binding.emailProfileViewMode.text = user?.email
    }

    private fun getUserData() {
        user = FirebaseAuth.getInstance().currentUser!!
        profilePic = binding.imageViewEdit
        getProfilePic(profilePic)
        getProfilePic(binding.imageView2)

        val name : String
        val email = user.email!!
        name = if (user.displayName != null) user.displayName!!
        else email.substring(0, email.indexOf("@"))

        binding.nameProfileViewMode.text = name
        binding.emailProfileViewMode.text = email

        binding.nameProfile.setText(name, TextView.BufferType.EDITABLE)
        binding.emailProfile.setText(email, TextView.BufferType.EDITABLE)

        binding.confirmInfoBtn.setOnClickListener {
            changeDetails(binding.nameProfile.text.toString(), binding.emailProfile.text.toString())
        }

        profilePic.setOnClickListener {
            if (checkPermissions()) {
                // Abrir el selector si los permisos están concedidos
                openImagePicker(true, true)
            } else {
                // Solicitar permisos si no están concedidos
                requestPermissions()
            }
        }
    }
    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val readStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermission && readStoragePermission
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }
    private fun openImagePicker(cameraGranted: Boolean, readStorageGranted: Boolean) {
        if (cameraGranted && readStorageGranted) {
            // Permitir tanto cámara como galería
            ImagePicker.with(this)
                .cameraOnly() // Permite el uso de la cámara
                .galleryOnly() // También puede acceder a la galería
                .crop() // Opcional: permitir recorte
                .compress(1024) // Reducir tamaño a 1MB
                .maxResultSize(1080, 1080) // Limitar resolución máxima
                .start()
        } else if (cameraGranted) {
            // Solo cámara permitida
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(1024) // Reducir tamaño a 1MB
                .maxResultSize(1080, 1080) // Limitar resolución máxima
                .start()
        } else if (readStorageGranted) {
            // Solo galería permitida
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(1024) // Reducir tamaño a 1MB
                .maxResultSize(1080, 1080) // Limitar resolución máxima
                .start()
        } else {
            toastMessage("No se puede acceder a la cámara ni a la galería")
        }
    }
    private fun getProfilePic(imageView: ImageView){
        // First we create a temp file
        val localFile : File = File.createTempFile("pfp", "jpg")
        // Then we get the File from the storage reference
        try {
                storageReference.getFile(localFile).addOnSuccessListener { // if it succeeds we set it to the imageView
                val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                uri = localFile.toUri()
                imageView.setImageBitmap(bitmap)
            }
        }catch (_: StorageException){}
    }
    private fun changeDetails(name: String, newEmail: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        progressDialog = AlertDialog.Builder(requireContext())
            .setView(ProgressBar(requireContext()))
            .setCancelable(false)
            .setTitle("Updating Profile Data")
            .setIcon(R.drawable.edit)
            .create()

        progressDialog.show()

        // Verificar si se necesita cambiar el correo
        if (user.email != newEmail) {
            promptPassword { userPassword ->
                val credentials = EmailAuthProvider.getCredential(user.email!!, userPassword)

                // Reautenticación del usuario
                user.reauthenticate(credentials)
                    .addOnSuccessListener {
                        Log.d("FirebaseAuth", "Reauthentication successful")
                        // Solicitar verificación antes de actualizar el correo
                        user.verifyBeforeUpdateEmail(newEmail)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                toastMessage("A verification email has been sent to $newEmail. Please verify to complete the process.")
                            }
                            .addOnFailureListener { exception ->
                                progressDialog.dismiss()
                                toastMessage("Failed to send verification email: ${exception.localizedMessage}")
                                Log.e("FirebaseAuth", "Verification email failed: ${exception.localizedMessage}")
                            }
                    }
                    .addOnFailureListener { exception ->
                        progressDialog.dismiss()
                        toastMessage("Reauthentication failed: ${exception.localizedMessage}")
                        Log.e("FirebaseAuth", "Reauthentication error: ${exception.localizedMessage}")
                    }
            }
        } else {
            // Solo actualizar el perfil si no se cambia el correo
            updateProfile(profileUpdates)
        }
    }

    // Actualizar el perfil del usuario (nombre, foto, etc.)
    private fun updateProfile(profileUpdates: UserProfileChangeRequest) {
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadProfilePic() // Llamada a uploadProfilePic
            } else {
                progressDialog.dismiss()
                toastMessage("Failed to update profile: ${task.exception?.localizedMessage}")
                Log.e("FirebaseAuth", "Profile update failed: ${task.exception?.localizedMessage}")
            }
        }
    }

    //Solicitar la contraseña actual al usuario mediante un diálogo.
    private fun promptPassword(onPasswordEntered: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_email_change_request, null)
        val passwordInput = view.findViewById<EditText>(R.id.editBox)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()

        view.findViewById<Button>(R.id.btnCfrm).setOnClickListener {
            val password = passwordInput.text.toString()
            if (password.isNotEmpty()) {
                dialog.dismiss()
                onPasswordEntered(password)
            } else {
                toastMessage("Password cannot be empty")
            }
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
            progressDialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    private fun uploadProfilePic(){
        if (!::uri.isInitialized) {
            uri = Uri.parse("android.resource://${activity?.packageName}/drawable/pfp")
            profilePic.setImageURI(uri)
        }
        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            toastMessage("Profile updated successfully")
            binding.editModeLayout.visibility = View.GONE
            binding.viewModeLayout.visibility = View.VISIBLE
            getUserData()

        }.addOnFailureListener{
            progressDialog.dismiss()
            toastMessage("Failed to upload the image")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super. onActivityResult(requestCode, resultCode, data)
        profilePic = myView.findViewById(R.id.imageView_Edit)
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.data?.let {
                    uri = it
                    profilePic.setImageURI(uri)
                } ?: run {
                    toastMessage("No image selected")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                toastMessage(ImagePicker.getError(data))
            }
            else -> {
                toastMessage("Task Cancelled")
            }
        }
    }
    private fun toastMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}