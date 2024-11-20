package com.example.expensemanager

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File


class ProfileEditModeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri
    private lateinit var myView: View

    // Profile Pic
    private lateinit var profilePic: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView  = inflater.inflate(R.layout.fragment_profile_edit_mode, container,false)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(
            FirebaseAuth.getInstance().uid.toString())
        /*super.onViewCreated(view, savedInstanceState)

        // Referencias a los elementos del layout
        val viewModeLayout = view.findViewById<LinearLayout>(R.id.viewModeLayout)
        val editModeLayout = view.findViewById<LinearLayout>(R.id.editModeLayout)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)

        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)

        // Mostrar la vista de perfil por defecto
        viewModeLayout.visibility = View.VISIBLE
        editModeLayout.visibility = View.GONE

        // Cambiar al modo de edición
        btnEdit.setOnClickListener {
            viewModeLayout.visibility = View.GONE
            editModeLayout.visibility = View.VISIBLE
        }

        // Guardar los cambios y volver al modo vista
        btnSave.setOnClickListener {
            val newName = etName.text.toString()
            val newEmail = etEmail.text.toString()
            val newPhone = etPhone.text.toString()

            // Aquí podrías agregar validación para los campos

            // Actualizar los datos en la vista de solo lectura
            tvName.text = "Nombre: $newName"
            tvEmail.text = "Correo: $newEmail"
            tvPhone.text = "Teléfono: $newPhone"

            // Volver al modo vista
            viewModeLayout.visibility = View.VISIBLE
            editModeLayout.visibility = View.GONE
        }*/
        getUserData(myView)
        return  myView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePic = myView.findViewById(R.id.imageView2)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            profilePic.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    fun getUserData(myView: View) {
        user = FirebaseAuth.getInstance().currentUser!!
        val mName = myView.findViewById<EditText>(R.id.name_profile)
        val mEmail = myView.findViewById<EditText>(R.id.email_profile)
        profilePic = myView.findViewById(R.id.imageView2)
        getProfilePic(profilePic)

        var name = ""
        val email = user.email!!
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email.substring(0, email.indexOf("@"))
        }
        mName.setText(name, TextView.BufferType.EDITABLE)
        mEmail.setText(email, TextView.BufferType.EDITABLE)

        val btnConfirm = myView.findViewById<Button>(R.id.confirm_info_btn)
        btnConfirm.setOnClickListener {
            changeDetails(mName.text.toString(), email)
        }
        val btnBack = myView.findViewById<Button>(R.id.back_profile_btn)
        btnBack.setOnClickListener {
            //this.finish() // TODO
        }
        profilePic.setOnClickListener {
            changeProfilePic()
        }
    }
    fun getProfilePic(imageView: ImageView){
        // First we create a temp file
        val localFile : File = File.createTempFile("pfp", "jpg")
        // Then we get the File from the storage reference
        try {
            storageReference.getFile(localFile).addOnSuccessListener { // if it succeeds we set it to the imageView
                val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                uri = localFile.toUri()
                imageView.setImageBitmap(bitmap)
            }
        }catch (e : StorageException){}
    }
    fun changeDetails(name: String, newEmail: String){
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadProfilePic(progressDialog)
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(activity, "Failed to update your profile", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun uploadProfilePic(progressDialog: ProgressDialog){
        profilePic = myView.findViewById(R.id.imageView2)

        if (uri == null){
            uri = Uri.parse("android.resource://${android.R.attr.packageNames}${R.drawable.pfp}")
            profilePic.setImageURI(uri)
        }
        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(activity, "Profile updated succesfully", Toast.LENGTH_SHORT).show()
            //this.finish() TODO
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(activity, "Failed to upload the image", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeProfilePic (){
        ImagePicker.with(this)
        ImagePicker.with(this)
            .cropSquare()	//Crop square image, its same as crop(1f, 1f)
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }
}