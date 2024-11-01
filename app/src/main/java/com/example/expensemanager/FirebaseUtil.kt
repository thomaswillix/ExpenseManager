package com.example.expensemanager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class FirebaseUtil {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var storageReference: StorageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())
    private lateinit var uri: Uri
    /*fun getProfilePic(imageView: ImageView){
        // First we create a temp file
        val localFile : File = File.createTempFile("pfp", "jpg")
        // Then we get the File from the storage reference
        storageReference.getFile(localFile).addOnSuccessListener { // if it succeeds we set it to the imageView
            val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            uri = localFile.toUri()
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener { // if it doesn't succeed, we set the default pfp
            imageView.setImageURI(Uri.parse("android.resource://$packageName${R.drawable.pfp}"))
        }
    }

    fun getUserData(): User{
        user = FirebaseAuth.getInstance().currentUser!!

        var name = ""
        val email = user.email!!
        if (user.displayName != null) {
            name = user.displayName!!
        } else{
            name = email.substring(0, email.indexOf("@"))
        }

        val usr : User = User(name, email)
        return usr
    }
    fun initSaladMenu(menu: object) {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                menu.clear()
                dataSnapshot.children.mapNotNullTo(menu) { it.getValue<Salad>(Salad::class.java) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("types").addListenerForSingleValueEvent(menuListener)
    }*/
}