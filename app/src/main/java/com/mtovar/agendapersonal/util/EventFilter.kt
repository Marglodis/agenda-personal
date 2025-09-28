package com.mtovar.agendapersonal.util

import com.mtovar.agendapersonal.model.Event
import java.util.Calendar

object EventFilter {

    enum class DateFilter {
        ALL, TODAY, UPCOMING
    }

    fun filterEventsByDate(events: List<Event>, filter: DateFilter): List<Event> {
        //     if (filter == DateFilter.ALL) return events

        val calendar = Calendar.getInstance()
        val today = calendar.time

        return events.filter { event ->
            val eventDate = DateUtils.parseDate(event.date) ?: return@filter false

            when (filter) {
                DateFilter.TODAY -> DateUtils.isSameDay(eventDate, today)
                DateFilter.UPCOMING -> eventDate.after(today)
                DateFilter.ALL -> true
            }
        }
    }
}