package com.example.projectone.head

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.projectone.R

class SalesHeadWorkHistory : AppCompatActivity() {
    private lateinit var toolbarSalesHeadWorkHistory:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_head_work_history)
        toolbarSalesHeadWorkHistory=findViewById(R.id.toolbarSalesHeadWorkHistory)
        setSupportActionBar(toolbarSalesHeadWorkHistory)
        supportActionBar?.title=""
        supportActionBar?.subtitle="Work History"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}