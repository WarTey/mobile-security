package fr.isen.guillaume.mobilesecurity.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.guillaume.mobilesecurity.R
import fr.isen.guillaume.mobilesecurity.model.Patient
import kotlinx.android.synthetic.main.recyclerview_patients.view.*

class PatientsAdapter(private val items: ArrayList<Patient>, private val context: Context) :
    RecyclerView.Adapter<PatientsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: ArrayList<Patient>) {
        this.items.clear()
        this.items.addAll(items)
    }

    fun addItem(item: Patient?) {
        if (item != null)
            items.add(item)
    }

    fun addItems(items: ArrayList<Patient>) {
        this.items.addAll(items)
    }

    fun changeItem(item: Patient?) {
        if (item != null) {
            items.forEachIndexed { index, visit ->
                if (visit.id == item.id) {
                    items[index] = item
                    return
                }
            }
        }
    }

    fun removeItem(id: String) {
        items.forEachIndexed { index, visit ->
            if (visit.id == id) {
                items.removeAt(index)
                return
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recyclerview_patients, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]

        holder.txtFirstname.text = p.firstname
        holder.txtLastname.text = p.lastname

        if (p.picture != null)
            holder.imgPatient.setImageURI(p.picture)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFirstname: TextView = view.txtFirstname
        val txtLastname: TextView = view.txtLastname
        val imgPatient: ImageView = view.imgPatient as ImageView
    }
}