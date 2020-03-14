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

class VisitsAdapter(
    private var items: ArrayList<Visit>,
    private val context: Context,
    var mode: VisitType
) :
    RecyclerView.Adapter<VisitsAdapter.ViewHolder>() {

    fun invertMode() {
        mode = if (mode == VisitType.PATIENT) VisitType.PATIENT_VISITOR else VisitType.PATIENT
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: ArrayList<Visit>) {
        this.items.clear()
        this.items.addAll(items)
    }

    fun addItems(items: ArrayList<Visit>) {
        this.items.addAll(items)
    }

    fun clearItems() {
        items.clear()
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

        holder.txtVisitor.text = if (mode == VisitType.PATIENT_VISITOR) p.visitor["name"] else ""
        holder.txtPatient.text = p.patient["name"]

        holder.txtActions.text = p.actions
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.txtDate
        val txtYear: TextView = view.txtYear

        val txtPatient: TextView = view.txtPatient
        val txtVisitor: TextView = view.txtVisitor
        val txtActions: TextView = view.txtReference
    }

    enum class VisitType {
        PATIENT, PATIENT_VISITOR
    }
}