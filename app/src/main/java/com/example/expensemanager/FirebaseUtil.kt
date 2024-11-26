package com.example.expensemanager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseUtil {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var storageReference: StorageReference = FirebaseStorage.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().uid.toString())


    //fun getUserData(): user{

    /*fun initSaladMenu(menu: object) {
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