package com.example.projectone.head.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectone.R
import com.example.projectone.salesperson.salespersondatamodels.SalesPerson
import com.google.android.material.imageview.ShapeableImageView

class CreateSalesPersonGroupAdapter(
    context: Context,
    private val originalSalesPersonList: List<SalesPerson>
) : ArrayAdapter<SalesPerson>(context, 0, originalSalesPersonList), Filterable {

    private var filteredSalesPersonList = originalSalesPersonList
    private val salesPersonFilter = SalesPersonFilter()
    private val selectedSalesPersons = mutableSetOf<SalesPerson>()

    override fun getFilter(): Filter {
        return salesPersonFilter
    }

    override fun getCount(): Int {
        return filteredSalesPersonList.size
    }

    override fun getItem(position: Int): SalesPerson? {
        return filteredSalesPersonList.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.selectsalespersonstocreateinteam, parent, false)
        val salesPerson = getItem(position)
        salesPerson?.let { sp ->
            val salespersonProfileImage = view.findViewById<ShapeableImageView>(R.id.salesPersonsProfileImage)
            val salesPersonFullName = view.findViewById<TextView>(R.id.salespersonsName)
            val salesPersonRole = view.findViewById<TextView>(R.id.salespersonsRole)
            val salesPersonEmpId = view.findViewById<TextView>(R.id.salespersonsEmpId)
            val salesPersonEmail = view.findViewById<TextView>(R.id.salespersonsEmailId)
            val listItemContainer = view.findViewById<View>(R.id.listItemContainer) // Create this in XML

            salesPersonFullName.text = sp.fullname.ifEmpty { "No Full Name" }
            salesPersonRole.text = sp.role.ifEmpty { "No Role" }
            salesPersonEmpId.text = sp.empId.ifEmpty { "No Emp ID" }
            salesPersonEmail.text = sp.email.ifEmpty { "No Email" }

            // Load profile image
            Glide.with(context)
                .load(sp.profileImageUrl)
                .placeholder(R.drawable.sample_logo)
                .error(R.drawable.sample_logo)
                .into(salespersonProfileImage)

            // Handle item selection
            listItemContainer.setBackgroundColor(
                if (selectedSalesPersons.contains(sp)) {
                    ContextCompat.getColor(context, R.color.select_user_color) // Define this color in colors.xml
                } else {
                    ContextCompat.getColor(context, R.color.transparent) // Define this color in colors.xml
                }
            )

            listItemContainer.setOnClickListener {
                if (selectedSalesPersons.contains(sp)) {
                    selectedSalesPersons.remove(sp)
                } else {
                    if (selectedSalesPersons.size < 100) {
                        selectedSalesPersons.add(sp)
                    } else {
                        Toast.makeText(context, "You can select up to 100 salespersons only.", Toast.LENGTH_SHORT).show()
                    }
                }
                notifyDataSetChanged()
            }
        }
        return view
    }

    fun getSelectedSalesPersons(): List<SalesPerson> {
        return selectedSalesPersons.toList()
    }

    private inner class SalesPersonFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val query = constraint?.toString()?.lowercase() ?: ""
            val filteredList = if (query.isEmpty()) {
                originalSalesPersonList
            } else {
                originalSalesPersonList.filter {
                    it.fullname.lowercase().contains(query) || it.empId.lowercase().contains(query)
                }
            }
            filterResults.values = filteredList
            filterResults.count = filteredList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredSalesPersonList = results?.values as List<SalesPerson>? ?: emptyList()
            notifyDataSetChanged()
        }
    }
}
