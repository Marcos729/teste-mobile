package com.teste.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.teste.R
import com.teste.controller.network.NetworkUtils
import com.teste.controller.service.EventoEndpoint
import com.teste.model.Checkin
import com.teste.model.Evento
import kotlinx.android.synthetic.main.activity_detail_event.*
import kotlinx.android.synthetic.main.activity_detail_event.toolbar_detail
import kotlinx.android.synthetic.main.checkin_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.sql.DataSource

class DetailEventActivity : AppCompatActivity() {

    @SuppressLint("SimpleDateFormat")
    val formatDate = SimpleDateFormat("dd/MM/yyyy HH:mm ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)
        setSupportActionBar(toolbar_detail)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        var idEvento = intent.getIntExtra("idEvento", -1)

        getData(idEvento = idEvento)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.shared, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.bt_shared == item.itemId) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    txt_title.text.toString() + " " + txt_descricao.text.toString() + " " + txt_date.text.toString()
                )
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } else if (R.id.bt_add == item.itemId) {
            val checkinDialog = LayoutInflater.from(this).inflate(R.layout.checkin_dialog, null)
            val builder = AlertDialog.Builder(this)
                .setView(checkinDialog)

            val alertDialog = builder.show()
            alertDialog.setCancelable(false)

            alertDialog.alert_nome.requestFocus()
            showKeyboard()

            alertDialog.bt_enviar.setOnClickListener {
                if (alertDialog.alert_nome.text.isEmpty()) {
                    alertDialog.alert_nome.setError("Informe um nome")
                } else if (alertDialog.alert_email.text.isEmpty()) {
                    alertDialog.alert_email.setError("Informe um E-mail")
                }else if (!Patterns.EMAIL_ADDRESS.matcher(alertDialog.alert_email.text).matches()){
                    alertDialog.alert_email.setError("Informe um E-mail válido")

                }else{
                    alertDialog.pBar_dialog.isVisible = true
                    val s = Checkin(alertDialog.alert_nome.text.toString(), alertDialog.alert_email.text.toString(), intent.getIntExtra("idEvento", -1))

                    val retrofitClient = NetworkUtils
                        .getRetrofitInstance()

                    val endpoint = retrofitClient.create(EventoEndpoint::class.java)
                    val callback = endpoint.getCheckinEvento(s)

                    callback.enqueue(object : Callback<Checkin> {
                        override fun onFailure(call: Call<Checkin>, t: Throwable) {
                            Toast.makeText(applicationContext, "Não foi possível receber sua inscrição", Toast.LENGTH_SHORT).show()
                            alertDialog.pBar_dialog.isVisible = false
                        }

                        override fun onResponse(call: Call<Checkin>, response: Response<Checkin>) {
                            alertDialog.pBar_dialog.isVisible = false

                            Toast.makeText(applicationContext, "Recebemos sua inscrição", Toast.LENGTH_SHORT).show()
                            hideKeyboard()
                            alertDialog.dismiss()

                        }
                    })
                }

            }

            alertDialog.bt_cancelar.setOnClickListener {
                hideKeyboard()
                alertDialog.dismiss()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getData(idEvento: Int) {
        ln_detail.isVisible = false
        pBar_detail.isVisible = true

        val retrofitClient = NetworkUtils
            .getRetrofitInstance()

        val endpoint = retrofitClient.create(EventoEndpoint::class.java)
        val callback = endpoint.getEvento(idEvento)

        callback.enqueue(object : Callback<Evento> {
            override fun onFailure(call: Call<Evento>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                pBar_detail.isVisible = false
                ln_detail.isVisible = false

            }

            override fun onResponse(call: Call<Evento>, response: Response<Evento>) {

                val evento = response.body()!!

                Glide.with(applicationContext)
                    .load(evento.image)
                    .error(R.drawable.not_found)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_princ)


                txt_title.text = evento.title
                txt_date.text = formatDate.format(evento.date)
                txt_descricao.text = evento.description

                pBar_detail.isVisible = false
                ln_detail.isVisible = true

            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun showKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
