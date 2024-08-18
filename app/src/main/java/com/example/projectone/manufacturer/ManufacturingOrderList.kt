package com.example.projectone.manufacturer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class ManufacturingOrderList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manufacturing_order_list)

        val toolbarManufacturerOrderLists=findViewById<Toolbar>(R.id.toolbarManufacturerOrderLists)
        setSupportActionBar(toolbarManufacturerOrderLists)
        supportActionBar?.title=""
        supportActionBar?.apply {
            supportActionBar?.subtitle="Orders Lists"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)

        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}