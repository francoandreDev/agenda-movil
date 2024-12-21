package actividades.agenda.codigo.repositorios.dbAgenda

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(contexto: Context): SQLiteOpenHelper(
    contexto, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object{
        // sobre la bbdd
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "AgendaDB.db"
        // sobre la tabla Tarea
        const val TABLE_TAREA = "Tarea"
        const val COLUMN_TAREA_ID = "id"
        const val COLUMN_TAREA_NOMBRE = "nombre"
        const val COLUMN_TAREA_CATEGORIA = "categoria"
        const val COLUMN_TAREA_FECHA_INICIO = "fecha_inicio"
        const val COLUMN_TAREA_FECHA_FIN = "fecha_fin"
        const val COLUMN_TAREA_ESTADO = "estado"

    }

    override fun onCreate(db: SQLiteDatabase) {
        // crear estructura de las tablas
        val tablaTarea = (
            "CREATE TABLE $TABLE_TAREA ("
            + "$COLUMN_TAREA_ID INTEGER PRIMARY KEY,"
            + "$COLUMN_TAREA_NOMBRE TEXT,"
            + "$COLUMN_TAREA_CATEGORIA TEXT,"
            + "$COLUMN_TAREA_FECHA_INICIO INTEGER,"
            + "$COLUMN_TAREA_FECHA_FIN INTEGER,"
            + "$COLUMN_TAREA_ESTADO TEXT)"
        )

        // ejecutar creación de tablas
        db.execSQL(tablaTarea)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // eliminar tablas si existen cuando tengan que actualizarse
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAREA")

        // volver a crear tablas
        onCreate(db)
    }
}