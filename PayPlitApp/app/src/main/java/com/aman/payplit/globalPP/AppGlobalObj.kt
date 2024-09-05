package com.aman.payplit.globalPP

import com.google.firebase.auth.FirebaseAuth


object AppGlobalObj {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    //    val database = FirebaseDatabase.getInstance()
//    val referencedb : DatabaseReference = database.reference.child("checks")

}