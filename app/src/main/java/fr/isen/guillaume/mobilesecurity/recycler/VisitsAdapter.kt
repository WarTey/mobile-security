package fr.isen.guillaume.mobilesecurity.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.guillaume.mobilesecurity.R
import fr.isen.guillaume.mobilesecurity.misc.FormattedTime
import fr.isen.guillaume.mobilesecurity.model.Visit
import kotlinx.android.synthetic.main.recyclerview_visits.view.*
import java.util.*

class VisitsAdapter(private var items: ArrayList<Visit>, private val context: Context) :
    RecyclerView.Adapter<VisitsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: ArrayList<Visit>) {
        this.items.clear()
        this.items.addAll(items)
    }

    fun addItem(item: Visit?) {
        if (item != null)
            items.add(item)
    }

    fun addItems(items: ArrayList<Visit>) {
        this.items.addAll(items)
    }

    fun changeItem(item: Visit?) {
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
            LayoutInflater.from(context).inflate(R.layout.recyclerview_visits, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]

        holder.txtDate.text = FormattedTime.dayMonth(p.millis)
        holder.txtYear.text = FormattedTime.year(p.millis)

        holder.txtVisitor.text = p.patient.
        holder.txtActions.text = p.actions
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.txtDate
        val txtYear: TextView = view.txtYear

        val txtVisitor: TextView = view.txtVisitor
        val txtActions: TextView = view.txtActions
    }
}