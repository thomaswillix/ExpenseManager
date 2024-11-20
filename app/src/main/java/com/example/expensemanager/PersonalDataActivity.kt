package com.example.expensemanager

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class PersonalDataActivity : AppCompatActivity() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri

    // Profile Pic
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())
        getUserData()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePic = findViewById(R.id.imageView2)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            profilePic.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    fun getUserData(){
        user = FirebaseAuth.getInstance().currentUser!!
        val mName = findViewById<EditText>(R.id.name_profile)
        val mEmail = findViewById<EditText>(R.id.email_profile)
        profilePic = findViewById(R.id.imageView2)
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

        val btnConfirm = findViewById<Button>(R.id.confirm_info_btn)
        btnConfirm.setOnClickListener {
            changeDetails(mName.text.toString(), email)
        }
        val btnBack = findViewById<Button>(R.id.back_profile_btn)
        btnBack.setOnClickListener {
            this.finish()
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
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadProfilePic(progressDialog)
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to update your profile", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun uploadProfilePic(progressDialog: ProgressDialog){
        profilePic = findViewById(R.id.imageView2)

        if (uri == null){
            uri = Uri.parse("android.resource://$packageName${R.drawable.pfp}")
            profilePic.setImageURI(uri)
        }
        storageReference.putFile(uri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Profile updated succesfully", Toast.LENGTH_SHORT).show()
            this.finish()
        }.addOnFailureListener{
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to upload the image", Toast.LENGTH_SHORT).show()
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