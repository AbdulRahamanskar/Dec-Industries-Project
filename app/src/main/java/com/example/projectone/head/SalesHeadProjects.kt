package com.example.projectone.head


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SalesHeadProjects : AppCompatActivity() {
    private lateinit var salesHeadProjectsBottom:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_projects)
        val toolbarSalesHeadProjects=findViewById<Toolbar>(R.id.toolbarSalesHeadProjects)
        setSupportActionBar(toolbarSalesHeadProjects)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Projects"

         salesHeadProjectsBottom=findViewById(R.id.salesHeadProjectsBottom)
        salesHeadProjectsBottom.selectedItemId=R.id.salesHeadProjectsMenu
        salesHeadProjectsBottom.setOnNavigationItemSelectedListener {
            handleNavigation(it.itemId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.saleshead_top_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.salesHeadNotificationsMenu->{
                startActivity(Intent(this@SalesHeadProjects,SalesHeadNotifications::class.java))
            }
            R.id.salesHeadAddTeamsMenu->{
                startActivity(Intent(this@SalesHeadProjects,SalesHeadCreateSalesPersonTeam::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleNavigation(itemId: Int):Boolean {
        if (itemId==salesHeadProjectsBottom.selectedItemId) {
            return true
        }
        when(itemId){
            R.id.salesHeadProjectsMenu->{
                return true
            }
            R.id.salesHeadProfileMenu->{
                startActivity(Intent(this@SalesHeadProjects,SalesHeadProfile::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            R.id.salesHeadSettingsMenu->{
                startActivity(Intent(this@SalesHeadProjects,SalesHeadSettings::class.java))
                overridePendingTransition(0,0)
                finish()
                return true
            }
            else->return false
        }
        }

    }
