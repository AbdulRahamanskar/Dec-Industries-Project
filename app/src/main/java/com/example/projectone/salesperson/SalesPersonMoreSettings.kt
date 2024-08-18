package com.example.projectone.salesperson

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.projectone.AboutUsActivity
import com.example.projectone.LoginScreen
import com.example.projectone.PrivacyPolicyActivity
import com.example.projectone.R
import com.example.projectone.TermsAndConditionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SalesPersonMoreSettings : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var salesPersonBottomSettings: BottomNavigationView
    private lateinit var salesPersonChangeOnlineStatus:BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_person_more_settings)
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        //card settings sales
        val salesPersonChangeOnlineStatus=findViewById<CardView>(R.id.card_Sales_Settings_status_mode)
        val salesPersonChangePassword=findViewById<CardView>(R.id.card_Sales_Settings_ChangePassword)
        val salesPersonsSettingsOrderLists=findViewById<CardView>(R.id.card_SalesPerson_Settings_OrdersLists)
        val salesPersonsSettingsFeedback=findViewById<CardView>(R.id.card_SalesPerson_Settings_FeedbackForm)
        val salesPersonsSettingsFeedBackLists=findViewById<CardView>(R.id.card_SalesPerson_Settings_FeedbackLists)
        val salesPersonsSettingsSalesTarget=findViewById<CardView>(R.id.card_SalesPerson_Settings_SalesTarget)
        val salesPersonsSettingsWorkHistory=findViewById<CardView>(R.id.card_SalesPerson_Settings_WorkHistory)
        val salesPersonsSettingsPrivacyPolicy=findViewById<CardView>(R.id.card_SalesPerson_Settings_PrivacyPolicy)
        val salesPersonsSettingsAboutUs=findViewById<CardView>(R.id.card_SalesPerson_Settings_Aboutus)
        val salesPersonsSettingsTandC=findViewById<CardView>(R.id.card_SalesPerson_Settings_TandC)

        //settings card view listnerns
        salesPersonChangeOnlineStatus.setOnClickListener {
            showOnlineStatusBottomSheet();

        }
        salesPersonChangePassword.setOnClickListener {
            startActivity(Intent(this,SalesPersonChangePassword::class.java))
        }

        salesPersonsSettingsOrderLists.setOnClickListener {
            startActivity(Intent(this,SalesPersonOrderList::class.java))
        }
        salesPersonsSettingsFeedback.setOnClickListener {
            startActivity(Intent(this,SalesPersonFeedbackForm::class.java))
        }
        salesPersonsSettingsFeedBackLists.setOnClickListener {
            startActivity(Intent(this,SalesPersonFeedbackLists::class.java))
        }
        salesPersonsSettingsSalesTarget.setOnClickListener {
            startActivity(Intent(this,SalesPersonSalesTarget::class.java))
        }
        salesPersonsSettingsWorkHistory.setOnClickListener {
            startActivity(Intent(this,SalesPersonHistory::class.java))
        }
        salesPersonsSettingsPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this,PrivacyPolicyActivity::class.java))
        }
        salesPersonsSettingsAboutUs.setOnClickListener {
            startActivity(Intent(this,AboutUsActivity::class.java))
        }
        salesPersonsSettingsTandC.setOnClickListener {
            startActivity(Intent(this, TermsAndConditionsActivity::class.java))
        }

        // toolbar settings

        val toolbarSalesPersonMoreSettings=findViewById<Toolbar>(R.id.toolbarSalesPersonMoreSettings)
        setSupportActionBar(toolbarSalesPersonMoreSettings)
        supportActionBar?.title=""
        supportActionBar?.apply {
            subtitle="Settings"
        }
        salesPersonBottomSettings=findViewById(R.id.salesPersonBottom)
        salesPersonBottomSettings.selectedItemId=R.id.salesPersonSettingsMenu

        salesPersonBottomSettings.setOnNavigationItemSelectedListener {
            navigateToScreen(it.itemId)
        }
    }



    fun navigateToScreen(itemId: Int):Boolean {
        if (itemId==salesPersonBottomSettings.selectedItemId){
            return true
        }

        when(itemId){
            R.id.salesPersonProfileMenu->{
                startActivity(Intent(this,SalesPersonProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonProjectsMenu->{
                startActivity(Intent(this,SalesPersonProjects::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonCreateOrderMenu->{
                startActivity(Intent(this,SalesPersonCreateOrder::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonProfileMenu->{
                startActivity(Intent(this,SalesPersonProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesPersonSettingsMenu->{

                return true
            }
            else->return false
        }



    }
    private fun showOnlineStatusBottomSheet() {
        val view = layoutInflater.inflate(R.layout.salespersonchangestatusbottomsheet, null)
        val dialogSheets = BottomSheetDialog(this)

        val online = view.findViewById<TextView>(R.id.tvSalesPersonOnline)
        val offline = view.findViewById<TextView>(R.id.tvSalesPersonOffline)
        val brb = view.findViewById<TextView>(R.id.tvSalesPersonBRB)
        val breaks = view.findViewById<TextView>(R.id.tvSalesPersonBreak)

        // Set click listeners for the buttons
        online.setOnClickListener {
            updateStatus("Online")
            Toast.makeText(this, "Online selected check in profile...", Toast.LENGTH_SHORT).show()
            dialogSheets.dismiss()

        }
        offline.setOnClickListener {
            updateStatus("Offline")
            Toast.makeText(this, "Offline selected check in profile...", Toast.LENGTH_SHORT).show()
            dialogSheets.dismiss()

        }
        brb.setOnClickListener {
            updateStatus("BRB")
            Toast.makeText(this, "BRB selected check in profile...", Toast.LENGTH_SHORT).show()
            dialogSheets.dismiss()

        }
        breaks.setOnClickListener {
            updateStatus("Break")
            Toast.makeText(this, "Break selected check in profile...", Toast.LENGTH_SHORT).show()
            dialogSheets.dismiss()

        }
//        dialogSheets.setCancelable(false)
        dialogSheets.setContentView(view)
        dialogSheets.show()
    }

    private fun updateStatus(status: String) {
        // Save status in SharedPreferences
        val sharedPreferences = getSharedPreferences("SalesPersonPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("STATUS", status)
        editor.apply()

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
                startActivity(Intent(this@SalesPersonMoreSettings, LoginScreen::class.java))
                Toast.makeText(this@SalesPersonMoreSettings, "Logged out!!!", Toast.LENGTH_SHORT).show()
                finish()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}