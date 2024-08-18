package com.example.projectone.salesperson.salespersondatamodels

data class Orders(
    val customerName: String = "",
    val id: String? = null, // Ensure this matches the key in Firebase
    val clientId: String = "",
    val empId: String="",
    val mobileNumber: String = "",
    val email: String = "",
    val address: String = "",
    val companyName: String = "",
    val productName: String = "",
    val orderQuantity: String = "",
    val productDescription: String = "",
    val orderDate: String = "",
    val dueDate: String = "",
    val estimatedBudget: String = "",
    val modeOfPayment: String = "",
    val paymentStatus: String = "",
    val salesPersonName: String = "",
    val salesPersonProfileImageUrl: String = "",
    val profileImageUrl: String = "",
    var orderStatus:String="Order Received",
    val imageUrl: String? = null,
    val createdBy: String? = null,
    val orderId: String? = null,
    val ratings: String? = null,


    )
