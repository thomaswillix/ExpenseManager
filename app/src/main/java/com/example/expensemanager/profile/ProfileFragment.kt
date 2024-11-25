@file:Suppress("DEPRECATION")

package com.example.expensemanager.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.expensemanager.R
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


class ProfileFragment : Fragment() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri
    private lateinit var myView: View
    private lateinit var editModeLayout: View
    private lateinit var viewModeLayout: View
    // Profile Pic
    private lateinit var profilePic: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        myView  = inflater.inflate(R.layout.fragment_profile, container,false)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(
            FirebaseAuth.getInstance().uid.toString())
        super.onViewCreated(myView, savedInstanceState)

        // ViewMode
        val viewModeLayout = myView.findViewById<LinearLayout>(R.id.viewModeLayout)
        val btnBack = myView.findViewById<Button>(R.id.back_profile_btn)
        val btnEdit = myView.findViewById<Button>(R.id.edit_profile_btn)

        //EditMode
        editModeLayout = myView.findViewById<LinearLayout>(R.id.editModeLayout)
        val btnCancel = myView.findViewById<Button>(R.id.cancel_view_btn)

        // Show view mode by default
        viewModeLayout.visibility = View.VISIBLE
        editModeLayout.visibility = View.GONE
        getUserData(myView)

        btnBack.setOnClickListener {
            activity?.finish()
        }
        // Change into edit mode
        btnEdit.setOnClickListener {
            viewModeLayout.visibility = View.GONE
            editModeLayout.visibility = View.VISIBLE
        }
        btnCancel.setOnClickListener {
            getUserData(myView)
            editModeLayout.visibility = View.GONE
            viewModeLayout.visibility = View.VISIBLE
        }
        return  myView
    }

    private fun getUserData(myView: View) {
        user = FirebaseAuth.getInstance().currentUser!!
        //ViewMode
        val imageViewMode = myView.findViewById<ImageView>(R.id.imageView2)

        val tvName = myView.findViewById<TextView>(R.id.name_profileViewMode)
        val tvEmail = myView.findViewById<TextView>(R.id.email_profileViewMode)
        val tvPhone = myView.findViewById<TextView>(R.id.phone_profileViewMode)

        //Edit Mode
        profilePic = myView.findViewById(R.id.imageView_Edit)
        val btnSave = myView.findViewById<Button>(R.id.confirm_info_btn)

        val etName = myView.findViewById<EditText>(R.id.name_profile)
        val etEmail = myView.findViewById<EditText>(R.id.email_profile)
        val etPhone = myView.findViewById<EditText>(R.id.phone_profile)
        //val number: PhoneAuthCredential
        getProfilePic(profilePic)
        getProfilePic(imageViewMode)

        val name : String
        val email = user.email!!
        var phone = ""
        name = if (user.displayName != null) user.displayName!!
        else email.substring(0, email.indexOf("@"))

        if (user.phoneNumber != null) phone = user.phoneNumber.toString()

        tvName.text = name
        tvEmail.text = email
        tvPhone.text = phone

        etName.setText(name, TextView.BufferType.EDITABLE)
        etEmail.setText(email, TextView.BufferType.EDITABLE)
        etPhone.setText(phone, TextView.BufferType.EDITABLE)

        btnSave.setOnClickListener {
            changeDetails(etName.text.toString(), email)
        }

        profilePic.setOnClickListener {
            changeProfilePic()
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
    private fun changeDetails(name: String, email: String){
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        //user.updatePhoneNumber(phone)
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
    private fun uploadProfilePic(progressDialog: ProgressDialog){
        if (uri == null){
            uri = Uri.parse("android.resource://${android.R.attr.packageNames}${R.drawable.pfp}")
            profilePic.setImageURI(uri)
        }
        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            editModeLayout.visibility = View.GONE
            viewModeLayout.visibility = View.VISIBLE

        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(activity, "Failed to upload the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeProfilePic (){
        ImagePicker.with(this)
        ImagePicker.with(this)
            .cropSquare()	//Crop square image, its same as crop(1f, 1f)
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePic = myView.findViewById(R.id.imageView_Edit)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                uri = data?.data!!
                // Use Uri object instead of File to avoid storage permissions
                profilePic.setImageURI(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}