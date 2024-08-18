package com.example.projectone.head

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
import com.google.rpc.context.AttributeContext.Auth

class SalesHeadSettings : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var salesHeadSettingsBottom:BottomNavigationView
    private lateinit var cardSalesHeadGroups:CardView
    private lateinit var cardSalesHeadOrdersLists:CardView
    private lateinit var cardSalesHeadFeedback:CardView
    private lateinit var cardSalesHeadFeedbackLists:CardView
    private lateinit var cardSalesHeadWorkHistory:CardView
    private lateinit var cardSalesHeadAvailableStatus:CardView
    private lateinit var cardSalesHeadChangePassword:CardView
    private lateinit var cardSalesHeadPrivacypolicy:CardView
    private lateinit var cardSalesHeadTermsAndConditions:CardView
    private lateinit var cardSalesHeadAboutUs:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_settings)
        auth= FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        cardSalesHeadGroups=findViewById(R.id.cardSalesHeadGroups)
        cardSalesHeadOrdersLists=findViewById(R.id.card_SalesHead_Settings_OrdersLists)
        cardSalesHeadFeedback=findViewById(R.id.card_SalesHead_Settings_FeedbackForm)
        cardSalesHeadFeedbackLists=findViewById(R.id.card_SalesHead_Settings_FeedbackLists)
        cardSalesHeadWorkHistory=findViewById(R.id.card_SalesHead_Settings_WorkHistory)
        cardSalesHeadAvailableStatus=findViewById(R.id.card_SalesHead_Settings_status_mode)
        cardSalesHeadChangePassword=findViewById(R.id.card_SalesHead_Settings_ChangePassword)
        cardSalesHeadPrivacypolicy=findViewById(R.id.card_SalesHead_Settings_PrivacyPolicy)
        cardSalesHeadTermsAndConditions=findViewById(R.id.card_SalesHead_Settings_TandC)
        cardSalesHeadAboutUs=findViewById(R.id.card_SalesHead_Settings_Aboutus)

        cardSalesHeadGroups.setOnClickListener {
            startActivity(Intent(this,SalesHeadDisplayGroups::class.java))
        }
        cardSalesHeadFeedbackLists.setOnClickListener {
            startActivity(Intent(this,SalesHeadFeedbackLists::class.java))
        }
        cardSalesHeadOrdersLists.setOnClickListener {
            startActivity(Intent(this,SalesHeadOrdersLists::class.java))
        }
        cardSalesHeadFeedback.setOnClickListener {
            startActivity(Intent(this,SalesHeadFeedback::class.java))
        }
        cardSalesHeadWorkHistory.setOnClickListener {
            startActivity(Intent(this,SalesHeadWorkHistory::class.java))
        }
        cardSalesHeadChangePassword.setOnClickListener {
            startActivity(Intent(this,SalesHeadChangePassword::class.java))
        }
        cardSalesHeadAboutUs.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java))
        }
        cardSalesHeadPrivacypolicy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        cardSalesHeadTermsAndConditions.setOnClickListener {
            startActivity(Intent(this,TermsAndConditionsActivity::class.java))
        }

        val toolbarSalesHeadSettings=findViewById<Toolbar>(R.id.toolbarSalesHeadSettings)
        setSupportActionBar(toolbarSalesHeadSettings)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Settings"
        salesHeadSettingsBottom=findViewById(R.id.salesHeadSettingsBottom)
        salesHeadSettingsBottom.selectedItemId=R.id.salesHeadSettingsMenu

        salesHeadSettingsBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
            }
        }

    private fun handleNavigation(itemId: Int):Boolean {
        if (itemId==salesHeadSettingsBottom.selectedItemId){
            return true
        }
        when(itemId){
            R.id.salesHeadProfileMenu->{
                startActivity(Intent(this@SalesHeadSettings,SalesHeadProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesHeadProjectsMenu->{
                startActivity(Intent(this@SalesHeadSettings,SalesHeadProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesHeadSettingsMenu->{

                return true
            }
            else-> return false
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
                startActivity(Intent(this@SalesHeadSettings, LoginScreen::class.java))
                Toast.makeText(this@SalesHeadSettings, "Logged out!!!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
