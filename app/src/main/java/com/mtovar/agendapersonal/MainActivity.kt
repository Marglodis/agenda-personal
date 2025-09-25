package com.mtovar.agendapersonal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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

        setupEventListeners()
        displayEvents()
    }

    private fun displayEvents() {
        val events = EventManager.getSortedEvents()
        // Limpiar la lista actual
        binding.eventsListContainer.removeAllViews()
        if (events.isEmpty()) {
            showEmptyList()
            return
        }
           binding.tvEmptyState.visibility = View.GONE
           binding.eventsListContainer.visibility = View.VISIBLE
        events.forEach { event ->
            val eventView = createEventView(event)
            binding.eventsListContainer.addView(eventView)
        }
    }

    private fun createEventView(event: Event): View {
        val eventView = layoutInflater.inflate(
            R.layout.item_event,
            binding.eventsListContainer, false
        )

        // Configurar la vista del evento
        eventView.findViewById<TextView>(R.id.tvTitleEvent).text = event.title
        eventView.findViewById<TextView>(R.id.tvDate).text = event.date

        eventView.findViewById<TextView>(R.id.tvDescription).apply {
            if (!event.description.isNullOrBlank()) {
                text = event.description
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
        //Botón quitar evento
        eventView.findViewById<View>(R.id.btnQuitarEvento).setOnClickListener {
            showRemoveConfirmation(event)
        }
        return eventView
    }

    private fun showRemoveConfirmation(event: Event) {
        Snackbar.make(
            binding.root,
            "Desea eliminar el evento '${event.title}'?",
            Snackbar.LENGTH_LONG
        )
            .setAction("Eliminar") {
                removeEvent(event)
            }.show()
    }

    private fun removeEvent(event: Event) {
        EventManager.removeEvent(event)
        displayEvents()
        showSuccess("Evento eliminado")
    }

    private fun setupEventListeners() {
        binding.btnAddEvent.setOnClickListener {
            handleAddEvent()
        }
    }

    private fun handleAddEvent() {

        val title = binding.etTitle.safeText()
        val description = binding.etDescription.text?.toString()
            ?.takeIf { it.isNotBlank() } // Null Safety: puede ser null
        val date = binding.etDate.safeText()

        // Validación mejorada con feedback a usuario
        when {
            title.isEmpty() -> {
                showError("El título es obligatorio")
                binding.etTitle.requestFocus()
                return
            }

            date.isEmpty() -> {
                showError("La fecha es obligatoria")
                binding.etDate.requestFocus()
                return
            }
        }
        // Crear y agregar evento
        val event = Event(title = title, description = description, date = date)
        EventManager.addEvent(event)

        // Limpiar campos y actualizar vista
        clearFields()
        displayEvents()

        // Mostrar mensaje de éxito
        showSuccess("Evento agregado exitosamente")
    }

    private fun clearFields(){
        binding.apply {
            etTitle.text?.clear()
            etDescription.text?.clear()
            etDate.text?.clear()
            etTitle.requestFocus() // Enfoca el cursor en el título
        }
    }
    private fun showEmptyList() {
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.eventsListContainer.visibility = View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



