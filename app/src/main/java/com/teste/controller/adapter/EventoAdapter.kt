package com.teste.controller.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teste.R
import com.teste.model.Evento
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import java.text.SimpleDateFormat


class EventoAdapter(val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>(){

    private var eventos : List<Evento> = listOf()
    @SuppressLint("SimpleDateFormat")
    val formatDate = SimpleDateFormat("dd/MM/yyyy HH:mm ")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return EventoViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        var currentItem = eventos[position]
        holder.txtTitle.text = currentItem.title
        holder.txtDescription.text = currentItem.description
        holder.txtDate.text = formatDate.format(currentItem.date)
    }

    override fun getItemCount() = eventos.size

    fun setEventoListItems(eventoList: List<Evento>){
        this.eventos = eventoList;
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Evento{
        return eventos.get(position)
    }

    inner class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val txtTitle : TextView = itemView.txt_title_item_holder;
        val txtDescription : TextView = itemView.txt_subtitle_item_holder;
        val txtDate : TextView = itemView.txt_date_main;

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}