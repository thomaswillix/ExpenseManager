package com.example.expensemanager

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.google.firebase.database.values
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseUtil {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var storageReference: StorageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())


    fun getUserData(){
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val userData = dataSnapshot.getValue<User>()
                /*storageReference.downloadUrl.addOnSuccessListener {
                    Log.i("firebase", "Got value")
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child(auth.uid!!).addValueEventListener(userListener)
    }

}