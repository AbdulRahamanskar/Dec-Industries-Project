package com.example.projectone.manufacturer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.projectone.AboutUsActivity
import com.example.projectone.LoginScreen
import com.example.projectone.PrivacyPolicyActivity
import com.example.projectone.R
import com.example.projectone.TermsAndConditionsActivity
import com.example.projectone.salesperson.SalesPersonChangePassword
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ManufacturerSettings : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var manufacturerSettingsBottom:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manufacturer_settings)
        auth= FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        val toolbarManufacturerSettings=findViewById<Toolbar>(R.id.toolbarManufacturerSettings)
        setSupportActionBar(toolbarManufacturerSettings)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Settings"

        //find views
        val manufacturingChangeOnlineStatus=findViewById<CardView>(R.id.card_Manufacturing_Settings_status_mode)
        val manufacturingAddTeam=findViewById<CardView>(R.id.card_Manufacturing_Settings_AddTeam)
        val manufacturingFeedback=findViewById<CardView>(R.id.card_Manufacturing_Settings_FeedbackForm)
        val manufacturingFeedbackLists=findViewById<CardView>(R.id.card_Manufacturing_Settings_FeedbackLists)
        val manufacturingWorkHistory=findViewById<CardView>(R.id.card_Manufacturing_Settings_WorkHistory)
        val manufacturingOrderLists=findViewById<CardView>(R.id.card_Manufacturing_Settings_OrdersLists)
        val manufacturingChangePassword=findViewById<CardView>(R.id.card_Manufacturing_Settings_ChangePassword)
        val manufacturingTeamLists=findViewById<CardView>(R.id.card_Manufacturing_Settings_TeamLists)
        val manufacturingPrivacyandPolicy=findViewById<CardView>(R.id.card_Manufacturing_Settings_PrivacyPolicy)
        val manufacturingTermsAndConditions=findViewById<CardView>(R.id.card_Manufacturing_Settings_TandC)
        val manufacturingAboutUs=findViewById<CardView>(R.id.card_Manufacturing_Settings_Aboutus)

        manufacturingChangeOnlineStatus.setOnClickListener {
            Toast.makeText(this, "Available status clicked...", Toast.LENGTH_SHORT).show()
        }
        manufacturingChangePassword.setOnClickListener {
            startActivity(Intent(this, ManufacturerChangePassword::class.java))
        }
        manufacturingAddTeam.setOnClickListener {
            startActivity(Intent(this, ManufacturingAddTeam::class.java))
        }
        manufacturingFeedback.setOnClickListener {
            startActivity(Intent(this, ManufacturingFeedback::class.java))
        }
        manufacturingFeedbackLists.setOnClickListener {
            startActivity(Intent(this, ManufacturingFeedbackLists::class.java))
        }
        manufacturingWorkHistory.setOnClickListener {
            startActivity(Intent(this, ManufacturingHistory::class.java))
        }
        manufacturingOrderLists.setOnClickListener {
            startActivity(Intent(this, ManufacturingOrderList::class.java))
        }
        manufacturingTeamLists.setOnClickListener {
            startActivity(Intent(this, ManufacturingTeamList::class.java))
        }
        manufacturingPrivacyandPolicy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        manufacturingTermsAndConditions.setOnClickListener {
            startActivity(Intent(this, TermsAndConditionsActivity::class.java))
        }
        manufacturingAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        manufacturerSettingsBottom=findViewById(R.id.manufacturerSettingsBottom)
        manufacturerSettingsBottom.selectedItemId=R.id.manufacturerSettingsMenu
        manufacturerSettingsBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
    }

    private fun handleNavigation(itemId: Int):Boolean {

        if (itemId==manufacturerSettingsBottom.selectedItemId){
            return true
        }

        when(itemId){
            R.id.manufacturerSettingsMenu->{
                return true
            }
            R.id.manufacturerProjectsMenu->{
                startActivity(Intent(this@ManufacturerSettings,ManufacturerProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }

            R.id.manufacturerProfileMenu->{
                startActivity(Intent(this@ManufacturerSettings,ManufacturerProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
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
                startActivity(Intent(this@ManufacturerSettings, LoginScreen::class.java))
                Toast.makeText(this@ManufacturerSettings, "Logged out!!!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}