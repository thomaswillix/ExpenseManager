package com.example.expensemanager

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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File


class ProfileFragment : Fragment() {
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
        val editModeLayout = myView.findViewById<LinearLayout>(R.id.editModeLayout)
        val btnCancel = myView.findViewById<Button>(R.id.cancel_view_btn)

        // Mostrar la vista de perfil por defecto
        viewModeLayout.visibility = View.VISIBLE
        editModeLayout.visibility = View.GONE

        btnBack.setOnClickListener {
            activity?.finish()
        }
        // Cambiar al modo de edición
        btnEdit.setOnClickListener {
            getUserData(myView)
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
        //ViewMode
        val imageeViewMode = myView.findViewById<ImageView>(R.id.imageView2)

        val tvName = myView.findViewById<TextView>(R.id.name_profileViewMode)
        val tvEmail = myView.findViewById<TextView>(R.id.email_profileViewMode)
        val tvPhone = myView.findViewById<TextView>(R.id.phone_profileViewMode)

        //Edit Mode
        profilePic = myView.findViewById<ImageView>(R.id.imageView_Edit)
        val btnSave = myView.findViewById<Button>(R.id.confirm_info_btn)

        val etName = myView.findViewById<EditText>(R.id.name_profile)
        val etEmail = myView.findViewById<EditText>(R.id.email_profile)
        val etPhone = myView.findViewById<EditText>(R.id.phone_profile)
        //val number: PhoneAuthCredential
        getProfilePic(profilePic)
        getProfilePic(imageeViewMode)

        var name = ""
        val email = user.email!!
        var phone = ""
        if (user.displayName != null) name = user.displayName!!
        else name = email.substring(0, email.indexOf("@"))

        if (user.phoneNumber != null) phone = user.phoneNumber.toString()

        tvName.setText(name)
        tvEmail.setText(email)
        tvPhone.setText(phone)

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
    fun changeDetails(name: String, email: String){
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