package com.example.projectone.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.projectone.AboutUsActivity
import com.example.projectone.LoginScreen
import com.example.projectone.PrivacyPolicyActivity
import com.example.projectone.R
import com.example.projectone.TermsAndConditionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminMoreSettings : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var adminsettingsSwitchDarkMode:SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_more_settings)

        //
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()

        val toolbarAdminSettings=findViewById<Toolbar>(R.id.toolbar_Settings)
        adminsettingsSwitchDarkMode=findViewById(R.id.adminsettingsSwitchDarkMode)
        setSupportActionBar(toolbarAdminSettings)
        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle = "Settings"
        }



        val cardSettingsManufacturers=findViewById<CardView>(R.id.card_Settings_Manufacturer)
        val cardSettingsSalesPersons=findViewById<CardView>(R.id.card_Settings_SalesPersons)
        val cardSettingsHeads=findViewById<CardView>(R.id.card_Settings_Heads)
        val cardSettingsChangePassword=findViewById<CardView>(R.id.card_Settings_ChangePassword)
        val cardSettingsOrdersList=findViewById<CardView>(R.id.card_Settings_OrdersList)
        val cardSettingFeedbackList=findViewById<CardView>(R.id.card_Settings_FeedbackList)
        val cardSettingProjects=findViewById<CardView>(R.id.card_Settings_Projects)
        val cardSettingHistory=findViewById<CardView>(R.id.card_Settings_History)
        val cardSettingsClients=findViewById<CardView>(R.id.card_Settings_Clients)
        val cardSettingsPrivacyPolicy=findViewById<CardView>(R.id.card_Settings_PrivacyPolicy)
        val cardSettingsAboutus=findViewById<CardView>(R.id.card_Settings_AboutUs)
        val cardSettingsTandC=findViewById<CardView>(R.id.card_Settings_TermsandConds)


        cardSettingsManufacturers.setOnClickListener {
            startActivity(Intent(this,AdminManufacturersUserLists::class.java))
        }
        cardSettingsSalesPersons.setOnClickListener {
            startActivity(Intent(this,AdminSalesPersonsUsersLists::class.java))

        }
        cardSettingsHeads.setOnClickListener {
            startActivity(Intent(this,AdminHeadsUsersLists::class.java))

        }
        cardSettingsChangePassword.setOnClickListener {
            startActivity(Intent(this,AdminChangePassword::class.java))

        }
        cardSettingsOrdersList.setOnClickListener {
            startActivity(Intent(this,AdminOrdersList::class.java))

        }
        cardSettingFeedbackList.setOnClickListener {
            startActivity(Intent(this,AdminFeedbackLists::class.java))
        }
        cardSettingHistory.setOnClickListener {
            startActivity(Intent(this,AdminWorkHistory::class.java))
        }
        cardSettingProjects.setOnClickListener {
            startActivity(Intent(this,AdminProjects::class.java))
        }
        cardSettingsClients.setOnClickListener {
            startActivity(Intent(this,AdminClientsUsersLists::class.java))
        }
        cardSettingsPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        cardSettingsAboutus.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java))
        }
        cardSettingsTandC.setOnClickListener {
            startActivity(Intent(this,TermsAndConditionsActivity::class.java))
        }



//        val ivLogout=findViewById<ImageView>(R.id.ivLogout)
//        ivLogout.setOnClickListener {
//            Toast.makeText(this, "Logout Button Clicked...", Toast.LENGTH_SHORT).show()
//        }


        bottomNav = findViewById(R.id.adminSettingsBottomNav) // Corrected to use R.id.adminBottomNav

        // Set the default selected item
        bottomNav.selectedItemId = R.id.adminMoreMenu // Select the more options menu item


        bottomNav.setOnNavigationItemSelectedListener {menuItem ->

            handleNavigation(menuItem.itemId)
        }


    }
    //setting of top menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_top_menu,menu)
        return true
    }
    //onclick on item menu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu listners
        return when(item.itemId){
            R.id.menu_logout ->{
                auth.signOut()
                startActivity(Intent(this@AdminMoreSettings,LoginScreen::class.java))
                Toast.makeText(this@AdminMoreSettings, "Logged out!!!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun handleNavigation(itemId: Int): Boolean {
        // Check if the current activity is already the selected one
        if (itemId == bottomNav.selectedItemId) {
            return true
        }

        when (itemId) {
            R.id.adminOngoingProjectsMenu -> {
                startActivity(Intent(this, AdminOngoingProjects::class.java))
                overridePendingTransition(0,0) // Disable transition animation
                finish()
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
                // No action needed as we are already in this activity
                return true
            }
            else -> return false
        }
    }


}
