package com.mtovar.agendapersonal.util

import android.widget.Toast
import android.content.Context
import android.widget.Toast.LENGTH_SHORT

object UIHelpers {
    fun showError(context: Context, message: String) {
        Toast.makeText(context, message, LENGTH_SHORT).show()
    }

    fun showSuccess(context: Context, message: String) {
        Toast.makeText(context, message, LENGTH_SHORT).show()
    }
}