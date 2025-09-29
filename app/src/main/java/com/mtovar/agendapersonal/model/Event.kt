package com.mtovar.agendapersonal.model

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val date: String
) {
    companion object {
        fun create(title: String, description: String?, date: String): Event {
            val id = EventIdGenerator.generateEventId() // Genera un ID único para el evento desde una clase Java
            return Event(id, title, description, date)
        }
    }
}
