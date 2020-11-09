package com.teste

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teste.controller.adapter.EventoAdapter
import com.teste.controller.network.NetworkUtils
import com.teste.controller.service.EventoEndpoint
import com.teste.model.Evento
import com.teste.view.DetailEventActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), EventoAdapter.OnItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: EventoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_home)
        recyclerAdapter = EventoAdapter(this, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter

        getData()
        bt_nova_tentativa.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                responseSuccess()
                getData()
            }})
    }

    fun getData(): List<Evento> {
        pBar.isVisible = true
        val list: ArrayList<Evento> = ArrayList()

        val retrofitClient = NetworkUtils
            .getRetrofitInstance()

        val endpoint = retrofitClient.create(EventoEndpoint::class.java)
        val callback = endpoint.getEventos()

        callback.enqueue(object : Callback<List<Evento>> {
            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                responseError()
            }

            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                responseSuccess()
                recyclerAdapter.setEventoListItems(response.body()!!)
            }
        })

        return list
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, DetailEventActivity::class.java)
        val params = Bundle()
        params.putInt("idEvento", recyclerAdapter.getItem(position).id)
        intent.putExtras(params)
        startActivity(intent)
    }

    fun responseSuccess(){
        recyclerView.isVisible = true
        txt_error.isVisible = false
        bt_nova_tentativa.isVisible = false
        pBar.isVisible = false

    }

    fun responseError(){
        recyclerView.isVisible = false
        txt_error.isVisible = true
        bt_nova_tentativa.isVisible = true
        pBar.isVisible = false
    }
}
