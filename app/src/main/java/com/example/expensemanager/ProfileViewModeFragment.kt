package com.example.expensemanager

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
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileViewModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileViewModeFragment : Fragment() {
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

    //Fragment
    private lateinit var editMode: ProfileEditModeFragment
    private lateinit var viewMode: ProfileViewModeFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView  = inflater.inflate(R.layout.fragment_profile_view_mode, container,false)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(
            FirebaseAuth.getInstance().uid.toString())

        val btnEdit = myView.findViewById<Button>(R.id.edit_profile_btn)
        btnEdit.setOnClickListener {
            val newFragment = ProfileEditModeFragment() // Fragmento que deseas cargar
            replaceFragment(newFragment) // Cambia el fragmento
        }
        getUserData(myView)
        return  myView
    }

    private fun replaceFragment(newFragment: ProfileEditModeFragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.viewMode, fragment)
        fragmentTransaction.addToBackStack(null)  // Permite volver atrás
        fragmentTransaction.commit()
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


        val btnBack = myView.findViewById<Button>(R.id.back_profile_btn)
        btnBack.setOnClickListener {
            //this.finish() // TODO
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
}