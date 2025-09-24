package com.mtovar.agendapersonal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mtovar.agendapersonal.databinding.ActivityMainBinding
import com.mtovar.agendapersonal.model.Event
import com.mtovar.agendapersonal.util.safeText

class MainActivity : AppCompatActivity() {
    // Usando lateinit. posponemos la incializacion hasata el Oncreate (ViewBinding)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Uso de lambda
        binding.btnAddEvent.setOnClickListener {
            val title = binding.etTitle.safeText()
            val description = binding.etDescription.text?.toString() // Null Safety: puede ser null
            val date = binding.etDate.safeText()

            // VAlidar campos
            if(title.isNotEmpty() && date.isNotEmpty()) {
                // Se crea el objeto Event
                val event = Event(title = title, description = description, date = date)
                // Se agrega al EventManager
                EventManager.addEvent(event)

                // Limpiar campos
                binding.etTitle.text?.clear()
                binding.etDescription.text?.clear()
                binding.etDate.text?.clear()

                // Actualizar la UI
                updateEventsList()

            }
        }
        updateEventsList()
    }

    private fun updateEventsList() {
        val events = EventManager.getSortedEvents()
        val eventsListContainer = binding.eventsListContainer
        eventsListContainer.removeAllViews()

        val eventToShow = events

        eventToShow.forEach { event ->
            val eventView = layoutInflater.inflate(R.layout.item_event, eventsListContainer, false)

            eventView.findViewById<TextView>(R.id.tvTitleEvent).apply {
                text = event.title
            }

            eventView.findViewById<TextView>(R.id.tvDate).apply {
                text = event.date
            }

            event.description?.let { description ->
                eventView.findViewById<TextView>(R.id.tvDescription).apply {
                    text = description
                    visibility = View.VISIBLE
                }
            }

            eventsListContainer.addView(eventView)

            }


    }
}