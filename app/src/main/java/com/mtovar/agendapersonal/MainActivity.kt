package com.mtovar.agendapersonal

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.mtovar.agendapersonal.databinding.ActivityMainBinding
import com.mtovar.agendapersonal.model.Event
import com.mtovar.agendapersonal.util.orEmptyIfNull
import com.mtovar.agendapersonal.util.safeText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // Usando lateinit. posponemos la incializacion hasata el Oncreate (ViewBinding)
    private lateinit var binding: ActivityMainBinding

    enum class DateFilter {
        ALL, TODAY, UPCOMING
    }

    private var currentFilter = DateFilter.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEventListeners()
        setupSearch()
        setupChipFilters()
        displayEvents()
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnClickListener {
            selectChip(binding.chipAll, DateFilter.ALL)
        }

        binding.chipToday.setOnClickListener {
            selectChip(binding.chipToday, DateFilter.TODAY)
        }

        binding.chipUpcoming.setOnClickListener {
            selectChip(binding.chipUpcoming, DateFilter.UPCOMING)
        }
    }

    private fun selectChip(selectedChip: Chip, filter: DateFilter) {
        // Desmarcar los chips existentes
        val chips = listOf(binding.chipAll, binding.chipToday, binding.chipUpcoming)
        chips.forEach { chip ->
            chip.isChecked = chip == selectedChip
        }
        // Actualizar el filtro actual
        currentFilter = filter
        // Mostrar eventos según el filtro seleccionado
        applyFilters()
    }

    private fun applyFilters() {
        val searchQuery = binding.etSearch.text?.toString().orEmptyIfNull()
        val events = if (searchQuery.isEmpty()) {
            EventManager.getSortedEvents()
        } else {
            EventManager.getEventsByTitle(searchQuery)
        }
        val filteredEvents = filterEventsByDate(events, currentFilter)
        /*//binding.eventsListContainer.removeAllViews()
        //if (filteredEvents.isEmpty()) {
            showEmptyList()
            return
        }*/
    displayFilteredEvents(filteredEvents, searchQuery)
    }

    private fun filterEventsByDate(events: List<Event>, filter: DateFilter): List<Event> {
        if (filter == DateFilter.ALL) return events

        val calendar = Calendar.getInstance()
        val today = calendar.time

        return events.filter { event ->
            val eventDate = parseDate(event.date) ?: return@filter false

            when (filter) {
                DateFilter.TODAY -> isSameDay(eventDate, today)
                DateFilter.UPCOMING -> eventDate.after(today)
                DateFilter.ALL -> true
            }
        }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            // Intenta varios formatos comunes
            // Según como está el codigo no necesitaria tantos formatos
            // más que el ultimo  pues la app utiliza un datepicker para poder ingresar la fecha
            val formats = listOf(
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "yyyy-MM-dd",
                "dd/MM/yy",
                "MMM dd, yyyy"
            )

            for (format in formats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    sdf.isLenient = false
                    return sdf.parse(dateString)
                } catch (e: Exception) {
                    continue
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    private fun setupSearch() {
        // Configurar búsqueda en tiempo real
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })

        // Botón para limpiar búsqueda
       /* binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }*/
    }

    private fun displayFilteredEvents(events: List<Event>,query: String) {
       // val filteredEvents = EventManager.getEventsByTitle(query)

        // Limpiar contenedor antes de añadir los filtrados
       binding.eventsListContainer.removeAllViews()

        if (events.isEmpty()) {
            showEmptySearchResults(query)
            return
        }
        binding.emptyStateContainer.visibility = View.GONE
        binding.eventsListContainer.visibility = View.VISIBLE

        events.forEach { event ->
            val eventView = createEventView(event)
            binding.eventsListContainer.addView(eventView)
        }
    }

    private fun showEmptySearchResults(searchQuery: String) {
        val filterText = when (currentFilter) {
            DateFilter.ALL -> ""
            DateFilter.TODAY -> "hoy"
            DateFilter.UPCOMING -> "próximos"
        }
        val message = if(searchQuery.isEmpty()) {
            "No se encontraron eventos $filterText"
        } else {
            "No se encontraron resultados para la búsqueda: $searchQuery"
        }

        // Actualizar el TextView del estado vacío
        binding.emptyStateTitle.text = message

        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.eventsListContainer.visibility = View.GONE
    }

    private fun displayEvents() {
        val events = EventManager.getSortedEvents()
        // Limpiar la lista actual
        binding.eventsListContainer.removeAllViews()
        if (events.isEmpty()) {
            showEmptyList()
            return
        }
           binding.emptyStateContainer.visibility = View.GONE
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

        // Click en el evento para expandir/colapsar
        eventView.setOnClickListener {
            showEventDetail(event)
        }
        //Botón quitar evento
        eventView.findViewById<View>(R.id.btnQuitarEvento).setOnClickListener {
            showRemoveConfirmation(event)
        }


        return eventView
    }

    @SuppressLint("SetTextI18n")
    private fun showEventDetail(event: Event) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_event_detail, null)
        //Configurar vistas del dialogo
        dialogView.findViewById<TextView>(R.id.tvDetailTitle).text = event.title
        dialogView.findViewById<TextView>(R.id.tvDetailDate).text = event.date

        val descripcionView = dialogView.findViewById<TextView>(R.id.tvDetailDescription)
        if (!event.description.isNullOrBlank()) {
            descripcionView.text = event.description
            descripcionView.visibility = View.VISIBLE
        } else {
            descripcionView.text = "No hay descripción"
            descripcionView.visibility = View.GONE
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Eliminar") {
                //cuando no se necesita usar una variable de un parámetro,
                //se puede reemplazar su nombre con un guion bajo _ para indicar
                //explícitamente "no me interesa este valor".
                // cuando se presione el botón negativo, ignora los parámetros,
                //y simplemente ejecuta showRemoveConfirmation(event)
                _, _ -> showRemoveConfirmation(event)
            }
            .create()
            .show()

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

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showDatePicker(){
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona la fecha del evento")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            binding.etDate.setText(datePicker.headerText)
        }
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun handleAddEvent() {

        val title = binding.etTitle.safeText()
        val description = binding.etDescription.text?.toString().orEmptyIfNull() // Uso de la extensión de String
            .takeIf { it.isNotBlank() } // Null Safety: puede ser null
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
        binding.emptyStateContainer.visibility = View.VISIBLE
        binding.eventsListContainer.visibility = View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}