package com.example.expensemanager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.FragmentProfileBinding
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
    // Profile Pic
    private lateinit var profilePic: ImageView

    private lateinit var binding: FragmentProfileBinding

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
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(
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
        var phone = ""
        name = if (user.displayName != null) user.displayName!!
        else email.substring(0, email.indexOf("@"))

        if (user.phoneNumber != null) phone = user.phoneNumber.toString()

        binding.nameProfileViewMode.text = name
        binding.emailProfileViewMode.text = email
        binding.phoneProfileViewMode.text = phone

        binding.nameProfile.setText(name, TextView.BufferType.EDITABLE)
        binding.emailProfile.setText(email, TextView.BufferType.EDITABLE)
        binding.phoneProfile.setText(phone, TextView.BufferType.EDITABLE)

        binding.confirmInfoBtn.setOnClickListener {
            changeDetails(binding.nameProfile.text.toString(), email)
        }

        profilePic.setOnClickListener {
            changeProfilePic()
        }
    }
    private fun  getProfilePic(imageView: ImageView){
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

        val progressDialog = AlertDialog.
        Builder(requireContext()).
        setView(ProgressBar(activity)).
        setCancelable(false).
        setTitle("Updating Profile data").
        create()

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
    private fun uploadProfilePic(progressDialog: AlertDialog){
        if (!::uri.isInitialized) {
            uri = Uri.parse("android.resource://${activity?.packageName}/drawable/pfp")
            profilePic.setImageURI(uri)
        }
        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            binding.editModeLayout.visibility = View.GONE
            binding.viewModeLayout.visibility = View.VISIBLE
            getUserData()

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super. onActivityResult(requestCode, resultCode, data)
        profilePic = myView.findViewById(R.id.imageView_Edit)
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.data?.let {
                    uri = it
                    profilePic.setImageURI(uri)
                } ?: run {
                    Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show()
                }
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