package com.example.projectone.admin.admindatamodels

data class Admin(
    val fullname: String = "",
    val email: String = "",
    val mobile: String = "",
    val empId: String = "",
    val role: String = "", // This represents the role
    val doj: String = "",
    val companyName: String = "",
    val companyEmail: String = "",
    val companyAddress: String = "",
    val roleOfAccess: String = "",
    val department: String = "",
    val linkedIn: String = "",
    val experience: String = "",
    val highestQualification: String = "",
    val languagesKnown: String = "",
    val skills: String = "",
    val gender: String = "" // Optional field
)
