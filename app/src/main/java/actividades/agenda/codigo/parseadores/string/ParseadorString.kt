package actividades.agenda.codigo.parseadores.string

import java.util.Locale

class ParseadorString {
    companion object {
        fun parseEnumText(text: String): String {
            return text.lowercase().replaceFirstChar {
                it.uppercase()
            }.replace("_", " ")
        }
    }
}