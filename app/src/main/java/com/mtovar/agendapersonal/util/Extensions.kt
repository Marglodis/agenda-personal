package com.mtovar.agendapersonal.util

import android.widget.EditText

/**
 * Funciones de extensión para simplificar el manejo de los EditText
 */

//Para reutilizar lógica de validación nula y evitar repetición en la actividad.
fun EditText.safeText(): String = this.text?.toString() ?: ""

// Para manejo dde nulo
fun String?.orEmptyIfNull(): String = this ?: ""