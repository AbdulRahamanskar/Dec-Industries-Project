package com.example.projectone.client


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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ClientSettings : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var databse:FirebaseDatabase
    private lateinit var clientSettingsBottom:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_settings)
        //auth and database
        auth= FirebaseAuth.getInstance()
        databse= FirebaseDatabase.getInstance()
        clientSettingsBottom=findViewById(R.id.clientSettingsBottom)
        val cardClientSettingsOrdersLists=findViewById<CardView>(R.id.card_Client_Settings_OrdersLists)
        val cardClientSettingsFeedbackForm=findViewById<CardView>(R.id.card_Client_Settings_FeedbackForm)
        val cardClient_SettingsFeedbackLists=findViewById<CardView>(R.id.card_Client_Settings_FeedbackLists)
        val cardClientSettingsWorkHistory=findViewById<CardView>(R.id.card_Client_Settings_WorkHistory)
        val cardClientSettingsChangePassword=findViewById<CardView>(R.id.card_Client_Settings_ChangePassword)
        val cardClientSettingsPrivacyPolicy=findViewById<CardView>(R.id.card_Client_Settings_PrivacyPolicy)
        val cardClientSettingsTandC=findViewById<CardView>(R.id.card_Client_Settings_TandC)
        val cardClientSettingsAboutUs=findViewById<CardView>(R.id.card_Client_Settings_Aboutus)

        cardClientSettingsOrdersLists.setOnClickListener {
            startActivity(Intent(this,ClientOrderList::class.java))
        }
        cardClientSettingsWorkHistory.setOnClickListener {
            startActivity(Intent(this,ClientHistory::class.java))
        }
        cardClientSettingsFeedbackForm.setOnClickListener {
            startActivity(Intent(this,ClientFeedbackForm::class.java))
        }
        cardClient_SettingsFeedbackLists.setOnClickListener {
            startActivity(Intent(this,ClientFeedbackLists::class.java))
        }
        cardClientSettingsChangePassword.setOnClickListener {
            startActivity(Intent(this,ClientChangePassword::class.java))
        }
        cardClientSettingsTandC.setOnClickListener {
            startActivity(Intent(this,TermsAndConditionsActivity::class.java))
        }
        cardClientSettingsPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        cardClientSettingsAboutUs.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java))
        }

        clientSettingsBottom.selectedItemId=R.id.clientSettingsMenu

        val toolbarClientSettings=findViewById<Toolbar>(R.id.toolbarClientSettings)
        setSupportActionBar(toolbarClientSettings)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.subtitle="Settings"
        }
        clientSettingsBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
    }

    private fun handleNavigation(itemId: Int):Boolean {

        if (itemId==clientSettingsBottom.selectedItemId){
            return true
        }
        when(itemId){
            R.id.clientSettingsMenu->{
                return true

            }
            R.id.clientProfileMenu->{
                startActivity(Intent(this@ClientSettings,ClientProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
        }
            R.id.clientProjectsMenu->{
                startActivity(Intent(this@ClientSettings,ClientProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_top_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout->{
                auth.signOut()
                startActivity(Intent(this@ClientSettings,LoginScreen::class.java))
                Toast.makeText(this@ClientSettings, "Logged out!!!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}