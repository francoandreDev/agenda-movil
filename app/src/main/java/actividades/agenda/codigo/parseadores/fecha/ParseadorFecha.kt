package actividades.agenda.codigo.parseadores.fecha

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ParseadorFecha {
    companion object {
        private val formatoEn = SimpleDateFormat(
            "EEE MMM dd HH:mm:ss zzz yyyy",
            Locale.ENGLISH
        )
        private val formatoEs = SimpleDateFormat(
            "dd/MM/yyyy - HH:mm:ss",
            Locale("es", "PE")
        )

        fun fechaATextoEs(fecha: Date): String {
            return formatoEs.format(fecha)
        }

        fun textoEnAFecha(texto: String): Date {
            return formatoEn.parse(texto)!!
        }

        fun textoEsAFecha(texto: String): Date {
            return formatoEs.parse(texto)!!
        }

        fun obtenerFechaActual(): Date {
            val zonaHorariaLima = TimeZone.getTimeZone("GMT-5")
            val calendario = Calendar.getInstance(zonaHorariaLima)
            return calendario.time
        }
    }
}
