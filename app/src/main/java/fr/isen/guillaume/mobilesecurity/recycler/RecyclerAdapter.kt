package fr.isen.guillaume.mobilesecurity.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.guillaume.mobilesecurity.R
import fr.isen.guillaume.mobilesecurity.model.Pending
import fr.isen.guillaume.mobilesecurity.recycler.PendingViewHolder
import java.util.ArrayList

class RecyclerAdapter(private val pending: ArrayList<Pending>, private val context: Context, private val pendingMode: Boolean) : RecyclerView.Adapter<PendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        return PendingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recyclerview_pending,
                parent,
                false
            ), context, pendingMode
        )
    }

    override fun getItemCount() = pending.size

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        holder.bindPending(pending[position])
    }
}