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
        binding.viewModeLayout.visibility = View.VISIBLE
        binding.editModeLayout.visibility = View.GONE
        getUserData()

        binding.editProfileBtn.setOnClickListener {
            binding.viewModeLayout.visibility = View.GONE
            binding.editModeLayout.visibility = View.VISIBLE
        }
        binding.cancelViewBtn.setOnClickListener {
            getUserData()
            binding.viewModeLayout.visibility = View.VISIBLE
            binding.editModeLayout.visibility = View.GONE
        }
        return  myView
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
            changeDetails(binding.nameProfile.text.toString(), email)
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
    private fun changeDetails(name: String, newEmail: String){
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        val progressDialog = AlertDialog.Builder(requireContext())
            .setView(ProgressBar(activity))
            .setCancelable(false)
            .setTitle("Updating Profile data")
            .setIcon(R.drawable.edit)
            .create()

        progressDialog.show()

        var userPassword = ""
        if (!user.email.equals(newEmail)) {
            // Pedir al usuario que ingrese su contraseña actual
            val builder = AlertDialog.Builder(requireContext())
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_email_change_request, null)
            val password = view.findViewById<EditText>(R.id.editBox)
            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnCfrm).setOnClickListener {
                userPassword = password.text.toString()  // Obtener la contraseña aquí
                dialog.dismiss()  // Cerrar el diálogo
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            if (dialog.window != null) {
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))  // Opción para personalizar la ventana del diálogo
            }
            dialog.show()

            val credentials = EmailAuthProvider.getCredential(user.email.toString(), userPassword)

            user.reauthenticate(credentials)
                .addOnSuccessListener {
                    // Reautenticación exitosa, proceder con la actualización del email
                    user.verifyBeforeUpdateEmail(newEmail)
                        .addOnSuccessListener {
                            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    uploadProfilePic(progressDialog)
                                } else {
                                    progressDialog.dismiss()
                                    toastMessage("Failed to update your profile. Invalid username")
                                }
                            }
                        }.addOnFailureListener {
                            progressDialog.dismiss()
                            toastMessage("Failed to update your profile. Invalid email")
                        }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    toastMessage("Reauthentication failed. Check credentials")
                }
        } else {
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadProfilePic(progressDialog)
                } else {
                    progressDialog.dismiss()
                    toastMessage("Failed to update your profile. Invalid username")
                }
            }
        }
    }

    private fun uploadProfilePic(progressDialog: AlertDialog){
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