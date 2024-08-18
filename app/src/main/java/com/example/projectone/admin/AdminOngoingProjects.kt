package com.example.projectone.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.projectone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminOngoingProjects : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    private lateinit var cardAdminCreateOrder:CardView
    private lateinit var cardAdminPendingProjects:CardView
    private lateinit var cardAdminCompletedProjects:CardView
    private lateinit var cardAdminExploreProjects:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ongoing_projects)
        cardAdminCreateOrder=findViewById(R.id.cardAdminCreateOrder)
        cardAdminPendingProjects=findViewById(R.id.cardPendingProjects)
        cardAdminCompletedProjects=findViewById(R.id.cardCompletedProjects)
        cardAdminExploreProjects=findViewById(R.id.cardExploreProjects)

        val toolbarManufacturerInfo=findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_OngoingProjects)

        setSupportActionBar(toolbarManufacturerInfo)
        supportActionBar?.title=""
        supportActionBar?.apply {
            setSubtitle("Admin Home")
        }
        cardAdminCreateOrder.setOnClickListener {
            startActivity(Intent(this,AdminCreateOrderAccess::class.java))
        }
        cardAdminPendingProjects.setOnClickListener {
            startActivity(Intent(this,PendingProjects::class.java))
        }
        cardAdminCompletedProjects.setOnClickListener {
            startActivity(Intent(this,CompletedProjects::class.java))
        }
        cardAdminExploreProjects.setOnClickListener {
            startActivity(Intent(this,AdminProjects::class.java))
        }







        bottomNav = findViewById(R.id.adminBottomNav)

        // Set the default selected item
        bottomNav.selectedItemId = R.id.adminOngoingProjectsMenu

        bottomNav.setOnNavigationItemSelectedListener { menuItem -> //default is it changed to menuItem
            handleNavigation(menuItem.itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_notifications_top_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.adminNotificationsMenu->{
                startActivity(Intent(this,AdminNotifications::class.java))
                return true
            }
            else ->super.onOptionsItemSelected(item)

        }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        // Check if the current activity is already the selected one
        if (itemId == bottomNav.selectedItemId) {
            return true
        }

        when (itemId) {
            R.id.adminOngoingProjectsMenu -> {
                // No action needed as we are already in this activity
                return true
            }
            R.id.adminaddAddEmployeesMenu -> {
                startActivity(Intent(this, AdminAddEmployees::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminManufactringInfoMenu -> {
                startActivity(Intent(this, AdminManufacturerInfo::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminProfileMenu -> {
                startActivity(Intent(this, AdminProfile::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            R.id.adminMoreMenu -> {
                startActivity(Intent(this, AdminMoreSettings::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
                return true
            }
            else -> return false
        }
    }
}
