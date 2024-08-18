package com.example.projectone.head.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.head.salesheaddatamodels.SalesHead

// Adapter for sales users list inside admin adapter
class AdminSalesHeadAdapter(
    private val context: Context,  // Ensure this is private and does not conflict
    private val originalSalesHeadList: List<SalesHead>
) : ArrayAdapter<SalesHead>(context, 0, originalSalesHeadList), Filterable {

    init {
        // Clear Glide's memory and disk cache
        Glide.get(context).clearMemory()
        Thread {
            Glide.get(context).clearDiskCache()
        }.start()
    }

    private var filteredSalesHeadList = originalSalesHeadList
    private val salesHeadFilter = SalesHeadFilter()

    override fun getFilter(): Filter {
        return salesHeadFilter
    }

    override fun getCount(): Int {
        return filteredSalesHeadList.size
    }

    override fun getItem(position: Int): SalesHead? {
        return filteredSalesHeadList.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.adminheadsuserslists, parent, false)
        val salesHead = getItem(position)
        salesHead?.let {
            val salesHeadFullname = view.findViewById<TextView>(R.id.adminSalesHeadFullName)
            val salesHeadEmpId = view.findViewById<TextView>(R.id.adminSalesHeadEmpId)
            val salesHeadRole = view.findViewById<TextView>(R.id.adminSalesHeadRole)
            val salesHeadProfileImage = view.findViewById<ImageView>(R.id.adminSalesHeadProfileImage) // Ensure this is an ImageView
            val salesHeadEmail = view.findViewById<TextView>(R.id.adminSalesHeadEmail) // Ensure this is an ImageView
            val salesHeadDoj = view.findViewById<TextView>(R.id.adminSalesHeadDoj) // Ensure this is an ImageView

            salesHeadFullname.text = "Name: ${it.fullname}".ifEmpty { "No Name" }
            salesHeadRole.text = "Role: ${it.role}".ifEmpty { "No Role" }
            salesHeadEmpId.text = "EmpId: ${it.empId}".ifEmpty { "No EmpId" }
            salesHeadEmail.text = "Email: ${it.email}".ifEmpty { "No Email" }
            salesHeadDoj.text = "Joined: ${it.doj}".ifEmpty { "No DOJ" }

            // Load profile image
            Glide.get(context).clearMemory()
            if (it.profileImageUrl?.isNotEmpty() == true) {
                Glide.with(context)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.sample_logo) // Placeholder image if URL is empty or loading
                    .error(R.drawable.sample_logo) // Error image if loading fails
                    .into(salesHeadProfileImage as ImageView)
            } else {
                salesHeadProfileImage.setImageResource(R.drawable.sample_logo) // Use a default image if URL is empty
            }
        }
        return view
    }

    private inner class SalesHeadFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            val query = constraint?.toString()?.lowercase() ?: ""
            val filteredList = if (query.isEmpty()) {
                originalSalesHeadList
            } else {
                originalSalesHeadList.filter {
                    it.fullname.lowercase().contains(query) || it.empId.lowercase().contains(query)
                }
            }

            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredSalesHeadList = results?.values as List<SalesHead>? ?: emptyList()
            notifyDataSetChanged()
        }
    }
}
