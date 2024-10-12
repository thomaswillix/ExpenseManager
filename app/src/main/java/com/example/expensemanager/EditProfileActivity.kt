package com.example.expensemanager

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.expensemanager.databinding.ActivityMainBinding
import com.firebase.client.Firebase
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.callbackFlow

class EditProfileActivity : AppCompatActivity() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    // Profile Pic
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_edit_profile)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        getUserData(uid)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePic = findViewById<ImageView>(R.id.imageView2)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            profilePic.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
    fun getUserData(uid: String?){
        user = FirebaseAuth.getInstance().currentUser!!
        val mName = findViewById<EditText>(R.id.name_profile)
        val mEmail = findViewById<EditText>(R.id.email_profile)
        profilePic = findViewById<ImageView>(R.id.imageView2)
        getProfilePic(profilePic, uid)

        var name = ""
        val email = user.email!!
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email?.substring(0, email.indexOf("@")).toString()
        }
        mName.setText(name, TextView.BufferType.EDITABLE)
        mEmail.setText(email, TextView.BufferType.EDITABLE)

        // TODO: HACER QUE LA FOTO QUE SE MUESTRE SEA DE LA BASE DE DATOS Y SI NO EXISTE PONER LA DE POR DEFECTO
        val btnConfirm = findViewById<Button>(R.id.confirm_info_btn)
        btnConfirm.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            changeDetails(progressDialog, uid, user, name, email, profilePic)
        }
        val btnBack = findViewById<Button>(R.id.back_profile_btn)
        btnBack.setOnClickListener {
            this.finish()
        }
        profilePic.setOnClickListener {
            changeProfilePic()
        }

    }
    fun getProfilePic(imageView: ImageView, uid: String?){
        // The storage reference to the firebase file
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pic").child(uid!!)
        val uri = storageReference.downloadUrl
        if (uri == null || !uri.isSuccessful){
            imageView.setImageURI(Uri.parse("android.resource://$packageName${R.drawable.pfp}"))
        } else{
            imageView.setImageURI(uri.result)
        }
    }
    fun changeDetails(progressDialog: ProgressDialog, uid: String?, firebaseUser:FirebaseUser,  name: String, email: String, picture: ImageView){
        val user = User(name, email)
        if(uid != null){
            databaseReference.child(uid).setValue(user).addOnCompleteListener{
                if(it.isSuccessful){
                    uploadProfilePic(progressDialog)
                } else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to update your profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun uploadProfilePic(progressDialog: ProgressDialog){
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + auth.currentUser?.uid)
        profilePic = findViewById<ImageView>(R.id.imageView2)
        val uri = storageReference.downloadUrl

        // If the initial URI is not null, we
        if (uri == null){
            profilePic.setImageURI(Uri.parse("android.resource://$packageName${R.drawable.pfp}"))
        } else{
            profilePic.setImageURI(uri.result)
        }
        storageReference.putFile(uri.result).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Profile updated succesfully", Toast.LENGTH_SHORT).show()
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