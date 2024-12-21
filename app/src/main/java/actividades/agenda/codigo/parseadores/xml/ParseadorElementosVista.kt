package actividades.agenda.codigo.parseadores.xml

import android.text.Editable

class ParseadorElementosVista {
    companion object {
        fun textoToEditable(text: String): Editable {
            return Editable.Factory.getInstance().newEditable(text)
        }
    }
}