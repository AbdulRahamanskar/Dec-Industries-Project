package com.example.projectone.salesperson

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projectone.R
import com.example.projectone.client.ClientProjects
import com.example.projectone.client.clientdatamodels.Client
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class SalesPersonCreateOrder : AppCompatActivity() {

    private lateinit var salesPersonCreateCustFullname: AutoCompleteTextView
    private lateinit var createOrderLodingBar: ProgressBar // Fixed type name
    private lateinit var salesPersonCreateCustMobileNo: TextInputEditText
    private lateinit var salesPersonCreateCustId: TextInputEditText
    private lateinit var salesPersonCreateCustEmail: TextInputEditText
    private lateinit var salesPersonCreateCompanyName: TextInputEditText
    private lateinit var salesPersonCreateCustAddress: TextInputEditText
    private lateinit var salesPersonCreateCustProductName: TextInputEditText
    private lateinit var salesPersonCreateCustOrderQuantity: TextInputEditText
    private lateinit var salesPersonCreateCustProductDesc: TextInputEditText
    private lateinit var salesPersonCreateCustUploadSampleImg: ImageView
    private lateinit var salesPersonCreateCustOrderDate: TextInputEditText
    private lateinit var salesPersonCreateCustDueDate: TextInputEditText
    private lateinit var salesPersonCreateCustEstBuget: TextInputEditText
    private lateinit var salesPersonCreateCustModeOfPayment: TextInputEditText
    private lateinit var salesPersonCreatePaymentStatus: TextInputEditText
    private lateinit var salesPersonNameOfAssignedSalesPerson: TextInputEditText
    private lateinit var salesPersonEmpIdOfAssignedSalesPerson: TextInputEditText
    private lateinit var creatOrderButton: Button
    private lateinit var salesBottomMenu: BottomNavigationView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_create_order)

        // Initialize views
        salesPersonCreateCustFullname = findViewById(R.id.atvCreateOrderSalesCustomerName)
        createOrderLodingBar = findViewById(R.id.createOrderLodingBar) // Correct initialization
        salesPersonCreateCustMobileNo = findViewById(R.id.etCreateOrderSalesCustomerMobileNumber)
        salesPersonCreateCustId = findViewById(R.id.etCreateOrderSalesCustomerId)
        salesPersonCreateCustEmail = findViewById(R.id.etCreateOrderSalesCustomerEmail)
        salesPersonCreateCompanyName = findViewById(R.id.etCreateOrderSalesCompanyName)
        salesPersonCreateCustAddress = findViewById(R.id.etCreateOrderSalesCustomerAddress)
        salesPersonCreateCustProductName = findViewById(R.id.etCreateOrderSalesCustomerProductName)
        salesPersonCreateCustOrderQuantity = findViewById(R.id.etCreateOrderSalesCustomerProductOrderQuantity)
        salesPersonCreateCustProductDesc = findViewById(R.id.etCreateOrderSalesCustomerProductDescription)
        salesPersonCreateCustUploadSampleImg = findViewById(R.id.ivUploadSampleImg)
        salesPersonCreateCustOrderDate = findViewById(R.id.etCreateOrderSalesCustomerProductOrderDate)
        salesPersonCreateCustDueDate = findViewById(R.id.etCreateOrderSalesCustomerProductDueDate)
        salesPersonCreateCustEstBuget = findViewById(R.id.etCreateOrderSalesCustomerEstProductBudget)
        salesPersonCreateCustModeOfPayment = findViewById(R.id.etCreateOrderSalesCustomerProductModeOfPayment)
        salesPersonCreatePaymentStatus = findViewById(R.id.etCreateOrderSalesCustomerProductPaymentStatus)
        salesPersonNameOfAssignedSalesPerson = findViewById(R.id.etCreateOrderSalesCustomerProductSalesPersonName)
        salesPersonEmpIdOfAssignedSalesPerson = findViewById(R.id.etCreateOrderSalesCustomerProductSalesPersonEmpId)
        creatOrderButton = findViewById(R.id.btnSalesPersonCreateOrder)
        salesBottomMenu = findViewById(R.id.salesPersonBottom)

        // Set up Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("orders") // Update path to "orders"
        storageReference = FirebaseStorage.getInstance().reference

        salesPersonCreateCustId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Fetch client details when text changes
                val clientId = s.toString()
                if (clientId.isNotBlank()) {
                    fetchClientDetails(clientId)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })




        // Set up UI components
        personDrawableAdjust()
        setupAutoCompleteClientNames()
        setCurrentDate()
        setUpDueDatePicker()
        setupCreateOrderButton()
        setupBottomNavigation()

        // Fetch and set salesperson's name
        fetchAndSetSalesPersonName()


        // Set up image upload click listener
        salesPersonCreateCustUploadSampleImg.setOnClickListener {
            openImageChooser()
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            salesPersonCreateCustUploadSampleImg.setImageURI(imageUri)
        }
    }

    private fun personDrawableAdjust() {
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.baseline_person_4_24)
        salesPersonCreateCustFullname.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        val drawablePadding = resources.getDimensionPixelSize(R.dimen.drawable_padding)
        salesPersonCreateCustFullname.setCompoundDrawablePadding(drawablePadding)
        salesPersonCreateCustFullname.setPadding(15 + drawablePadding, 0, 0, 0)
    }

    private fun setupAutoCompleteClientNames() {
        // Reference to the "users" node where client data is stored
        val clientRef = FirebaseDatabase.getInstance().getReference("users")

        // Filter clients based on their role
        clientRef.orderByChild("role").equalTo("client").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clientNames = mutableListOf<String>()
                for (clientSnapshot in snapshot.children) {
                    val clientName = clientSnapshot.child("fullname").getValue(String::class.java)
                    if (!clientName.isNullOrBlank()) {
                        clientNames.add(clientName)
                    }
                }
                // Set up the adapter for AutoCompleteTextView
                val adapter = ArrayAdapter(this@SalesPersonCreateOrder, android.R.layout.simple_dropdown_item_1line, clientNames)
                salesPersonCreateCustFullname.setAdapter(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesPersonCreateOrder, "Failed to load client names", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setCurrentDate() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        salesPersonCreateCustOrderDate.setText(currentDate)
    }

    private fun setUpDueDatePicker() {
        salesPersonCreateCustDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time)
                salesPersonCreateCustDueDate.setText(selectedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }

    private fun setupCreateOrderButton() {
        creatOrderButton.setOnClickListener {
            if (validateFields()) {
                showLoadingBar() // Show the loading bar before starting the upload
                uploadImageToFirebaseStorage() // Upload image and then create order

            }
        }
    }

    private fun fetchAndSetSalesPersonName() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val salesPersonRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId)
            salesPersonRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullname = snapshot.child("fullname").getValue(String::class.java)
                    val empId = snapshot.child("empId").getValue(String::class.java)
                    if (fullname != null && fullname.isNotBlank()) {
                        salesPersonNameOfAssignedSalesPerson.setText(fullname)
                    } else {
                        salesPersonNameOfAssignedSalesPerson.setText("Unknown")
                    }

                    if (empId != null && empId.isNotBlank()) {
                        salesPersonEmpIdOfAssignedSalesPerson.setText(empId)
                    } else {
                        salesPersonEmpIdOfAssignedSalesPerson.setText("Unknown")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SalesPersonCreateOrder, "Failed to load salesperson data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateFields(): Boolean {
        val fields = listOf(
            salesPersonCreateCustFullname.text,
            salesPersonCreateCustMobileNo.text,
            salesPersonCreateCustId.text,
            salesPersonCreateCustEmail.text,
            salesPersonCreateCompanyName.text,
            salesPersonCreateCustAddress.text,
            salesPersonCreateCustProductName.text,
            salesPersonCreateCustOrderQuantity.text,
            salesPersonCreateCustProductDesc.text,
            salesPersonCreateCustDueDate.text,
            salesPersonCreateCustEstBuget.text,
            salesPersonCreateCustModeOfPayment.text,
            salesPersonCreatePaymentStatus.text
        )

        for (field in fields) {
            if (field.isNullOrBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            val storageRef = storageReference.child("order_images/${UUID.randomUUID()}")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Get the download URL and pass it to createOrder()
                        createOrder(uri.toString())
                    }
                        .addOnFailureListener {
                            hideLoadingBar() // Hide the loading bar in case of failure
                            Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    hideLoadingBar() // Hide the loading bar in case of failure
                    Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // If no image is selected, create order without an image
            createOrder(null)
        }
    }

    private fun createOrder(imageUrl: String?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val orderId = generateUniqueOrderId()
//        val orderId = databaseReference.push().key ?: return

        val salesPersonRef = FirebaseDatabase.getInstance().getReference("salespersons").child(currentUser?.uid ?: "")
        salesPersonRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val salesPerson = snapshot.getValue(SalesPerson::class.java)
                val employeeId=salesPerson?.empId?:"null"
                val salesPersonName = salesPerson?.fullname ?: "Unable to detect"
                val salesPersonProfileImageUrl = salesPerson?.profileImageUrl ?: ""

                val clientId = salesPersonCreateCustId.text.toString()

                val order = hashMapOf(
                    "customerName" to salesPersonCreateCustFullname.text.toString(),
                    "mobileNumber" to salesPersonCreateCustMobileNo.text.toString(),
                    "clientId" to clientId,
                    "orderId" to orderId,
                    "empId" to salesPersonEmpIdOfAssignedSalesPerson.text.toString(),
                    "email" to salesPersonCreateCustEmail.text.toString(),
                    "companyName" to salesPersonCreateCompanyName.text.toString(),
                    "companyAddress" to salesPersonCreateCustAddress.text.toString(),
                    "productName" to salesPersonCreateCustProductName.text.toString(),
                    "orderQuantity" to salesPersonCreateCustOrderQuantity.text.toString(),
                    "productDescription" to salesPersonCreateCustProductDesc.text.toString(),
                    "orderDate" to salesPersonCreateCustOrderDate.text.toString(),
                    "dueDate" to salesPersonCreateCustDueDate.text.toString(),
                    "estimatedBudget" to salesPersonCreateCustEstBuget.text.toString(),
                    "modeOfPayment" to salesPersonCreateCustModeOfPayment.text.toString(),
                    "paymentStatus" to salesPersonCreatePaymentStatus.text.toString(),
                    "salesPersonName" to salesPersonNameOfAssignedSalesPerson.text.toString(),
//                    "salesPersonName" to salesPersonName,
                    "salesPersonProfileImageUrl" to salesPersonProfileImageUrl,
                    "imageUrl" to imageUrl,
                    "createdBy" to salesPersonNameOfAssignedSalesPerson.text.toString()
                )

                databaseReference.child(orderId).setValue(order)
                    .addOnSuccessListener {
                        hideLoadingBar() // Hide the loading bar on success
                        Toast.makeText(this@SalesPersonCreateOrder, "Order created successfully!", Toast.LENGTH_SHORT).show()
                        clearForm()

                        // Navigate to ClientProjects with clientId
                        val intent = Intent(this@SalesPersonCreateOrder, ClientProjects::class.java)
                        intent.putExtra("CLIENT_ID", clientId)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        hideLoadingBar() // Hide the loading bar on failure
                        Toast.makeText(this@SalesPersonCreateOrder, "Failed to create order", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoadingBar() // Hide the loading bar on failure
                Toast.makeText(this@SalesPersonCreateOrder, "Failed to load salesperson data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        salesBottomMenu.selectedItemId = R.id.salesPersonCreateOrderMenu
        salesBottomMenu.setOnNavigationItemSelectedListener {
            HandleNavigateListener(it.itemId)
        }
    }

    private fun HandleNavigateListener(itemId: Int): Boolean {
        if (itemId == salesBottomMenu.selectedItemId) {
            return true
        }
        when (itemId) {
            R.id.salesPersonCreateOrderMenu -> {
                // No action needed as we are already in this activity
                return true
            }
            R.id.salesPersonProjectsMenu -> {
                startActivity(Intent(this, SalesPersonProjects::class.java))
                overridePendingTransition(0, 0)
                finish()
                return true
            }
            R.id.salesPersonProfileMenu -> {
                startActivity(Intent(this, SalesPersonProfile::class.java))
                overridePendingTransition(0, 0)
                finish()
                return true
            }
            R.id.salesPersonSettingsMenu -> {
                startActivity(Intent(this, SalesPersonMoreSettings::class.java))
                overridePendingTransition(0, 0)
                finish()
                return true
            }
            else -> return false
        }
    }


    private fun fetchClientDetails(clientId: String) {
        salesPersonCreateCustId.toString().uppercase()
        val clientRef = FirebaseDatabase.getInstance().getReference("users").orderByChild("clientId").equalTo(clientId)
        clientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val clientSnapshot = snapshot.children.first() // Assuming clientId is unique
                    val client = clientSnapshot.getValue(Client::class.java)
                    client?.let {
                        // Update UI fields with client details
                        salesPersonCreateCustFullname.setText(it.fullname)
                        salesPersonCreateCustEmail.setText(it.email)
                        salesPersonCreateCustAddress.setText(it.companyAddress)
                        salesPersonCreateCustMobileNo.setText(it.mobile)
                    }
                } else {
                    // Clear fields if no client found
                    clearClientDetails()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesPersonCreateOrder, "Failed to fetch client details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearClientDetails() {
        salesPersonCreateCustFullname.text = null
        salesPersonCreateCustEmail.text = null
        salesPersonCreateCustAddress.text = null
        salesPersonCreateCustMobileNo.text = null
    }


    private fun clearForm() {
        salesPersonCreateCustFullname.text = null
        salesPersonCreateCustMobileNo.text = null
        salesPersonCreateCustId.text = null
        salesPersonCreateCustEmail.text = null
        salesPersonCreateCompanyName.text = null
        salesPersonCreateCustAddress.text = null
        salesPersonCreateCustProductName.text = null
        salesPersonCreateCustOrderQuantity.text = null
        salesPersonCreateCustProductDesc.text = null
        salesPersonCreateCustOrderDate.text = null
        salesPersonCreateCustDueDate.text = null
        salesPersonCreateCustEstBuget.text = null
        salesPersonCreateCustModeOfPayment.text = null
        salesPersonCreatePaymentStatus.text = null
        salesPersonCreateCustUploadSampleImg.setImageDrawable(null)
    }

    private fun generateUniqueOrderId(): String {
        val random = Random()
        val orderId = String.format("%010d", random.nextInt(1000000000)) // Generates a 10-digit number
        return orderId
    }


    // Method to show the loading bar
    private fun showLoadingBar() {
        createOrderLodingBar.visibility = View.VISIBLE
    }

    // Method to hide the loading bar
    private fun hideLoadingBar() {
        createOrderLodingBar.visibility = View.GONE
    }
}
