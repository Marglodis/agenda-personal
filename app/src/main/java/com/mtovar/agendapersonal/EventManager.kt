package com.mtovar.agendapersonal

import com.mtovar.agendapersonal.model.Event
import com.mtovar.agendapersonal.util.orEmptyIfNull

/***
 * Clase para gestionar eventos de forma centralizada para luego mostrarlos en la UI
 * Este es el COmpanion Object aqui usado como object
 */
object EventManager {
    private val events = mutableListOf<Event>()

    fun addEvent(event: Event) {
        events.add(event)
    }
    fun removeEvent(event: Event) {
        events.remove(event)
    }

    fun getAllEvents() = events.toList()

    fun getSortedEvents() = events.sortedBy { it.date }

    fun getEventsByTitle(query: String) =
        events.filter { it.title.contains(query.orEmptyIfNull(), ignoreCase = true) }
}