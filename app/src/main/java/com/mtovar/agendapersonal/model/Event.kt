package com.mtovar.agendapersonal.model

data class Event(
    val id: String = EventIdGenerator.generateEventId(), // Genera un ID único para el evento desde una clase Java
    val title: String,
    val description: String?,
    val date: String
)
