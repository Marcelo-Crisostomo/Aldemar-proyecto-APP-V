package cl.duoc.aquasample.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))

fun formatearDecimal(valor: Double): String = String.format(Locale.US, "%.1f", valor)
